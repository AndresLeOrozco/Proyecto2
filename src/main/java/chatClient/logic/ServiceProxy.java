package chatClient.logic;

import chatClient.presentation.Controller;
import chatProtocol.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

import java.util.ArrayList;
import java.util.List;

public class ServiceProxy implements IService{
    private static IService theInstance;
    public static IService instance(){
        if (theInstance==null){ 
            theInstance=new ServiceProxy();
        }
        return theInstance;
    }

    ObjectInputStream in;
    ObjectOutputStream out;
    Controller controller;

    public ServiceProxy() {           
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    Socket skt;
    private void connect() throws Exception{
        skt = new Socket(Protocol.SERVER,Protocol.PORT);
        out = new ObjectOutputStream(skt.getOutputStream());
        out.flush();
        in = new ObjectInputStream(skt.getInputStream());    
    }

    private void disconnect() throws Exception{
        skt.shutdownOutput();
        skt.close();
    }


    public void Register(User u) throws Exception{
        connect();
        try {
            out.writeInt(Protocol.REGISTER);
            out.writeObject(u);
            out.flush();
            int response = in.readInt();
            if (response==Protocol.ERROR_NO_ERROR){
                JOptionPane.showMessageDialog(null,"Usuario Registrado");
                disconnect();
            }
            else {
                JOptionPane.showMessageDialog(null,"Error al registrar el usuario");
                disconnect();
                throw new Exception("No remote user");
            }
        } catch (IOException | ClassNotFoundException ex) {

        }
    }

    @Override
    public Contacto addContact(User u, String id,Contacto co) {
        try {
            out.writeInt(Protocol.ADDCONTACT);
            out.writeUTF(id);
            out.writeObject(u);
            out.flush();
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @Override
    public void addMessageUnsend(String remi, String Rece, String Mess) {

    }

    @Override
    public List<Message> getUnsendedMessages(String User) {
        return null;
    }

    public User login(User u) throws Exception{
        connect();
        try {
            out.writeInt(Protocol.LOGIN);
            out.writeObject(u);
            out.flush();
            int response = in.readInt();
            if (response==Protocol.ERROR_NO_ERROR){
                User u1=(User) in.readObject();
                int cantUsuariosLoggeados = in.readInt();
                List<String> usuariosLogs = new ArrayList<>();
                for(int i=0;i<cantUsuariosLoggeados;i++){
                    usuariosLogs.add(in.readUTF());
                }
                controller.setCurrentUser(u1);
                controller.setUsuariosLoggs(usuariosLogs);
                controller.agregaContactos();
                Integer MensRecibidos = in.readInt();
                if(MensRecibidos == -1){
                    System.out.println("No hay mensajes recibidos");
                }else{
                    List<Message> mens = new ArrayList<>();
                    for(int i=0;i<MensRecibidos;i++){
                        Message m = (Message) in.readObject();
                        mens.add(m);
                    }

                    controller.entregaMensajes(mens);
                }
                this.start();
                return u1;
            }
            else {
                disconnect();
                throw new Exception("Error al loguearse");
            }            
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }
    
    public void logout(User u) throws Exception{
        out.writeInt(Protocol.LOGOUT);
        out.writeObject(u);
        out.flush();
        this.stop();
        this.disconnect();
    }
    
    public void post(Message message){
        try {
            out.writeInt(Protocol.POST);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            
        }   
    }  

    // LISTENING FUNCTIONS
   boolean continuar = true;    
   public void start(){
        System.out.println("Client worker atendiendo peticiones...");
        Thread t = new Thread(new Runnable(){
            public void run(){
                listen();
            }
        });
        continuar = true;
        t.start();
    }
    public void stop(){
        continuar=false;
    }
    
   public void listen(){
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                System.out.println("DELIVERY");
                System.out.println("Operacion: "+method);
                switch(method){
                        case Protocol.DELIVER:
                            try {
                                Message message=(Message)in.readObject();
                                System.out.println("Mensaje de "+message.getSender()+" Recibido");
                                deliver(message);
                            } catch (ClassNotFoundException ex) {}
                            break;

                        case Protocol.SUCCESSADDCONTACT:
                            Contacto co = (Contacto) in.readObject();
                            System.out.println(co);
                            controller.AgregaAlaListaDeContactos(co);
                            break;

                        case Protocol.ERROR_ADDCONTACT:
                            JOptionPane.showMessageDialog(null,"Error al agregar Contacto");
                            System.out.println("No se agrego");
                            break;

                        case Protocol.ESTADO:
                            String nom = in.readUTF();
                            controller.setEstadoDeContacto(nom);
                            break;

                        case Protocol.ESTADOOFF:
                            String nomb = in.readUTF();
                            controller.setEstadoDeContactoOff(nomb);
                            break;
                }
                out.flush();
            } catch (IOException  ex) {
                continuar = false;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void enviarContacto(Contacto co,User u){
        try {
            out.writeInt(Protocol.AGREGACONTA);
            out.writeObject(co);
            out.writeObject(u);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
   private void deliver(final Message message){
      SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                try {
                    controller.deliver(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParserConfigurationException e) {
                    throw new RuntimeException(e);
                } catch (SAXException e) {
                    throw new RuntimeException(e);
                }
            }
         }
      );
  }
}
