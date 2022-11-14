package chatProtocol;

import java.io.Serializable;

public class Message implements Serializable{
    String sender;
    String message;

    public String getUserDeliver() {
        return UserDeliver;
    }

    public void setUserDeliver(String userDeliver) {
        UserDeliver = userDeliver;
    }

    String UserDeliver;

    public Message() {
    }

    public Message(String sedner,String message) {
        this.sender = sedner;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
