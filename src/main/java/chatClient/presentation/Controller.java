package chatClient.presentation;

import chatClient.Application;
import chatClient.Data.XMLParse;
import chatClient.logic.ServiceProxy;
import chatProtocol.Contacto;
import chatProtocol.Message;
import chatProtocol.User;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public List<String> getUsuariosLoggs() {
        return usuariosLoggs;
    }

    public void setUsuariosLoggs(List<String> usuariosLoggs) {
        this.usuariosLoggs = usuariosLoggs;
    }

    List<String> usuariosLoggs;
    View view;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    Model model;
    
    ServiceProxy localService;
    
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        localService = (ServiceProxy)ServiceProxy.instance();
        localService.setController(this);
        view.setController(this);
        view.setModel(model);
        usuariosLoggs = new ArrayList<>();
    }

    public void setEstadodeContactos(){
        for(Contacto c:model.contactos){
            for(String n:usuariosLoggs){
                if(c.getNombreContacto().equals(n)){
                    c.setEstado(true);
                }
            }
        }
    }

    public void login(User u) throws Exception{
        User logged=ServiceProxy.instance().login(u);
        model.setCurrentContact(null);
        this.setEstadodeContactos();
        model.commit(Model.CHAT);
        model.commit(Model.AddContact);
    }

    public void agregaContactos(){
        List<Contacto> conts = new ArrayList<>();
        for(Contacto co:Application.allContacts){
            if(co.getNombreDuenoContacto().equals(model.currentUser.getNombre())){
                conts.add(co);
            }
        }
        model.setContactos(conts);
    }

    public void setEstadoDeContacto(String n){
        for(Contacto c: model.contactos){
            if(c.getNombreContacto().equals(n)){
                c.setEstado(true);
                model.commit(Model.AddContact);
            }
        }
    }
    public void setCurrentUser(User u){
        model.setCurrentUser(u);
    }
    public void setEstadoDeContactoOff(String n){
        for(Contacto c: model.contactos){
            if(c.getNombreContacto().equals(n)){
                c.setEstado(false);
                model.commit(Model.AddContact);
            }
        }
    }

    public void AddContact(String Co)throws Exception{
            if(model.currentUser.getNombre().equals(Co)) {
                JOptionPane.showMessageDialog(view.getPanel(),"No se puede agregar a si mismo como usuario");
            }else{
                Contacto co = localService.addContact(model.currentUser,Co,null);
            }

    }

    public Contacto RetornaContacto(String nCo){
        for(Contacto c:model.contactos){
            if(c.getNombreContacto().equals(nCo)){
                return c;
            }
        }
        return null;
    }

    public void updateViewContact(){
        model.commit(Model.AddContact);
    }

    public void AgregaAlaListaDeContactos(Contacto c){
        Application.agregarCont(c);
        agregaContactos();
        model.commit(Model.AddContact);
    }
    public void Register(User u)throws Exception{
        localService.Register(u);
    }

    public void post(String text){
        Message message = new Message();
        message.setMessage(text);
        if(model.getCurrentContact() != null) {
            message.setUserDeliver(model.currentContact.getNombreContacto());
            ServiceProxy.instance().post(message);
            model.commit(Model.CHAT);
        }
    }

    public void logout(){
        try {

            ServiceProxy.instance().logout(model.getCurrentUser());
            View nueva = new View();
            Model moNuevo = new Model();
            Controller newCo = new Controller(nueva,moNuevo);
            Application.allContacts = XMLParse.LeerXML();
            Application.window.setContentPane(newCo.view.getPanel());
            Application.window.getContentPane().repaint();
            Application.window.getContentPane().revalidate();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
        
    public void deliver(@NotNull Message message) throws IOException, ParserConfigurationException, SAXException {
        boolean MensajeEnviado = false;
        boolean MensajeRecibido = false;
        if(message.getSender().equals(model.currentUser.getNombre())){
            MensajeEnviado = true;
        }
        if(MensajeEnviado) {
            for (Contacto c : model.contactos) {
                if (message.getUserDeliver().equals(c.getNombreContacto())) {
                    Application.allContacts = XMLParse.LeerXML();
                    c.AgregarMensaje(message);
                    agregarMensajeAllContactos(c,message);
                    XMLParse.creaXML(Application.allContacts);
                    model.commit(Model.CHAT);
                    MensajeRecibido = true;
                }
            }
        }else{
            for (Contacto c : model.contactos){
                if (message.getSender().equals(c.getNombreContacto())){
                    Application.allContacts = XMLParse.LeerXML();
                    c.AgregarMensaje(message);
                    agregarMensajeAllContactos(c,message);
                    XMLParse.creaXML(Application.allContacts);
                    model.commit(Model.CHAT);
                    MensajeRecibido = true;
                }
            }
        }
        if(!MensajeRecibido){
            try {
                Application.allContacts = XMLParse.LeerXML();
                Contacto con = new Contacto(new ArrayList<Message>(),true,message.getSender(),model.getCurrentUser().getNombre());
                localService.enviarContacto(con,model.currentUser);
                con.AgregarMensaje(message);
                model.contactos.add(con);
                Application.allContacts.add(con);
                model.commit(Model.AddContact);
                XMLParse.creaXML(Application.allContacts);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void agregarMensajeAllContactos(Contacto con,Message m){

        for(Contacto c:Application.allContacts){
            if(c.getNombreContacto().equals(con.getNombreContacto()) && c.getNombreDuenoContacto().equals(con.getNombreDuenoContacto())){
                c.setMensajes(con.getMensajes());
                c.setCantMessage(con.getCantMessage());
            }
        }
    }

    public void entregaMensajes(List<Message> mens){
        for(Message m : mens){
            for(Contacto c:model.contactos){
                if(c.getNombreContacto().equals(m.getSender())){
                    c.AgregarMensaje(m);
                }
            }
        }
        XMLParse.creaXML(Application.allContacts);
    }

    public void setConversacion(Contacto c){
        model.setCurrentContact(c);
        model.commit(Model.CHAT);
    }
}
