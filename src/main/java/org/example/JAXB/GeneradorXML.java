package org.example.JAXB;

import org.example.datos.Reserva;

import java.io.*;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class GeneradorXML {
    public static void main(String[] args) {

        final String PathClientes = "Clientes.dat";
        final String PathViajes = "Viajes.dat";
        final String PathReservas = "Reservas.dat";
        final String xml = "todoslosviajes.xml";

        try {

            //almacenamos los clientes en un ArrayList para un cambio correcto a XML
            List<ClienteXML> listaClientes = leerClientes(PathClientes);
            //similar con los viajes
            List<ViajeXML> listaViajes = leerViajes(PathViajes);
            //debemos comprobar que viajes tienen que clientes mediante el fichero reservas
            Map<Integer, List<Integer>> reservas = leerReservas(PathReservas);

            //tras la comprobacion, los asociamos a la listaviajes, introduciendo los clientes en listaviajes
            asignarClientesAViajes(listaViajes, listaClientes, reservas);
            //llamamos al metodo que exporte el xml
            convertirXML(listaViajes, xml);
            System.out.println("Clientes exportados a XML: " + xml);

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<ClienteXML> leerClientes(String filePath) throws IOException {
        //array que contenga cada cliente
        List<ClienteXML> clienteArray = new ArrayList<>();
        char aux;

        try (RandomAccessFile clientesRAF = new RandomAccessFile(filePath, "r")) {
            while (clientesRAF.getFilePointer() < clientesRAF.length()) {
                ClienteXML c = new ClienteXML();

                c.setId(clientesRAF.readInt());

                char[] nombre = new char[18];
                for (int i = 0; i < nombre.length; i++) {
                    aux = clientesRAF.readChar();
                    nombre[i] = aux;
                }
                c.setNombre(new String(nombre).trim());

                c.setPlazas(clientesRAF.readInt());
                clientesRAF.readDouble();

                clienteArray.add(c);
            }
        }
        return clienteArray;
    }

    public static List<ViajeXML> leerViajes(String filePath) throws IOException {
        //array que contenga cada viaje
        List<ViajeXML> listaViajes = new ArrayList<>();

        try (RandomAccessFile viajesRAF = new RandomAccessFile(filePath, "r")) {

            while ((viajesRAF.getFilePointer() < viajesRAF.length())) {

                ViajeXML v = new ViajeXML();
                char aux;

                v.setId(viajesRAF.readInt());

                if (v.getId() == 0) {

                    viajesRAF.skipBytes(104 - 4);
                    continue;
                }
                char[] descChars = new char[32];
                for (int j = 0; j < 32; j++) {
                    aux = viajesRAF.readChar();
                    descChars[j] = aux;
                }
                v.setDescripcion(new String(descChars).trim());


                char[] fechaChars = new char[10];
                for (int j = 0; j < 10; j++) {
                    aux = viajesRAF.readChar();
                    fechaChars[j] = aux;
                }
                v.setFechaSalida(new String(fechaChars).trim());

                viajesRAF.readDouble();
                viajesRAF.readInt();
                v.setViajeros(viajesRAF.readInt());

                listaViajes.add(v);
            }

        }
        return listaViajes;

    }

    private static Map<Integer, List<Integer>> leerReservas(String filePath) throws IOException, ClassNotFoundException {

        Map<Integer, List<Integer>> reservasMap = new HashMap<>();


        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("El archivo de reservas no existe.");
            return reservasMap;
        }

        System.out.println("El archivo de reservas ha sido encontrado. Procediendo a leer.");

        try (ObjectInputStream dataIS = new ObjectInputStream(new FileInputStream(filePath))) {
            while (true) {
                try {
                    Reserva r = (Reserva) dataIS.readObject();

                    int idViaje = r.getIdviaje();
                    int idCliente = r.getIdcliente();

                    //System.out.println("Reserva: Viaje " + idViaje + " - Cliente " + idCliente);

                    // asociamos cada viaje, con una lista de idclientes que tengan asociada esa idViaje
                    //compute if Absent crea una nueva arraylist unicamente si no existia para un idViaje dado
                    reservasMap.computeIfAbsent(idViaje, k -> new ArrayList<>()).add(idCliente);


                } catch (EOFException e) {
                    break; // Fin del archivo
                } catch (IOException e) {
                    System.out.println("Error de lectura en el archivo de reservas: " + e.getMessage());
                    break;
                }
            }
        }

        // System.out.println("Reservas leídas: " + reservasMap.size());
        return reservasMap;
    }

    private static void asignarClientesAViajes(List<ViajeXML> listaViajes, List<ClienteXML> listaClientes, Map<Integer, List<Integer>> reservas) {
        //bucle for mejorado que recorra listaviajes
        for (ViajeXML v : listaViajes) {
            //vamos a hacer un listado de idClientes que aparezcan en reservas
            List<Integer> idsClientes = reservas.getOrDefault(v.getId(), Collections.emptyList());
            //lista inicialmente vacia donde meteremos aquellos clientes que tengan reserva
            List<ClienteXML> clientesAsignados = new ArrayList<>();

            for (ClienteXML c : listaClientes) {
                //recorremos los datos de listaclientes
                if (idsClientes.contains(c.getId())) {
                    //añadiremos a la arraylist vacia, solo los clientes del listado total, cuyo aparezca en el archivo reservas
                    clientesAsignados.add(c);
                }
            }

            //llamamos al setter de ViajeXML, que almacena la array del nuevo listado
            v.setListaDeClientes(clientesAsignados);
        }
    }

    public static void convertirXML(List<ViajeXML> listaViajes, String outputPath) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(ListaViajes.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ListaViajes l = new ListaViajes();
        l.setTodosLosViajes(listaViajes);

        marshaller.marshal(l, new File(outputPath));
    }
}