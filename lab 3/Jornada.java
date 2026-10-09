import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class Jornada {
    private final String codigo;
    private final Cuadrilla cuadrilla;

    private EstadoJornada estado = EstadoJornada.EN_PREPARACION;

    private final ArrayList<OrdenServicio> ordenes = new ArrayList<>();

    private final HashMap<String, ResultadoVisita> resultados =
            new HashMap<>();

    public Jornada(String codigo, Cuadrilla cuadrilla) {
        this.codigo = OrdenServicio.textoValido(
                codigo, "Codigo de jornada"
        );

        if (cuadrilla == null)
            throw new IllegalArgumentException("Cuadrilla requerida.");

        this.cuadrilla = cuadrilla;
    }

    void asignarOrden(OrdenServicio orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION);

        if (orden == null)
            throw new IllegalArgumentException("Orden requerida.");

        if (contieneOrden(orden.getCodigo()))
            throw new IllegalArgumentException(
                    "La orden ya pertenece a esta jornada."
            );

        if (orden.getEstado() != EstadoOrden.PENDIENTE)
            throw new IllegalStateException(
                    "Solo se asignan ordenes pendientes."
            );

        if (!orden.esCompatibleCon(cuadrilla))
            throw new IllegalArgumentException(
                    "La orden no es compatible con la cuadrilla."
            );

        BigDecimal suma = sumarHoras().add(
                BigDecimal.valueOf(orden.getHorasEstimadas())
        );

        if (suma.compareTo(
                BigDecimal.valueOf(cuadrilla.getCapacidadHoras())
        ) > 0) {
            throw new IllegalArgumentException(
                    "La asignacion supera la capacidad de la cuadrilla."
            );
        }

        orden.asignar();
        ordenes.add(orden);
    }

    void retirarOrden(String codigoOrden) {
        exigirEstado(EstadoJornada.EN_PREPARACION);

        OrdenServicio orden = buscarOrden(codigoOrden);

        orden.retirar();
        ordenes.remove(orden);
    }

    void iniciar() {
        exigirEstado(EstadoJornada.EN_PREPARACION);

        if (ordenes.isEmpty())
            throw new IllegalStateException(
                    "No puede iniciar una jornada vacia."
            );

        // Validar todo antes de cambiar estados o registrar cargos.
        for (OrdenServicio orden : ordenes)
            orden.validarInicioVisita();

        for (OrdenServicio orden : ordenes)
            orden.iniciarVisita();

        estado = EstadoJornada.EN_CURSO;
    }

    void registrarResultado(String codigoOrden,
                            ResultadoVisita resultado) {
        exigirEstado(EstadoJornada.EN_CURSO);

        OrdenServicio orden = buscarOrden(codigoOrden);

        if (resultados.containsKey(orden.getCodigo()))
            throw new IllegalStateException(
                    "Esta visita ya tiene resultado."
            );

        orden.registrarResultado(resultado);
        resultados.put(orden.getCodigo(), resultado);

        if (resultados.size() == ordenes.size())
            estado = EstadoJornada.CERRADA;
    }

    private OrdenServicio buscarOrden(String codigoOrden) {
        String buscado = OrdenServicio.textoValido(
                codigoOrden, "Codigo de orden"
        );

        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equalsIgnoreCase(buscado))
                return orden;
        }

        throw new IllegalArgumentException(
                "La orden no pertenece a esta jornada."
        );
    }

    private void exigirEstado(EstadoJornada esperado) {
        if (estado != esperado)
            throw new IllegalStateException(
                    "La jornada debe estar " + esperado + "."
            );
    }

    private BigDecimal sumarHoras() {
        BigDecimal suma = BigDecimal.ZERO;

        for (OrdenServicio orden : ordenes) {
            suma = suma.add(
                    BigDecimal.valueOf(orden.getHorasEstimadas())
            );
        }

        return suma;
    }

    public boolean contieneOrden(String codigoOrden) {
        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equalsIgnoreCase(codigoOrden))
                return true;
        }

        return false;
    }

    public double getHorasAsignadas() {
        return sumarHoras().doubleValue();
    }

    public double getCapacidadDisponible() {
        return BigDecimal.valueOf(cuadrilla.getCapacidadHoras())
                .subtract(sumarHoras())
                .doubleValue();
    }

    public String getCodigo() {
        return codigo;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public ArrayList<OrdenServicio> getOrdenes() {
        return new ArrayList<>(ordenes);
    }

    public HashMap<String, ResultadoVisita> getResultados() {
        return new HashMap<>(resultados);
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Cuadrilla: %s | %s"
                + " | Asignadas: %.2f h | Disponibles: %.2f h",
                codigo,
                cuadrilla.getCodigo(),
                estado,
                getHorasAsignadas(),
                getCapacidadDisponible()
        );
    }
}