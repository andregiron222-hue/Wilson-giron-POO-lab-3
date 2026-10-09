public class Cuadrilla {
    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;

    public Cuadrilla(String codigo, double capacidadHoras,
                     boolean certificadaRefrigeracion) {
        this.codigo = OrdenServicio.textoValido(
                codigo, "Codigo de cuadrilla"
        );

        OrdenServicio.validarPositivo(
                capacidadHoras, "Capacidad de horas"
        );

        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadHoras() {
        return capacidadHoras;
    }

    public boolean isCertificadaRefrigeracion() {
        return certificadaRefrigeracion;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Capacidad: %.2f h | Refrigeracion: %s",
                codigo,
                capacidadHoras,
                certificadaRefrigeracion ? "Si" : "No"
        );
    }
}