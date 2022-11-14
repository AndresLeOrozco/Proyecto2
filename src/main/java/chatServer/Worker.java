package chatServer;

import chatProtocol.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Worker {
    Server srv;
    ObjectInputStream in;
    ObjectOutputStream out;
    IService service;
    User user;

    public Worker(Server srv, ObjectInputStream in, ObjectOutputStream out, User user, IService service) {
        this.srv=srv;
        this.in=in;
        this.out=out;
        this.user=user;
        this.service=service;
    }

    boolean continuar;    
    public void start(){
        try {
            System.out.println("Worker atendiendo peticiones...");
            Thread t = new Thread(new Runnable(){
                public void run(){
                    listen();
                }
            });
            continuar = true;
            t.start();
        } catch (Exception ex) {  
        }
    }
    
    public void stop(){
        continuar=false;
        System.out.println("Conexion cerrada...");
    }
    
    public void listen(){
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                System.out.println("Operacion: "+method);
                switch(method){
                //case Protocol.LOGIN: done on accept
                    case Protocol.LOGOUT:
                        try {
                            String n = user.getNombre();
                            srv.remove(user);
                            srv.InformarCambioEstadoOff(n);
                            //service.logout(user); //nothing to do
                        } catch (Exception ex) {}
                        stop();
                        break;
                    case Protocol.POST:
                        Message message=null;
                        try {
                            message = (Message)in.readObject();
                            message.setSender(user.getNombre());
                            srv.deliver(message,service);
                            //service.post(message); // if wants to save messages, ex. recivier no logged on
                            System.out.println(user.getNombre()+": "+message.getMessage());
                        } catch (ClassNotFoundException ex) {}
                        break;

                    case Protocol.ADDCONTACT:
                        String NoCo = in.readUTF();
                        User u = (User)in.readObject();
                        Contacto co = service.addContact(u,NoCo,null);
                        if(co == null) {
                            out.writeInt(Protocol.ERROR_ADDCONTACT);
                        }
                        else{
                                co.setEstado(srv.UsuarioLogeado(NoCo));
                                out.writeInt(Protocol.SUCCESSADDCONTACT);
                                out.writeObject(co);
                            }
                        break;
                }
                out.flush();
            } catch (IOException  ex) {
                System.out.println(ex);
                continuar = false;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void deliver(Message message){
        try {
            out.writeInt(Protocol.DELIVER);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
        }
    }

    public void InformarCambioEstado(String nCon){
        try {
            out.writeInt(Protocol.ESTADO);
            out.writeUTF(nCon);
            out.flush();
        } catch (IOException ex) {
        }
    }
    public void InformarCambioEstadoOff(String nCon){
        try {
            out.writeInt(Protocol.ESTADOOFF);
            out.writeUTF(nCon);
            out.flush();
        } catch (IOException ex) {
        }
    }

}
