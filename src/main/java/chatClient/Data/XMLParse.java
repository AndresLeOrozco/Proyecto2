package chatClient.Data;

import chatProtocol.Contacto;
import chatProtocol.Message;
import chatProtocol.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLParse {
    public static final String xmlFilePath = "Contactos.xml";
    public static  void creaXML(List<Contacto> modelo) {

        try {
            File myfile = new File(xmlFilePath);
            myfile.delete();
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement("Contactos");
            document.appendChild(root);
            for (Contacto c:modelo) {
                // employee element
                Element Contacto = document.createElement("Contacto");
                Contacto con = c;
                root.appendChild(Contacto);

                //you can also use staff.setAttribute("id", "1") for this

                // firstname element
                Element NombreContacto = document.createElement("nombre_contacto");
                NombreContacto.appendChild(document.createTextNode(con.getNombreContacto()));
                Contacto.appendChild(NombreContacto);

                Element DuenoContacto = document.createElement("dueno_contacto");
                DuenoContacto.appendChild(document.createTextNode(con.getNombreDuenoContacto()));
                Contacto.appendChild(DuenoContacto);

                Element CantMensajes = document.createElement("cant_mensajes");
                CantMensajes.appendChild(document.createTextNode(String.valueOf(con.getCantMessage())));
                Contacto.appendChild(CantMensajes);

                for(Message m:con.getMensajes()){
                    Element Sender = document.createElement("Sender");
                    Sender.appendChild(document.createTextNode(m.getSender()));
                    Contacto.appendChild(Sender);
                    Element UserDeliver = document.createElement("UserDeliver");
                    UserDeliver.appendChild(document.createTextNode(m.getUserDeliver()));
                    Contacto.appendChild(UserDeliver);
                    Element Mensaje = document.createElement("Mensaje");
                    Mensaje.appendChild(document.createTextNode(m.getMessage()));
                    Contacto.appendChild(Mensaje);
                }

            }

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static List<Contacto> LeerXML() throws IOException, SAXException, ParserConfigurationException {
        File xmlFile = new File(xmlFilePath);
        List<Contacto> ContactosTotales = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if(!xmlFile.exists())
            return new ArrayList<>();
        Document doc = builder.parse(xmlFile);
        NodeList empleadosNodes = doc.getElementsByTagName("Contacto");

        for(int i = 0; i < empleadosNodes.getLength(); i++) {
            List<Message> mensajesCont = new ArrayList<>();
            Node empleadosNode = empleadosNodes.item(i);
            if(empleadosNode.getNodeType() == Node.ELEMENT_NODE) {
                Element empleadoElement = (Element) empleadosNode;
                String dueno_contacto = empleadoElement.getElementsByTagName("dueno_contacto").item(0).getTextContent();
                String Nombre_contacto = empleadoElement.getElementsByTagName("nombre_contacto").item(0).getTextContent();
                Integer cantMensajes = Integer.valueOf( empleadoElement.getElementsByTagName("cant_mensajes").item(0).getTextContent());

                for(int j=0;j<cantMensajes;j++){
                    String sender = empleadoElement.getElementsByTagName("Sender").item(j).getTextContent();
                    String Deliver = empleadoElement.getElementsByTagName("UserDeliver").item(j).getTextContent();
                    String Mensaje = empleadoElement.getElementsByTagName("Mensaje").item(j).getTextContent();
                    Message m = new Message(sender,Mensaje);
                    m.setUserDeliver(Deliver);
                    mensajesCont.add(m);
                }

                Contacto co = new Contacto(mensajesCont,false,Nombre_contacto,dueno_contacto);
                co.setCantMessage(cantMensajes);
                ContactosTotales.add(co);
            }
        }
        return ContactosTotales;
    }

}
