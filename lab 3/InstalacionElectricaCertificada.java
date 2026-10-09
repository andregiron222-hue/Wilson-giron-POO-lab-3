public class InstalacionElectricaCertificada extends Instalacion {
    private final double valorMateriales;

    public InstalacionElectricaCertificada(
            String codigo,
            String cliente,
            String direccion,
            double horasEstimadas,
            Zona zona,
            int cantidadPuntos,
            double valorMateriales) {

        super(
                codigo, cliente, direccion,
                horasEstimadas, zona, cantidadPuntos
        );

        validarPositivo(valorMateriales, "Valor de materiales");
        this.valorMateriales = valorMateriales;
    }

    public double getValorMateriales() {
        return valorMateriales;
    }

    @Override
    public double calcularCosto() {
        return dinero(
                super.calcularCosto()
                + 120
                + valorMateriales * 0.05
        );
    }

    @Override
    public int estimarDiasAtencion() {
        return super.estimarDiasAtencion() + 2;
    }

    @Override
    public int getMaxVisitas() {
        return 2;
    }

    @Override
    public String getCategoria() {
        return "Instalacion electrica certificada";
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(
                        " | Materiales: Q%.2f",
                        valorMateriales
                );
    }
}