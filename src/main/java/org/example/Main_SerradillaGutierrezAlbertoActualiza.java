package org.example;

import org.example.datos.*;

import java.io.*;
import java.io.RandomAccessFile;


import java.util.Scanner;

public class Main_SerradillaGutierrezAlbertoActualiza {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        actualizarViajeros();
        actualizarCliente();
        mostrarViajesActualizados();
        mostrarDatosViaje();
        menuEliminar();

    }


    // Ejercicio 1, metodo para actualizar viajeros
    private static void actualizarViajeros() throws IOException {

        /*
        el enunciado pide explicitamente que abramos el archivo como aleatorio, sin volcarlo en ningun array o coleccion,
        como necesitamos modificar el archivo viajes, modo rw;
        */
        RandomAccessFile viajes = new RandomAccessFile("Viajes.dat", "rw");
        File fichero = new File("Reservas.dat");
        ObjectInputStream dataIS = new ObjectInputStream(new FileInputStream(fichero));

        try {
            // Leer todas las reservas y calcular los viajeros
            while (true) {
                // Leemos un objeto de tipo Reserva
                Reserva reserva = (Reserva) dataIS.readObject();
                // Extraemos el idViaje, idCliente, y plazas de la reserva
                int idViaje = reserva.getIdviaje();
                int plazas = reserva.getPlazas();

                long puntero = (idViaje - 1) * 104; // 104 es el tamaño del registro de un viaje (en bytes)

                // Nos punteroamos en la correcta ubicación en el archivo de viajes
                viajes.seek(puntero);

                // Leemos el ID del viaje para confirmar que estamos en el lugar correcto
                int id = viajes.readInt();

                // Si el ID del viaje coincide con aquel de la reserva
                if (id == idViaje) {
                    // Nos movemos al campo de viajeros. Ultimos 4 bytes
                    viajes.seek(puntero + 104 - 4);

                    int viajerosActuales = viajes.readInt();

                    // Sumamos las plazas reservadas
                    int nuevosViajeros = viajerosActuales + plazas;

                    System.out.println("Viaje ID: " + idViaje + " | Viajeros iniciales: " + viajerosActuales + " | Viajero con ID: " + reserva.getIdcliente() + " ha reservado " + plazas + " plazas |" + " Viajeros en total: " + nuevosViajeros);

                    viajes.seek(puntero + 104 - 4);


                    // Escribimos el nuevo número de viajeros
                    viajes.writeInt(nuevosViajeros);

                }
            }

        } catch (EOFException e) {
            // Cuando llegamos al final del archivo de reservas, terminamos el proceso
            System.out.println("Se ha procesado todas las reservas satisfactoriamente.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Cerramos los archivos
            viajes.close();
            dataIS.close();
        }
    }

    //Ejercicio 2, metodo para actualizar cliente
    private static void actualizarCliente() throws IOException {

        RandomAccessFile viajes = new RandomAccessFile("Viajes.dat", "r");
        RandomAccessFile clientes = new RandomAccessFile("Clientes.dat", "rw");
        File fichero = new File("Reservas.dat");
        ObjectInputStream dataIS = new ObjectInputStream(new FileInputStream(fichero));

        try {
            // Leer todas las reservas y calcular los viajeros
            while (true) {
                Reserva reserva = (Reserva) dataIS.readObject();
                int idViaje = reserva.getIdviaje();
                int plazas = reserva.getPlazas();
                int idCliente = reserva.getIdcliente();

                long punteroViajes = (idViaje - 1) * 104;
                viajes.seek(punteroViajes);

                //buscaremos el valor del pvp, almcenando y saltando id, descripcion y fecha salida
                int idV = viajes.readInt();

                viajes.skipBytes(64 + 20);
                double pvp = viajes.readDouble();
                double importe = plazas * pvp;

                long punteroClientes = (idCliente - 1) * 52;
                clientes.seek(punteroClientes);
                int idC = clientes.readInt();

                //si el idclientes del fichero reservas coincide con el id en clientes dat
                if (idCliente == idC) {
                    // Saltamos al campo viajescontratados 4 del id + 36 de la descripcion
                    clientes.seek(punteroClientes + 40);
                    int viajesContratados = clientes.readInt();
                    double importeTotal = clientes.readDouble();

                    viajesContratados++;
                    importeTotal += importe;

                    //actualizamos
                    clientes.seek(punteroClientes + 40);
                    clientes.writeInt(viajesContratados);
                    clientes.writeDouble(importeTotal);

                    System.out.println("Cliente ID: " + idCliente + " actualizado - Viajes contratados: " + viajesContratados + " | Importe total: " + importeTotal);

                }


            }
        } catch (EOFException e) {
            // Cuando llegamos al final del archivo de reservas, terminamos el proceso
            System.out.println("Se ha procesado todos los clientes satisfactoriamente.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Cerramos los archivos
            viajes.close();
            dataIS.close();
        }
    }

    //Ejercicio 3, Mostrar de manera secuencial el archivo RAF
    private static void mostrarViajesActualizados() throws IOException {
        RandomAccessFile viajes = new RandomAccessFile("Viajes.dat", "r");

        // Definimos las variables necesarias para leer los campos
        int id, viajeros;
        char[] descripcion = new char[32]; // Para leer la descripción del viaje
        char aux;
        char[] fechasal = new char[10];  // Para leer la fecha de salida


        // Mostramos los encabezados
        System.out.println("==========================================================");
        System.out.println("ID          DESCRIPCION             FEC SALIDA VIAJEROS");
        System.out.println("=== =============================== ========== ========");

        try {
            // Leemos todos los registros en el archivo de viajes
            while ((viajes.getFilePointer() < viajes.length())) {
                // Leemos el ID del viaje
                id = viajes.readInt();

                if (id == 0) {
                    // Saltamos al siguiente componente de la lista (tamaño del registro de viaje: 104 bytes)
                    viajes.skipBytes(104 - 4);  // Restamos 4 porque ya hemos leído el ID
                    continue;
                }
                // Leemos la descripción del viaje (32 caracteres)
                for (int i = 0; i < descripcion.length; i++) {
                    aux = viajes.readChar();
                    descripcion[i] = aux;
                }

                String descripcionString = new String(descripcion).trim();

                // Leemos la fecha de salida (10 caracteres)
                for (int i = 0; i < fechasal.length; i++) {
                    aux = viajes.readChar();
                    fechasal[i] = aux;
                }
                String fechasalString = new String(fechasal).trim();

                // Saltamos el PVP y los días del viaje
                viajes.readDouble();
                viajes.readInt();

                // Leemos el número de viajeros
                viajeros = viajes.readInt();

                // Imprimimos los detalles del viaje
                System.out.printf("%2d %-32s %-15s %d\n", id, descripcionString, fechasalString, viajeros);
            }

            System.out.println("==========================================================");

        } catch (EOFException e) {
            // Cuando llegamos al final del archivo de viajes, terminamos el proceso
            System.out.println("Se ha mostrado el listado completo de viajes.");
        } finally {
            // Cerramos el archivo
            viajes.close();
        }
    }

    //Ejercicio 4, metodo para mostrar los diferentes datos de un viaje
    private static void mostrarDatosViaje() throws IOException {

        Scanner sn = new Scanner(System.in);
        boolean salir = false;
        byte opcion;
        while (!salir) {
            /*
            para este ejercicio necesitamos 4 cosas principalmente:
            1. comprobar la idViaje y mostrar los datos del viaje
            2. comprobar si hay o no reservas, y obtener datos de los clientes en clientes.dat
            3. en caso de que no existe id mostrarlo
            4. en caso de que exista id pero no reservas, mostrarlo.
             */
            System.out.println("Introduce la ID del viaje a consultar. 0 para salir");
            opcion = sn.nextByte();
            RandomAccessFile viajes = new RandomAccessFile("Viajes.dat", "r");
            RandomAccessFile clientes = new RandomAccessFile("Clientes.dat", "r");
            File fichero = new File("Reservas.dat");
            ObjectInputStream dataIS = new ObjectInputStream(new FileInputStream(fichero));

            char aux;
            boolean viajeEncontrado = false;
            int viajeros = 0;
            String descripcionString = "";

            if (opcion == 0) {
                salir = true;
            } else {

                //buscamos a través de viajes.dat la id, guardamos la descripcion
                while (viajes.getFilePointer() < viajes.length()) {
                    int id = viajes.readInt();
                    char[] descripcion = new char[32];

                    if (id == opcion) {
                        //si encontramos una id existente, empezamos a almacenar datos
                        for (int i = 0; i < descripcion.length; i++) {
                            aux = viajes.readChar();
                            descripcion[i] = aux;
                        }
                        descripcionString = new String(descripcion).trim();

                        // Saltamos los campos Fecha, PVP y días
                        viajes.skipBytes(20 + 8 + 4);
                        viajeros = viajes.readInt();

                        //finalmente, salimos del bucle
                        viajeEncontrado = true;
                        break;
                    } else {
                        //saltamos el resto de bytes si no encontramos coincidencias con id
                        viajes.skipBytes(+64 + 20 + 8 + 4 + 4);
                    }
                }
                // Si no se encuentra el viaje, mostrar mensaje de error
                if (!viajeEncontrado) {
                    System.out.println("NO EXISTE EL ID DE VIAJE");
                    System.out.println("=================================");
                    viajes.close();
                    continue; // Volver al principio del bucle
                }

                //pasamos a comprobar reservas
                boolean hayReservas = false;
                int totalClientes = 0;

                // Leer las reservas y verificar si hay reservas para este viaje
                while (true) {
                    try {
                        Reserva reserva = (Reserva) dataIS.readObject();

                        int idViaje = reserva.getIdviaje();
                        int idCliente = reserva.getIdcliente();

                        if (idViaje == opcion) {
                            // Si el ID de viaje coincide, buscar al cliente en el archivo Clientes.dat

                            long punteroClientes = (idCliente - 1) * 52;
                            clientes.seek(punteroClientes);

                            char[] nombre = new char[18];

                            for (int i = 0; i < nombre.length; i++) {
                                aux = clientes.readChar();
                                nombre[i] = aux;
                            }

                            String nombreString = new String(nombre).trim();

                            if (!hayReservas) {
                                System.out.println("=================================");
                                System.out.println(descripcionString + ", Viajeros: " + viajeros);
                                System.out.println("=================================");
                                System.out.println(" ID NOMBRE               PLAZAS ");
                                System.out.println("=== ==================== ======");
                                hayReservas = true;
                            }
                            System.out.printf("%3d %-22s %d\n", idCliente, nombreString, reserva.getPlazas());
                            // Suma 1 a cada iteración, a cada persona de la lista
                            totalClientes++;
                        }
                    } catch (EOFException e) {
                        break;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                // Si no hay reservas para este viaje
                if (!hayReservas) {
                    System.out.println("NO HAY RESERVAS");
                    System.out.println("=================================");
                } else {
                    System.out.println("\nNúmero de clientes: " + totalClientes);
                    System.out.println("=================================");
                }
                viajes.close();
                dataIS.close();
            }
        }
    }

    //Ejercicio 5, eliminar clientes del fichero aleatorio
    private static void menuEliminar() throws IOException, ClassNotFoundException {
        Scanner sn = new Scanner(System.in);
        boolean salir = false;


        while (!salir) {
            //menu
            System.out.println("Introduce el ID del cliente a eliminar. 0 para salir:");
            int opcionID = sn.nextInt();

            RandomAccessFile viajes = new RandomAccessFile("Viajes.dat", "r");
            File fichero = new File("Reservas.dat");
            ObjectInputStream dataIS = new ObjectInputStream(new FileInputStream(fichero));
            int reservas = contarReservas(opcionID);

            char aux;
            long punteroCliente = (opcionID - 1) * 52;

            if (opcionID == 0) {
                listarClientes();
                salir = true;

            } else {
                if (!verificarID(opcionID)) {
                    System.out.println("NO EXISTE EL ID DE CLIENTE");
                    System.out.println("===================================================================");
                    continue;  // Si no existe, continuar con el siguiente ciclo

                }
                if (reservas > 0) {
                    // Si tiene reservas, mostrar los viajes
                    String nombre = obtenerNombreCliente(opcionID);
                    String viajesCliente = mostrarViajesCliente(opcionID);

                    System.out.println(nombre + ", Viajes contratados: " + reservas);
                    System.out.println(viajesCliente);
                } else {
                    System.out.println("NO HA CONTRATADO NINGÚN VIAJE - eliminar");
                    if (eliminarClientes(opcionID)) {
                        System.out.println("Cliente: " + opcionID + ", eliminado");
                        System.out.println("===================================================================");


                    } else {
                        System.out.println("No se ha podido eliminar el cliente");
                    }
                }
            }
        }
    }

    public static void listarClientes() throws IOException {
        RandomAccessFile clientesFile = new RandomAccessFile("Clientes.dat", "r"); // Abrir en modo lectura
        System.out.println("LISTADO DE LOS CLIENTES");
        System.out.println("==================================================");

        try {
            clientesFile.seek(0); // Comenzar desde el inicio del archivo

            while (clientesFile.getFilePointer() < clientesFile.length()) {
                int id = clientesFile.readInt(); // Leer ID
                StringBuilder nombreBuilder = new StringBuilder();

                // Leer nombre (36 bytes: 18 caracteres de 2 bytes cada uno)
                for (int i = 0; i < 18; i++) {
                    nombreBuilder.append(clientesFile.readChar());
                }
                String nombre = nombreBuilder.toString().trim();
                int viajesContratados = clientesFile.readInt(); // Leer viajes contratados
                double importeTotal = clientesFile.readDouble(); // Leer importe total

                // Validar el registro para omitir datos vacíos
                if (id == 0 && nombre.isEmpty() && viajesContratados == 0 && importeTotal == 0.00) {
                    // Registro vacío (espacios no inicializados)
                    continue;
                }

                // Mostrar el cliente, incluidos eliminados
                System.out.printf("ID: %-3d, Nombre: %-18s, Viajes Contratados: %-3d, Importe: %,.2f\n",
                        id, nombre, viajesContratados, importeTotal);
            }
        } finally {
            clientesFile.close(); // Asegurar el cierre del archivo
        }
    }


    private static boolean eliminarClientes(int id) throws IOException {
        RandomAccessFile clientes = new RandomAccessFile("Clientes.dat", "rw");

        try {
            long punteroCliente = (id - 1) * 52; // Calculamos la posición del cliente
            if (verificarID(id)) { // Verificamos si el ID existe
                clientes.seek(punteroCliente);

                int idLeido = clientes.readInt();
                if (idLeido == id) {
                    clientes.seek(punteroCliente);
                    clientes.writeInt(id);
                    StringBuffer nombreBuffer = new StringBuffer("*eliminado*");
                    nombreBuffer.setLength(18);
                    clientes.writeChars(nombreBuffer.toString());
                    clientes.writeInt(-1);
                    clientes.writeDouble(-1.00);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientes.close();
        }

        return false; // Retornamos false si el cliente no existe o no se pudo eliminar
    }


    public static int contarReservas(int id) throws IOException {
        int reservas = 0;

        try (RandomAccessFile clientes = new RandomAccessFile("Clientes.dat", "r")) {
            // Recorrer secuencialmente el archivo
            while (clientes.getFilePointer() < clientes.length()) {
                int idCliente = clientes.readInt(); // Leer el ID del registro actual
                if (idCliente == id) {
                    clientes.skipBytes(36); // Saltar el nombre (18 caracteres * 2 bytes)
                    reservas = clientes.readInt(); // Leer el número de viajes contratados
                    break; // Detener el bucle una vez encontrado
                } else {
                    // Saltar al siguiente registro
                    clientes.skipBytes(36+4+8); // Saltar nombre, viajes contratados, y importe
                }
            }
        }

        return reservas;
    }


    public static String obtenerNombreCliente(int id) throws IOException {
        RandomAccessFile clientes = new RandomAccessFile("Clientes.dat", "r");
        long posicion = (id - 1) * 52;
        char aux;

        clientes.seek(posicion);
        char[] nombreCliente = new char[18];
        for (int i = 0; i < 18; i++) {
            aux = clientes.readChar();
            nombreCliente[i] = aux;
        }
        String nombreString = new String(nombreCliente).trim();
        clientes.close();
        return nombreString;
    }

    public static String mostrarViajesCliente(int id) throws IOException {
        RandomAccessFile viajes = new RandomAccessFile("Viajes.dat", "r");
        RandomAccessFile clientes = new RandomAccessFile("Clientes.dat", "r");

        StringBuilder info = new StringBuilder();
        info.append("===================================================================\n");
        info.append("ID   DESCRIPCION                    FEC SALIDA PVP     PLAZAS\n");
        info.append("=== =============================== ========== ========= ======\n");
        double importeTotal = 0.0;


        // Abrir el archivo de reservas
        try (ObjectInputStream dataIS = new ObjectInputStream(new FileInputStream("Reservas.dat"))) {
            while (true) {
                Reserva reserva = (Reserva) dataIS.readObject();
                if (reserva.getIdcliente() == id) {

                    int idViaje = reserva.getIdviaje();
                    int plazas = reserva.getPlazas();
                    int idCliente = reserva.getIdcliente();

                    char aux;

                    long punteroViajes = (idViaje - 1) * 104;
                    viajes.seek(punteroViajes);


                    int idV = viajes.readInt();
                    char[] descripcion = new char[32];
                    for (int i = 0; i < descripcion.length; i++) {
                        aux = viajes.readChar();
                        descripcion[i] = aux;

                    }
                    String descripcionString = new String(descripcion).trim();

                    char[] fechaSal = new char[10];
                    for (int i = 0; i < fechaSal.length; i++) {
                        aux = viajes.readChar();
                        fechaSal[i] = aux;
                    }
                    String fechaSalidaString = new String(fechaSal).trim();

                    double pvp = viajes.readDouble();
                    double importe = plazas * pvp;
                    viajes.skipBytes(4 + 4); //saltamos dias y viajeros

                    long punteroClientes = (idCliente - 1) * 52;
                    clientes.seek(punteroClientes);
                    int idC = clientes.readInt();

                    if (idCliente == idC) {
                        clientes.seek(punteroClientes + 4 + 36 + 4);


                        importeTotal += importe;

                        // Imprimimos la información del viaje
                        info.append(String.format("%-3d %-30s %-10s %-10.2f %-5d \n", idViaje, descripcionString, fechaSalidaString, pvp, plazas));
                    }

                }
            }

        } catch (EOFException e) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Mostrar el importe total de los viajes contratados
        info.append(String.format("\nImporte total : %.2f\n", importeTotal));
        info.append("===================================================================");
        return info.toString();
    }

    public static boolean verificarID(int id) throws IOException {
        RandomAccessFile clientes = new RandomAccessFile("Clientes.dat", "r");

        try {
            // Iterar sobre todos los registros de clientes
            while (clientes.getFilePointer() < clientes.length()) {
                // Leer el ID actual
                int idCliente = clientes.readInt();

                // Si encontramos el ID, retornamos true
                if (idCliente == id) {
                    clientes.close();
                    return true;
                }

                // Saltar el resto de los datos (nombre, viajes contratados, importe total)
                clientes.skipBytes(48);
            }
        } catch (EOFException e) {
            // Manejo del fin del archivo
        } finally {
            clientes.close();
        }

        // Si no encontramos el ID, retornamos false
        return false;
    }
}

//TODO resource leaks y cosas que ya no utilizo closear.