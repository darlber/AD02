package org.example;

import org.example.datos.*;

import java.io.*;
import java.io.RandomAccessFile;

public class Main_SerradillaGutierrezAlbertoActualiza {

    public static void main(String[] args) throws IOException {
        final int longRegViajes = 4 + 64 + 20 + 8 + 4 + 4;


        // Llamada al metodo del ejercicio1

        actualizarViajeros();


    }

    // Ejercicio1, metodo para actualizar viajeros


    public static void actualizarViajeros() throws IOException {

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

                    System.out.println("Viaje ID: " + idViaje + " | Viajeros iniciales: " + viajerosActuales
                            + " | Viajero con ID: " + reserva.getIdcliente() + " ha reservado " + plazas
                            + " plazas |"
                            + " Viajeros en total: " + nuevosViajeros);

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
}