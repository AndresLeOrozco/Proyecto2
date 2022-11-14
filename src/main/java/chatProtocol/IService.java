package chatProtocol;

import java.util.List;

public interface IService {
    public User login(User u) throws Exception;
    public void logout(User u) throws Exception; 
    public void post(Message m);

    public void Register(User u) throws Exception;

    public Contacto addContact(User u,String id,Contacto co);

    public void addMessageUnsend(String remi,String Rece,String Mess);
    public List<Message> getUnsendedMessages(String User);
}
