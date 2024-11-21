package org.example.JAXB;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "listaViajes")
public class ListaViajes {
    private List<ViajeXML> todosLosViajeXMLS;

    @XmlElementWrapper (name = "TODOS_LOS_VIAJES")
    @XmlElement(name = "Un_Viaje")
    public List<ViajeXML> getTodosLosViajes() {
        return todosLosViajeXMLS;
    }


    public void setTodosLosViajes(List<ViajeXML> todosLosViajeXMLS) {
        this.todosLosViajeXMLS = todosLosViajeXMLS;
    }
}
