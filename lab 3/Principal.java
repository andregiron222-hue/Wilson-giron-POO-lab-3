import java.util.NoSuchElementException;
import java.util.Scanner;

public class Principal {
    private Principal() { }

    public static void main(String[] args) {
        SistemaTecniHogar sistema = new SistemaTecniHogar();

        try (Scanner entrada = new Scanner(System.in)) {
            System.out.println("TECNIHOGAR - SERVICIOS A DOMICILIO");
            System.out.println(
                    "Precarga: D1, D2, I1, I2, E1, E2, A1, A2."
                    + " Cuadrillas: C1, C2, C3."
            );

            boolean continuar = true;

            while (continuar) {
                mostrarMenu();

                try {
                    switch (leerEntero(entrada, "Opcion: ")) {
                        case 1:
                            System.out.println(
                                    sistema.cotizar(leerOrden(entrada))
                            );
                            System.out.println(
                                    "Cotizacion realizada sin registrar ni cobrar."
                            );
                            break;

                        case 2:
                            OrdenServicio orden = leerOrden(entrada);
                            System.out.println(sistema.cotizar(orden));

                            int aceptar = leerEntero(
                                    entrada,
                                    "1 = aceptar y pagar, 0 = cancelar: "
                            );

                            if (aceptar != 0 && aceptar != 1)
                                throw new IllegalArgumentException(
                                        "Seleccione 0 o 1."
                                );

                            if (aceptar == 1) {
                                sistema.registrarOrden(orden);
                                System.out.println(
                                        "Orden registrada y cobrada;"
                                        + " estado PENDIENTE."
                                );
                            } else {
                                System.out.println(
                                        "Registro cancelado sin cobro."
                                );
                            }
                            break;

                        case 3:
                            String codigo = leerTexto(
                                    entrada, "Codigo de cuadrilla: "
                            );

                            double capacidad = leerDecimal(
                                    entrada, "Capacidad en horas: "
                            );

                            int certificada = leerEntero(
                                    entrada,
                                    "Refrigeracion (1 = si, 0 = no): "
                            );

                            if (certificada != 0 && certificada != 1)
                                throw new IllegalArgumentException(
                                        "Seleccione 0 o 1."
                                );

                            sistema.registrarCuadrilla(
                                    new Cuadrilla(
                                            codigo,
                                            capacidad,
                                            certificada == 1
                                    )
                            );

                            System.out.println("Cuadrilla registrada.");
                            break;

                        case 4:
                            sistema.crearJornada(
                                    leerTexto(entrada, "Codigo de jornada: "),
                                    leerTexto(entrada, "Codigo de cuadrilla: ")
                            );

                            System.out.println("Jornada creada.");
                            break;

                        case 5:
                            sistema.asignarOrden(
                                    leerTexto(entrada, "Codigo de jornada: "),
                                    leerTexto(entrada, "Codigo de orden: ")
                            );

                            System.out.println(
                                    "Orden asignada sin cargo adicional."
                            );
                            break;

                        case 6:
                            sistema.retirarOrden(
                                    leerTexto(entrada, "Codigo de jornada: "),
                                    leerTexto(entrada, "Codigo de orden: ")
                            );

                            System.out.println(
                                    "Orden retirada sin modificar ingresos."
                            );
                            break;

                        case 7:
                            sistema.iniciarJornada(
                                    leerTexto(entrada, "Codigo de jornada: ")
                            );

                            System.out.println(
                                    "Jornada iniciada; cargos adicionales"
                                    + " registrados si corresponden."
                            );
                            break;

                        case 8:
                            String jornada = leerTexto(
                                    entrada, "Codigo de jornada: "
                            );

                            String codigoOrden = leerTexto(
                                    entrada, "Codigo de orden: "
                            );

                            int resultado = leerEntero(
                                    entrada,
                                    "Resultado (1 = completada, 2 = fallida): "
                            );

                            if (resultado != 1 && resultado != 2)
                                throw new IllegalArgumentException(
                                        "Seleccione 1 o 2."
                                );

                            sistema.registrarResultado(
                                    jornada,
                                    codigoOrden,
                                    resultado == 1
                                            ? ResultadoVisita.COMPLETADA
                                            : ResultadoVisita.FALLIDA
                            );

                            System.out.println("Resultado registrado.");
                            System.out.println(
                                    sistema.buscarOrden(codigoOrden)
                            );
                            System.out.println(
                                    sistema.buscarJornada(jornada)
                            );
                            break;

                        case 9:
                            System.out.println(
                                    sistema.consultarOrdenes()
                            );
                            break;

                        case 10:
                            System.out.println(
                                    sistema.consultarCuadrillas()
                            );
                            break;

                        case 11:
                            System.out.println(
                                    sistema.reporteOrdenes()
                            );
                            break;

                        case 12:
                            System.out.println(
                                    sistema.reporteIngresos()
                            );
                            break;

                        case 13:
                            System.out.println(
                                    sistema.reporteJornadasAbiertas()
                            );
                            break;

                        case 14:
                            continuar = false;
                            System.out.println("Hasta pronto.");
                            break;

                        default:
                            System.out.println(
                                    "Seleccione una opcion del 1 al 14."
                            );
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println(
                    "Entrada finalizada. Se cierra el programa."
            );
        }
    }

    private static void mostrarMenu() {
        System.out.println(
                "\n1. Cotizar"
                + "\n2. Registrar orden"
                + "\n3. Registrar cuadrilla"
                + "\n4. Crear jornada"
                + "\n5. Asignar orden"
                + "\n6. Retirar orden"
                + "\n7. Iniciar jornada"
                + "\n8. Registrar resultado de visita"
                + "\n9. Consultar ordenes"
                + "\n10. Consultar cuadrillas"
                + "\n11. Reporte de ordenes"
                + "\n12. Reporte de ingresos"
                + "\n13. Jornadas abiertas"
                + "\n14. Salir"
        );
    }

    private static String leerTexto(Scanner entrada, String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    private static int leerEntero(Scanner entrada, String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(
                        leerTexto(entrada, mensaje)
                );
            } catch (NumberFormatException e) {
                System.out.println(
                        "Ingrese un numero entero valido."
                );
            }
        }
    }

    private static double leerDecimal(Scanner entrada, String mensaje) {
        while (true) {
            try {
                double valor = Double.parseDouble(
                        leerTexto(entrada, mensaje)
                );

                if (!Double.isFinite(valor))
                    throw new NumberFormatException();

                return valor;
            } catch (NumberFormatException e) {
                System.out.println(
                        "Ingrese un decimal valido;"
                        + " utilice punto, por ejemplo 2.5."
                );
            }
        }
    }

    private static OrdenServicio leerOrden(Scanner entrada) {
        System.out.println(
                "1. Diagnostico"
                + "\n2. Instalacion"
                + "\n3. Instalacion electrica certificada"
                + "\n4. Mantenimiento de aire acondicionado"
        );

        int tipo = leerEntero(entrada, "Categoria: ");

        if (tipo < 1 || tipo > 4)
            throw new IllegalArgumentException("Categoria invalida.");

        String codigo = leerTexto(entrada, "Codigo: ");
        String cliente = leerTexto(entrada, "Cliente: ");
        String direccion = leerTexto(entrada, "Direccion: ");

        double horas = leerDecimal(
                entrada, "Horas estimadas: "
        );

        int numeroZona = leerEntero(
                entrada,
                "Zona (1 = metropolitana, 2 = periferica): "
        );

        if (numeroZona != 1 && numeroZona != 2)
            throw new IllegalArgumentException("Zona invalida.");

        Zona zona = numeroZona == 1
                ? Zona.METROPOLITANA
                : Zona.PERIFERICA;

        switch (tipo) {
            case 1:
                return new Diagnostico(
                        codigo, cliente, direccion, horas, zona
                );

            case 2:
                return new Instalacion(
                        codigo, cliente, direccion, horas, zona,
                        leerEntero(entrada, "Cantidad de puntos: ")
                );

            case 3:
                return new InstalacionElectricaCertificada(
                        codigo, cliente, direccion, horas, zona,
                        leerEntero(entrada, "Cantidad de puntos: "),
                        leerDecimal(entrada, "Valor de materiales: Q")
                );

            default:
                return new MantenimientoAireAcondicionado(
                        codigo, cliente, direccion, horas, zona,
                        leerEntero(entrada, "Capacidad BTU: ")
                );
        }
    }
}