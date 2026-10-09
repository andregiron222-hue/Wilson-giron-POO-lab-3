import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SistemaTecniHogar {
    private final ArrayList<OrdenServicio> ordenes = new ArrayList<>();
    private final ArrayList<Cuadrilla> cuadrillas = new ArrayList<>();
    private final ArrayList<Jornada> jornadas = new ArrayList<>();

    public SistemaTecniHogar() {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        Zona m = Zona.METROPOLITANA;
        Zona p = Zona.PERIFERICA;

        // Precarga: no llamar cobrarRegistro().
        // Todos los ingresos comienzan en cero.
        ordenes.add(new Diagnostico(
                "D1", "Ana", "Zona 1", 1, m
        ));

        ordenes.add(new Diagnostico(
                "D2", "Luis", "Mixco", 1.5, p
        ));

        ordenes.add(new Instalacion(
                "I1", "Maria", "Zona 5", 2.5, m, 5
        ));

        ordenes.add(new Instalacion(
                "I2", "Pedro", "Villa Nueva", 2.2, p, 1
        ));

        ordenes.add(new InstalacionElectricaCertificada(
                "E1", "Sofia", "Zona 10", 2.5, m, 5, 2000
        ));

        ordenes.add(new InstalacionElectricaCertificada(
                "E2", "Carlos", "Fraijanes", 2, p, 2, 1000
        ));

        ordenes.add(new MantenimientoAireAcondicionado(
                "A1", "Elena", "Zona 14", 1.5, m, 9000
        ));

        ordenes.add(new MantenimientoAireAcondicionado(
                "A2", "Mario", "Zona 4", 2, m, 60000
        ));

        cuadrillas.add(new Cuadrilla("C1", 8, true));
        cuadrillas.add(new Cuadrilla("C2", 6, false));
        cuadrillas.add(new Cuadrilla("C3", 4, false));
    }

    public String cotizar(OrdenServicio propuesta) {
        if (propuesta == null)
            throw new IllegalArgumentException("Orden requerida.");

        return String.format(
                "%s | Costo: Q%.2f | Atencion: %d dias habiles",
                propuesta.getCategoria(),
                propuesta.calcularCosto(),
                propuesta.estimarDiasAtencion()
        );
    }

    public void registrarOrden(OrdenServicio orden) {
        if (orden == null)
            throw new IllegalArgumentException("Orden requerida.");

        for (OrdenServicio existente : ordenes) {
            if (existente.getCodigo().equalsIgnoreCase(orden.getCodigo()))
                throw new IllegalArgumentException(
                        "Codigo de orden repetido."
                );
        }

        // El cobro valida estado, visitas, monto y cobros anteriores.
        orden.cobrarRegistro();
        ordenes.add(orden);
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null)
            throw new IllegalArgumentException("Cuadrilla requerida.");

        for (Cuadrilla existente : cuadrillas) {
            if (existente.getCodigo().equalsIgnoreCase(cuadrilla.getCodigo()))
                throw new IllegalArgumentException(
                        "Codigo de cuadrilla repetido."
                );
        }

        cuadrillas.add(cuadrilla);
    }

    public void crearJornada(String codigo, String codigoCuadrilla) {
        String valido = OrdenServicio.textoValido(
                codigo, "Codigo de jornada"
        );

        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);

        for (Jornada jornada : jornadas) {
            if (jornada.getCodigo().equalsIgnoreCase(valido))
                throw new IllegalArgumentException(
                        "Codigo de jornada repetido."
                );

            if (jornada.getCuadrilla() == cuadrilla
                    && jornada.getEstado() != EstadoJornada.CERRADA) {
                throw new IllegalStateException(
                        "La cuadrilla ya tiene una jornada sin cerrar."
                );
            }
        }

        jornadas.add(new Jornada(valido, cuadrilla));
    }

    public void asignarOrden(String codigoJornada, String codigoOrden) {
        Jornada destino = buscarJornada(codigoJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);

        for (Jornada jornada : jornadas) {
            if (jornada.getEstado() != EstadoJornada.CERRADA
                    && jornada.contieneOrden(orden.getCodigo())) {
                throw new IllegalStateException(
                        "La orden ya pertenece a una jornada sin cerrar."
                );
            }
        }

        destino.asignarOrden(orden);
    }

    public void retirarOrden(String codigoJornada, String codigoOrden) {
        buscarJornada(codigoJornada).retirarOrden(codigoOrden);
    }

    public void iniciarJornada(String codigoJornada) {
        buscarJornada(codigoJornada).iniciar();
    }

    public void registrarResultado(String codigoJornada,
                                   String codigoOrden,
                                   ResultadoVisita resultado) {
        buscarJornada(codigoJornada)
                .registrarResultado(codigoOrden, resultado);
    }

    public OrdenServicio buscarOrden(String codigo) {
        String valido = OrdenServicio.textoValido(
                codigo, "Codigo de orden"
        );

        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equalsIgnoreCase(valido))
                return orden;
        }

        throw new IllegalArgumentException(
                "No existe la orden " + valido + "."
        );
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        String valido = OrdenServicio.textoValido(
                codigo, "Codigo de cuadrilla"
        );

        for (Cuadrilla cuadrilla : cuadrillas) {
            if (cuadrilla.getCodigo().equalsIgnoreCase(valido))
                return cuadrilla;
        }

        throw new IllegalArgumentException(
                "No existe la cuadrilla " + valido + "."
        );
    }

    public Jornada buscarJornada(String codigo) {
        String valido = OrdenServicio.textoValido(
                codigo, "Codigo de jornada"
        );

        for (Jornada jornada : jornadas) {
            if (jornada.getCodigo().equalsIgnoreCase(valido))
                return jornada;
        }

        throw new IllegalArgumentException(
                "No existe la jornada " + valido + "."
        );
    }

    public String consultarOrdenes() {
        StringBuilder texto = new StringBuilder();

        for (OrdenServicio orden : ordenes)
            texto.append(orden).append('\n');

        return texto.length() == 0
                ? "No hay ordenes."
                : texto.toString();
    }

    public String consultarCuadrillas() {
        StringBuilder texto = new StringBuilder();

        for (Cuadrilla cuadrilla : cuadrillas)
            texto.append(cuadrilla).append('\n');

        return texto.length() == 0
                ? "No hay cuadrillas."
                : texto.toString();
    }

    public String reporteOrdenes() {
        Map<String, int[]> conteos = new LinkedHashMap<>();
        EstadoOrden[] estados = EstadoOrden.values();

        for (OrdenServicio orden : ordenes) {
            conteos.putIfAbsent(
                    orden.getCategoria(),
                    new int[estados.length]
            );

            conteos.get(orden.getCategoria())
                    [orden.getEstado().ordinal()]++;
        }

        StringBuilder texto = new StringBuilder(
                "ORDENES POR CATEGORIA Y ESTADO\n"
        );

        int[] totales = new int[estados.length];

        for (Map.Entry<String, int[]> fila : conteos.entrySet()) {
            texto.append(fila.getKey()).append(": ");

            int total = 0;

            for (int i = 0; i < estados.length; i++) {
                texto.append(estados[i])
                        .append('=')
                        .append(fila.getValue()[i])
                        .append(" ");

                total += fila.getValue()[i];
                totales[i] += fila.getValue()[i];
            }

            texto.append("TOTAL=")
                    .append(total)
                    .append('\n');
        }

        texto.append("Totales por estado: ");

        for (int i = 0; i < estados.length; i++) {
            texto.append(estados[i])
                    .append('=')
                    .append(totales[i])
                    .append(" ");
        }

        return texto.append("\nTotal de ordenes: ")
                .append(ordenes.size())
                .toString();
    }

    public String reporteIngresos() {
        Map<String, double[]> ingresos = new LinkedHashMap<>();

        double registro = 0;
        double visitas = 0;

        for (OrdenServicio orden : ordenes) {
            ingresos.putIfAbsent(
                    orden.getCategoria(),
                    new double[2]
            );

            double[] fila = ingresos.get(orden.getCategoria());

            fila[0] += orden.getIngresoRegistro();
            fila[1] += orden.getIngresoVisitas();

            registro += orden.getIngresoRegistro();
            visitas += orden.getIngresoVisitas();
        }

        StringBuilder texto = new StringBuilder(
                "INGRESOS POR CATEGORIA\n"
        );

        for (Map.Entry<String, double[]> fila : ingresos.entrySet()) {
            texto.append(String.format(
                    "%s | Registro: Q%.2f | Visitas: Q%.2f | Total: Q%.2f%n",
                    fila.getKey(),
                    fila.getValue()[0],
                    fila.getValue()[1],
                    fila.getValue()[0] + fila.getValue()[1]
            ));
        }

        return texto.append(String.format(
                "Registro total: Q%.2f%n"
                + "Visitas adicionales: Q%.2f%n"
                + "INGRESO TOTAL: Q%.2f",
                registro,
                visitas,
                registro + visitas
        )).toString();
    }

    public String reporteJornadasAbiertas() {
        StringBuilder texto = new StringBuilder();

        for (Jornada jornada : jornadas) {
            if (jornada.getEstado() == EstadoJornada.CERRADA)
                continue;

            texto.append(jornada).append('\n');

            Map<String, ResultadoVisita> resultados =
                    jornada.getResultados();

            for (OrdenServicio orden : jornada.getOrdenes()) {
                ResultadoVisita resultado =
                        resultados.get(orden.getCodigo());

                texto.append("  ")
                        .append(orden.getCodigo())
                        .append(" | ")
                        .append(
                                resultado == null
                                ? "Sin resultado"
                                : resultado
                        )
                        .append('\n');
            }
        }

        return texto.length() == 0
                ? "No hay jornadas abiertas."
                : texto.toString();
    }
}