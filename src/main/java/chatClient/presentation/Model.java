/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatClient.presentation;

import chatClient.Data.XMLParse;
import chatProtocol.Contacto;
import chatProtocol.Message;
import chatProtocol.User;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Model extends java.util.Observable {


    User currentUser;



    public Contacto getCurrentContact() {
        return currentContact;
    }

    public void setCurrentContact(Contacto currentContact) {
        this.currentContact = currentContact;
    }

    Contacto currentContact;

    public List<Contacto> getContactos() {
        return contactos;
    }

    public void setContactos(List<Contacto> contactos) {
        this.contactos = contactos;
    }

    List<Contacto> contactos;

    public Model() {

        currentUser = null;
       contactos = new ArrayList<>();
       currentContact = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }



    public void addObserver(java.util.Observer o) {
        super.addObserver(o);
        this.commit(Model.USER+Model.CHAT);
    }
    
    public void commit(int properties){
        this.setChanged();
        this.notifyObservers(properties);        
    } 
    
    public static int USER=1;
    public static int CHAT=2;

    public static int AddContact = 3;
}
