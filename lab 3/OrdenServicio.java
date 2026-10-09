import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class OrdenServicio {
    private final String codigo, cliente, direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    private int visitasRealizadas;
    private double ingresoRegistro, ingresoVisitas;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            double horasEstimadas, Zona zona) {
        this.codigo = textoValido(codigo, "Codigo");
        this.cliente = textoValido(cliente, "Cliente");
        this.direccion = textoValido(direccion, "Direccion");

        validarPositivo(horasEstimadas, "Horas estimadas");

        if (zona == null)
            throw new IllegalArgumentException("Seleccione una zona.");

        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
    }

    static String textoValido(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            throw new IllegalArgumentException(
                    campo + " no puede estar vacio."
            );

        return valor.trim();
    }

    static void validarPositivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0)
            throw new IllegalArgumentException(
                    campo + " debe ser positivo y finito."
            );
    }

    protected static double dinero(double valor) {
        if (!Double.isFinite(valor) || valor < 0)
            throw new IllegalArgumentException(
                    "Monto fuera del rango permitido."
            );

        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    protected double tarifaBaseZona() {
        return zona == Zona.METROPOLITANA ? 100 : 175;
    }

    public abstract double calcularCosto();

    public abstract int estimarDiasAtencion();

    public abstract int getMaxVisitas();

    public abstract String getCategoria();

    public boolean esCompatibleCon(Cuadrilla cuadrilla) {
        return cuadrilla != null;
    }

    void asignar() {
        exigirEstado(EstadoOrden.PENDIENTE);

        if (visitasRealizadas >= getMaxVisitas())
            throw new IllegalStateException(
                    "La orden agoto sus visitas."
            );

        estado = EstadoOrden.ASIGNADA;
    }

    void retirar() {
        exigirEstado(EstadoOrden.ASIGNADA);
        estado = EstadoOrden.PENDIENTE;
    }

    void validarInicioVisita() {
        exigirEstado(EstadoOrden.ASIGNADA);

        if (visitasRealizadas >= getMaxVisitas())
            throw new IllegalStateException(
                    "La orden agoto sus visitas."
            );
    }

    void iniciarVisita() {
        validarInicioVisita();

        if (visitasRealizadas > 0)
            ingresoVisitas += 40;

        visitasRealizadas++;
        estado = EstadoOrden.EN_ATENCION;
    }

    void registrarResultado(ResultadoVisita resultado) {
        exigirEstado(EstadoOrden.EN_ATENCION);

        if (resultado == null)
            throw new IllegalArgumentException("Resultado requerido.");

        if (resultado == ResultadoVisita.COMPLETADA) {
            estado = EstadoOrden.COMPLETADA;
        } else {
            estado = visitasRealizadas >= getMaxVisitas()
                    ? EstadoOrden.CANCELADA
                    : EstadoOrden.PENDIENTE;
        }
    }

    void cobrarRegistro() {
        exigirEstado(EstadoOrden.PENDIENTE);

        if (ingresoRegistro != 0 || visitasRealizadas != 0)
            throw new IllegalStateException(
                    "La orden no es nueva o ya fue cobrada."
            );

        ingresoRegistro = dinero(calcularCosto());
    }

    private void exigirEstado(EstadoOrden esperado) {
        if (estado != esperado)
            throw new IllegalStateException(
                    "Orden " + codigo + ": se requiere " + esperado
                    + ", pero su estado es " + estado + "."
            );
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitasRealizadas() {
        return visitasRealizadas;
    }

    public double getIngresoRegistro() {
        return ingresoRegistro;
    }

    public double getIngresoVisitas() {
        return ingresoVisitas;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | %s | Cliente: %s | Direccion: %s | %s | %.2f h"
                + " | %s | Visitas: %d/%d"
                + " | Registro: Q%.2f | Extras: Q%.2f",
                codigo, getCategoria(), cliente, direccion,
                zona, horasEstimadas, estado,
                visitasRealizadas, getMaxVisitas(),
                ingresoRegistro, ingresoVisitas
        );
    }
}