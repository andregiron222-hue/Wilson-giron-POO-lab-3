public class Diagnostico extends OrdenServicio {

    public Diagnostico(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);

        if (horasEstimadas > 1.5)
            throw new IllegalArgumentException(
                    "Un diagnostico no puede superar 1.5 horas."
            );
    }

    @Override
    public double calcularCosto() {
        return getZona() == Zona.METROPOLITANA ? 150 : 225;
    }

    @Override
    public int estimarDiasAtencion() {
        return getZona() == Zona.METROPOLITANA ? 1 : 2;
    }

    @Override
    public int getMaxVisitas() {
        return 3;
    }

    @Override
    public String getCategoria() {
        return "Diagnostico";
    }
}