package chatClient;

import chatClient.Data.XMLParse;
import chatClient.presentation.Controller;
import chatClient.presentation.Model;
import chatClient.presentation.View;
import chatProtocol.Contacto;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class Application {
    static public List<Contacto> allContacts;

    static public void agregarCont(Contacto c){
        try {
            allContacts = XMLParse.LeerXML();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        allContacts.add(c);
        XMLParse.creaXML(allContacts);
        System.out.println("Agregado a allcontactos");
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");}
        catch (Exception ex) {};

        window = new JFrame();
        try {
            allContacts = XMLParse.LeerXML();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Model model= new Model();
        View view = new View();
        Controller controller =new Controller(view, model);
        window.setSize(600,200);
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.setTitle("CHAT");
        try {
            window.setIconImage((new ImageIcon(Application.class.getResource("/logo.png"))).getImage());
        } catch (Exception e) {}
        window.setContentPane(controller.getView().getPanel());
        window.setVisible(true);
    }

    public static JFrame window;
}
