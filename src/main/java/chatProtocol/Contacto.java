package chatProtocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Contacto implements Serializable {

    public Integer getCantMessage() {
        return cantMessage;
    }

    public void setCantMessage(Integer cantMessage) {
        this.cantMessage = cantMessage;
    }

    Integer cantMessage;
    public String getNombreDuenoContacto() {
        return NombreDuenoContacto;
    }

    public void setNombreDuenoContacto(String nombreDuenoContacto) {
        NombreDuenoContacto = nombreDuenoContacto;
    }

    String NombreDuenoContacto;

    public String getNombreContacto() {
        return NombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        NombreContacto = nombreContacto;
    }

    String NombreContacto;

    public List<Message> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Message> mensajes) {
        this.mensajes = mensajes;
    }

    List<Message> mensajes;

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    boolean estado;

    public Contacto(){
        mensajes = new ArrayList<>();
        estado = false;
        NombreContacto = "";
        NombreDuenoContacto = "";
        cantMessage = 0;
    }
    public Contacto(List<Message> m, Boolean es, String nCon, String nNdC){
        mensajes = m;
        estado = es;
        NombreContacto = nCon;
        NombreDuenoContacto = nNdC;
        cantMessage = 0;
    }

    public void AgregarMensaje(Message m){
        mensajes.add(m);
        cantMessage++;
    }

    @Override
    public String toString() {
        if(estado)
        return NombreContacto+"   online";

        return  NombreContacto+"   offline";
    }
}
