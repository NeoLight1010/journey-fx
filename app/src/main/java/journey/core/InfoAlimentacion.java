package journey.core;

import java.util.HashMap;

/**
 * Contiene la información de las varias "comidas" del día (desayuno, almuerzo,
 * merienda).
 *
 * Cada comida contiene la cantidad de porciones consumidas de cada alimento,
 * almacenadas en un {@link HashMap}.
 */
public class InfoAlimentacion {

    private HashMap<Alimento, Integer> desayuno = new HashMap<Alimento, Integer>();
    private HashMap<Alimento, Integer> almuerzo = new HashMap<Alimento, Integer>();
    private HashMap<Alimento, Integer> merienda = new HashMap<Alimento, Integer>();

    public InfoAlimentacion() {}

    public InfoAlimentacion(HashMap<Alimento, Integer> desayuno, HashMap<Alimento, Integer> almuerzo,
            HashMap<Alimento, Integer> merienda) {
        this.desayuno = desayuno;
        this.almuerzo = almuerzo;
        this.merienda = merienda;
    }

    public float caloriasTotales() {
        float resultado = 0;

        for (var alimento : desayuno.keySet()) {
            int porciones = desayuno.get(alimento);

            resultado += alimento.getCaloriasPorPorcion() * porciones;
        }

        for (var alimento : almuerzo.keySet()) {
            int porciones = almuerzo.get(alimento);

            resultado += alimento.getCaloriasPorPorcion() * porciones;
        }

        for (var alimento : merienda.keySet()) {
            int porciones = merienda.get(alimento);

            resultado += alimento.getCaloriasPorPorcion() * porciones;
        }

        return resultado;
    }

    public String diagnostico(float minimoRecomendado, float maximoRecomendado) {
        var caloriasTotales = this.caloriasTotales();

        if (caloriasTotales < minimoRecomendado) {
            return "Tienes una alimentación desbalanceada. Procura consumir más calorías.";
        }
        if (caloriasTotales > maximoRecomendado) {
            return "Tienes una alimentación desbalanceada. Procura consumir menos calorías.";
        }

        return "¡Felicidades! Tu alimentación está dentro del rango de consumo de calorías recomendadas.";
    }

    public void agregarADesayuno(Alimento alimento, int porciones) {
        this.desayuno.put(alimento, porciones);
    }

    public void agregarAAlmuerzo(Alimento alimento, int porciones) {
        this.almuerzo.put(alimento, porciones);
    }

    public void agregarAMerienda(Alimento alimento, int porciones) {
        this.merienda.put(alimento, porciones);
    }

    // Getters and setters
    public HashMap<Alimento, Integer> getDesayuno() {
        return desayuno;
    }

    public HashMap<Alimento, Integer> getAlmuerzo() {
        return almuerzo;
    }

    public HashMap<Alimento, Integer> getMerienda() {
        return merienda;
    }
}
