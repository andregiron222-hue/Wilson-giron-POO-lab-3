public class Instalacion extends OrdenServicio {
    private final int cantidadPuntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona,
                       int cantidadPuntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);

        if (cantidadPuntos <= 0)
            throw new IllegalArgumentException(
                    "Los puntos deben ser enteros positivos."
            );

        this.cantidadPuntos = cantidadPuntos;
    }

    public int getCantidadPuntos() {
        return cantidadPuntos;
    }

    public double calcularHorasFacturables() {
        return Math.max(
                getHorasEstimadas(),
                cantidadPuntos * 0.75
        );
    }

    @Override
    public double calcularCosto() {
        return dinero(
                tarifaBaseZona()
                + 90 * Math.ceil(calcularHorasFacturables())
        );
    }

    @Override
    public int estimarDiasAtencion() {
        return getZona() == Zona.METROPOLITANA ? 3 : 5;
    }

    @Override
    public int getMaxVisitas() {
        return 3;
    }

    @Override
    public String getCategoria() {
        return "Instalacion";
    }

    @Override
    public String toString() {
        return super.toString() + " | Puntos: " + cantidadPuntos;
    }
}