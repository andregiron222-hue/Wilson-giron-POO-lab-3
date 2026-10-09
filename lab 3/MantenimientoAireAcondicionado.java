public class MantenimientoAireAcondicionado extends OrdenServicio {
    private final int capacidadBTU;

    public MantenimientoAireAcondicionado(
            String codigo,
            String cliente,
            String direccion,
            double horasEstimadas,
            Zona zona,
            int capacidadBTU) {

        super(codigo, cliente, direccion, horasEstimadas, zona);

        if (zona != Zona.METROPOLITANA)
            throw new IllegalArgumentException(
                    "El mantenimiento solo admite zona metropolitana."
            );

        if (capacidadBTU < 9000 || capacidadBTU > 60000)
            throw new IllegalArgumentException(
                    "Los BTU deben estar entre 9000 y 60000."
            );

        this.capacidadBTU = capacidadBTU;
    }

    public int getCapacidadBTU() {
        return capacidadBTU;
    }

    @Override
    public double calcularCosto() {
        return dinero(
                tarifaBaseZona()
                + 110 * getHorasEstimadas()
                + 80
        );
    }

    @Override
    public int estimarDiasAtencion() {
        return 2;
    }

    @Override
    public int getMaxVisitas() {
        return 1;
    }

    @Override
    public String getCategoria() {
        return "Mantenimiento de aire acondicionado";
    }

    @Override
    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return cuadrilla != null
                && cuadrilla.isCertificadaRefrigeracion();
    }

    @Override
    public String toString() {
        return super.toString() + " | BTU: " + capacidadBTU;
    }
}