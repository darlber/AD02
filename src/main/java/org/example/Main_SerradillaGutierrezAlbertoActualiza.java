package org.example;

import org.example.datos.*;

import java.io.*;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.Scanner;

public class Main_SerradillaGutierrezAlbertoActualiza {

    public static void main(String[] args) throws IOException {

        //actualizarViajeros();
        //actualizarCliente();
        //mostrarViajesActualizados();
        mostrarDatosViaje();


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
        long puntero;

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
                                nombre[i] = clientes.readChar();
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
                        throw new RuntimeException(e);
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
}
