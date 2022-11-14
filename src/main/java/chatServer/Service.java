package chatServer;

import chatProtocol.Contacto;
import chatProtocol.User;
import chatProtocol.IService;
import chatProtocol.Message;
import chatServer.data.Data;

import java.util.ArrayList;
import java.util.List;

public class Service implements IService{

    private Data data;
    
    public Service() {

        data =  new Data();
    }

    
    public void post(Message m){
        // if wants to save messages, ex. recivier no logged on
    }

    @Override
    public void Register(User u) throws Exception {
        data.InsertaUsuario(u.getNombre(),u.getClave());
    }

    @Override
    public Contacto addContact(User u, String nomCo,Contacto co) {
            return data.InsertaContacto(u.getNombre(),nomCo);
    }

    @Override
    public void addMessageUnsend(String remi, String Rece, String Mess) {
        data.ingresaMensaje(remi,Rece,Mess);
    }

    @Override
    public List<Message> getUnsendedMessages(String User) {
        return data.mensajesRecibidos(User);
    }

    public User login(User p) throws Exception{
        for(User u:data.getUsers())
            if(p.getNombre().equals(u.getNombre()) && p.getClave().equals(u.getClave()))
                return u;
        throw new Exception("User does not exist");

    } 

    public void logout(User p) throws Exception{
        //nothing to do
    }    
}
