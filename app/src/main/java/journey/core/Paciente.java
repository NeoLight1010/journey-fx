package journey.core;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
/**
 * Esta clase contiene la información y acciones relacionadas al paciente
 * @author Grupo 23
 * @version 31/01/2022
 */
public class Paciente {
    // Atributos

    /**
     * Constante que define el mínimo de entradas para conocer la cantidad de ejercicio semanal.
     * Sirve para calcular el factor de actividad.
     */
    final static int MINIMO_ENTRADAS_CALCULO_EJERCICIO = 5;
    /**
     * Constante que define lo que se le resta y suma a las calorías diarias recomendadas para obtener un rango.
     */
    final static int ERROR_CALORIAS_RECOM = 100;

    /**
     * Nombre de usuario del paciente; sirve para registrarse e iniciar sesión.
     */
    private String username;
    /**
     * Contraseña del paciente; sirve para registrarse e iniciar sesión.
     */
    private String password;
    /**
     * Primer nombre del paciente.
     */
    private String primerNombre;
    /**
     * Apellido del paciente.
     */
    private String apellido;
    /**
     * Fecha de nacimiento del paciente.
     */
    private LocalDate fechaNacimiento;
    /**
     * Sexo del paciente.
     */
    private Sexo sexo;
    /**
     * Número de contacto del paciente.
     */
    private String numeroContacto;
    /**
     * Ocupación del paciente.
     */
    private String ocupacion;

    /**
     * Sorted set de InfoDia, permite iterar sobre los elementos de información diaria ingresada en un orden natural
     */
    public SortedSet<InfoDia> infoDiaria = new TreeSet<>(Comparator.comparing(InfoDia::getFecha));

    /**
     * Constructor para Paciente
     * @param username
     * @param password
     * @param primerNombre
     * @param apellido
     * @param fechaNacimiento
     * @param sexo
     * @param numeroContacto
     * @param ocupacion
     */
    public Paciente(String username, String password, String primerNombre, String apellido, LocalDate fechaNacimiento,
            Sexo sexo, String numeroContacto, String ocupacion) {
        this.username = username;
        this.password = password;
        this.primerNombre = primerNombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.numeroContacto = numeroContacto;
        this.ocupacion = ocupacion;
    }

    // Métodos públicos

    /**
     * Método para buscar un infoDia dad una fecha específica
     * @param fecha
     * @return si se encuentra, infoDia; si no se encuentra, null
     */
    public InfoDia buscarInfoDiaPorFecha(LocalDate fecha) {
        InfoDia encontrado = null;

        for (var infoDia : this.infoDiaria) {
            if (infoDia.getFecha().equals(fecha)) {
                encontrado = infoDia;
            }
        }

        return encontrado;
    }

    /**
     * Método para agregar información a infoDia
     * @param infoDia
     */
    public void agregarInfoDia(InfoDia infoDia) {
        this.infoDiaria.add(infoDia);
    }

    /**
     * Método para buscar el diagnóstico de IMC del paciente dada una fecha en específico
     * @param fecha
     * @return cadena de caracteres con el diagnóstico de IMC
     */
    public String buscarDiagnosticoIMCPorDia(LocalDate fecha) {
        return this.buscarInfoDiaPorFecha(fecha).diagnosticoIMC();
    }

    /**
     * Método para unir el primer nombre y el apellido del paciente
     * @return cadena de caracteres con el nombre completo del paciente
     */
    public String nombreCompleto() {
        return this.getPrimerNombre() + " " + this.getApellido();
    }

    /**
     * Método para calcular la edad del paciente, basado en la fecha de nacimiento y la fecha actual
     * @return edad
     */
    public int calcularEdad() {
        return (int) ChronoUnit.YEARS.between(this.getFechaNacimiento(), LocalDate.now());
    }

    /**
     * Método para calcular el factor de actividad, definido según la cantidad de veces promedio de ejercicio semanal
     * Si el número de entradas en el diario es menor a MINIMO_ENTRADAS_CALCULO_EJERCICIO,
     * se asume que el factor de actividad es el mínimo (1.2).
     * @return factor de actividad
     */
    public float calcularFactorActividad() {
        // TODO: calcular de acuerdo a un infoDia específico.
        if (this.infoDiaria.size() < MINIMO_ENTRADAS_CALCULO_EJERCICIO) {
            return 1.2f;
        }

        int vecesEjercicioSemana = this.vecesEjercicioPorSemana();

        if (vecesEjercicioSemana == 0)
            return 1.2f;

        if (vecesEjercicioSemana <= 3)
            return 1.375f; 

        if (vecesEjercicioSemana <= 5)
            return 1.55f;

        else
            return 1.725f;
    }

    /**
     * Método para calcular la media de consumo de calorías diarias ideales de acuerdo a las fórmulas de Harris-Bennedict.
     * Toma en cuenta el peso, la altura, el sexo, la edad y el factor de actividad.
     * @return media de cantidad de calorías diarias ideales
     */
    public float idealCaloriasDiariasMedia(InfoDia infoDia) {
        var peso = infoDia.getPeso();
        var altura = infoDia.getAltura();

        if (this.getSexo() == Sexo.MASCULINO) {
            return (66 + (13.7f * peso)) + ((5f * altura)   - (6.8f * calcularEdad())) * calcularFactorActividad();
        }
        return (655 + (9.6f * peso)) + ((1.8f * altura) - (4.7f * calcularEdad())) * calcularFactorActividad();
    }

