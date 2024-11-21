package org.example.JAXB;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement (name = "Lista_de_Clientes")

public class ListaClientes {
    private List<ClienteXML> clienteXMLS;

    @XmlElementWrapper
    @XmlElement (name = "Un_Cliente")
    public List<ClienteXML> getClientes() {
        return clienteXMLS;
    }

    public void setClientes(List<ClienteXML> clienteXMLS) {
        this.clienteXMLS = clienteXMLS;
    }
}
