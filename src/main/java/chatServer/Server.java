
package chatServer;

import chatProtocol.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;

public class Server {
    ServerSocket srv;
    List<Worker> workers; 
    
    public Server() {
        try {
            srv = new ServerSocket(Protocol.PORT);
            workers =  Collections.synchronizedList(new ArrayList<Worker>());
            System.out.println("Servidor iniciado...");
        } catch (IOException ex) {
        }
    }
    
    public void run(){
        IService service = new Service();

        boolean continuar = true;
        ObjectInputStream in=null;
        ObjectOutputStream out=null;
        Socket skt=null;
        while (continuar) {
            try {
                skt = srv.accept();
                in = new ObjectInputStream(skt.getInputStream());
                out = new ObjectOutputStream(skt.getOutputStream() );
                System.out.println("Conexion Establecida...");
                int Prot = in.readInt();
                if(Prot == Protocol.REGISTER)
                    RegisterUser(Prot,in,out,service);
                else {
                    User user = this.login(Prot, in, out, service);
                    InformarCambioEstado(user.getNombre());
                    Worker worker = new Worker(this, in, out, user, service);
                    workers.add(worker);
                    worker.start();
                }
            }
            catch (IOException | ClassNotFoundException ex) {}
            catch (Exception ex) {
                try {
                    System.out.println(ex.getMessage());
                    out.writeInt(Protocol.ERROR_LOGIN);
                    out.flush();
                    skt.close();
                } catch (IOException ex1) {}
               System.out.println("Conexion cerrada...");
            }

        }
    }

    public Boolean UsuarioLogeado(String n){
        for(Worker w:workers){
            if(w.user.getNombre().equals(n))
                return true;
        }
        return false;
    }

    public void informarDeEstado(ObjectOutputStream out){
        try{
        out.writeInt(workers.size());
        for(Worker w:workers) {
            out.writeUTF(w.user.getNombre());
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private User login(Integer pro,ObjectInputStream in,ObjectOutputStream out,IService service) throws Exception{

        int method = pro;
        if (method!=Protocol.LOGIN) throw new Exception("Should login first");
        User user=(User)in.readObject();                          
        user=service.login(user);
        for(Worker w:workers){
            if(w.user.getNombre() == user.getNombre())
                throw new Exception("El usuario se encuentra en linea");
        }
        List<Message> mensRecibidos = service.getUnsendedMessages(user.getNombre());
        out.writeInt(Protocol.ERROR_NO_ERROR);
        out.writeObject(user);
        out.writeInt(workers.size());
        for(Worker w:workers) {
            out.writeUTF(w.user.getNombre());
        }
        if(mensRecibidos == null){
            out.writeInt(-1);
        }else {
            out.writeInt(mensRecibidos.size());
            for (Message m : mensRecibidos) {
                out.writeObject(m);
            }
        }
        out.flush();
        return user;
    }

    public void InformarCambioEstado(String Nuser){
        for(Worker wk:workers){
            wk.InformarCambioEstado(Nuser);
        }
    }

    public void InformarCambioEstadoOff(String Nuser){
        for(Worker wk:workers){
            wk.InformarCambioEstadoOff(Nuser);
        }
    }

    public void RegisterUser(Integer Pro,ObjectInputStream in,ObjectOutputStream out,IService service)throws Exception{
        if(Pro == Protocol.REGISTER) {
            User user = null;
            user = (User) in.readObject();
            service.Register(user);
            out.writeInt(Protocol.ERROR_NO_ERROR);
            out.flush();
        }
    }


    public void deliver(Message message,IService ser){
        Boolean entregado = false;
        for(Worker wk:workers){
               if(message.getUserDeliver().equals(wk.user.getNombre())){
                   wk.deliver(message);
                   entregado = true;
               }
               else{
                   if(message.getSender().equals(wk.user.getNombre())){
                       wk.deliver(message);
               }
               }
        }
        if(!entregado){
            ser.addMessageUnsend(message.getSender(),message.getUserDeliver(),message.getMessage());
        }
    } 
    
    public void remove(User u){
        for(Worker wk:workers)
            if(wk.user.equals(u)){
                workers.remove(wk);
                break;
            }
        System.out.println("Quedan: " + workers.size());
    }
    
}