    public float idealCaloriasDiariasMedia() {
        return idealCaloriasDiariasMedia(this.getInfoDiaMasReciente());
    }

    /**
     * Método para calcular el mínimo de consumo de calorías diarias ideales con la información del día (última añadida)
     * Se basa en el método idealCaloriasDiariasMedia() y le resta 100.
     * @return mínimo de cantidad de calorías diarias ideales
     */
    public float idealCaloriasDiariasMinimo(InfoDia infoDia) {
        return idealCaloriasDiariasMedia(infoDia) - ERROR_CALORIAS_RECOM;
    }

    public float idealCaloriasDiariasMinimo() {
        return idealCaloriasDiariasMedia() - ERROR_CALORIAS_RECOM;
    }

    /**
     * Método para calcular el máximo de consumo de calorías diarias ideales
     * Se basa en el método idealCaloriasDiariasMedia() y le suma 100
     * @return máximo de cantidad de calorías diarias ideales
     */
    public float idealCaloriasDiariasMaximo(InfoDia infoDia) {
        return this.idealCaloriasDiariasMedia(infoDia) + ERROR_CALORIAS_RECOM;
    }
    public float idealCaloriasDiariasMaximo() {
        return this.idealCaloriasDiariasMedia() + ERROR_CALORIAS_RECOM;
    }

    /**
     * Método que devuelve el rango de consumo de calorías diarias ideales, une el mínimo y máximo de consumo.
     * @return cadena de caracteres con el rango de consumo de calorías diarias ideales
     */
    public String rangoIdealCalorias(InfoDia infoDia) {
        return "[" + idealCaloriasDiariasMinimo(infoDia) + ", " + idealCaloriasDiariasMaximo(infoDia) + "]";
    }

    /**
     * Método que devuelve el rango de consumo de calorías diarias ideales con la información del día (última añadida)
     * @return cadena de caracteres con el rango de consumo de calorías diarias ideales
     */
    public String rangoIdealCalorias() {
        return "[" + idealCaloriasDiariasMinimo() + ", " + idealCaloriasDiariasMaximo() + "]";
    }

    /**
     * Método que calcula un promedio de las veces que se ha hecho ejercicio por semana
     * @return promedio de ejercicio semanal del paciente
     */
    public int vecesEjercicioPorSemana() {
        // Calcular cantidad de días que ha hecho ejercicio.
        int diasHechoEjercicio = 0;
        for (var infoDia : this.infoDiaria) {
            if (infoDia.getInfoEjercicio().getTiempo() > 0) {
                diasHechoEjercicio += 1;
            }
        }

        // Cantidad de días entre el primer y el úlitmo entry del journal.
        LocalDate primerDia = this.infoDiaria.first().getFecha();
        LocalDate ultimoDia = this.infoDiaria.last().getFecha();
        long rangoDiasDiario = ChronoUnit.DAYS.between(primerDia, ultimoDia);

        // Promedio de ejercicio por día
        float vecesEjercicioPorDia = (float) diasHechoEjercicio / rangoDiasDiario;
        // 2.46
        // Promedio de ejercicio por semana
        return (int) Math.ceil(vecesEjercicioPorDia * 7);
    }

    /**
     * Método que encuentra la información diaria ingresada más reciente
     * @return infoDia más reciente
     */
    public InfoDia getInfoDiaMasReciente() {
        return this.infoDiaria.last();
    }

    // Getters and setters

    /**
     * Método que devuelve el nombre de usuario
     * @return username
     */

    public String getUsername() {
        return username;
    }

    /**
     * Método que agrega el nombre de usuario
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Método que devuelve la contraseña
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Método que agrega la contraseña
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Método que devuelve el primer nombre
     * @return primerNombre
     */
    public String getPrimerNombre() {
        return primerNombre;
    }

    /**
     * Método que agrega el primer nombre
     * @param primerNombre
     */
    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    /**
     * Método que devuelve el apellido
     * @return apellido
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Método que agrega el apellido
     * @param apellido
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Método que devuelve la fecha de nacimiento
     * @return fechaNacimiento
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Método que devuelve el sexo
     * @return sexo
     */
    public Sexo getSexo() {
        return sexo;
    }

    /**
     * Método que agrega el sexo
     * @param sexo
     */
    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    /**
     * Método que devuelve el número de contacto
     * @return numeroContacto
     */
    public String getNumeroContacto() {
        return numeroContacto;
    }

    /**
     * Método que agrega el número de contacto
     * @param numeroContacto
     */
    public void setNumeroContacto(String numeroContacto) {
        this.numeroContacto = numeroContacto;
    }

    /**
     * Método que devuelve la ocupación
     * @return ocupacion
     */
    public String getOcupacion() {
        return ocupacion;
    }

    /**
     * Método que agrega la ocupación
     * @param ocupacion
     */
    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }
}
