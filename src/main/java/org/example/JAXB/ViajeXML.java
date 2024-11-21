package org.example.JAXB;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement
@XmlType(name = "Un_Viaje", propOrder = {"id", "descripcion", "fechaSalida", "viajeros", "listaDeClientes"})
public class ViajeXML {
    private int id;
    private String descripcion;
    private String fechaSalida;
    private int viajeros;
    private List<ClienteXML> listaDeClienteXMLS;

    public ViajeXML() {
    }

    public ViajeXML(int id, String descripcion, String fechaSalida, int viajeros, List<ClienteXML> listaDeClienteXMLS) {
        this.id = id;
        this.descripcion = descripcion;
        this.fechaSalida = fechaSalida;
        this.viajeros = viajeros;
    }

    public ViajeXML(List<ClienteXML> listaDeClienteXMLS) {
        this.listaDeClienteXMLS = listaDeClienteXMLS;
    }

    public ViajeXML(int id) {
        this.id = id;
    }

    public ViajeXML(int id, String descripcion, String fechasalida, int viajeros) {

    }

    // Getters y setters con @XmlElement
    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlElement
    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    @XmlElement
    public int getViajeros() {
        return viajeros;
    }

    public void setViajeros(int viajeros) {
        this.viajeros = viajeros;
    }

    @XmlElementWrapper (name = "Lista_de_Clientes" )
    @XmlElement(name = "Un_Cliente")
    public List<ClienteXML> getListaDeClientes() {
        return listaDeClienteXMLS;
    }

    public void setListaDeClientes(List<ClienteXML> clientesAsignados) {
        this.listaDeClienteXMLS = clientesAsignados;
    }
}
