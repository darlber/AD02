package org.example.JAXB;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType (propOrder = {"id", "nombre", "plazas"})
public class ClienteXML {
    private int id;
    private String nombre;
    private int plazas;

    public ClienteXML() {
    }

    public ClienteXML(int id, String nombre, int plazas) {
        this.id = id;
        this.nombre = nombre;
        this.plazas = plazas;
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlElement
    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }
}


