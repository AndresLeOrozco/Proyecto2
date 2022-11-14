package chatClient.presentation;

import chatClient.Application;
import chatProtocol.Contacto;
import chatProtocol.Message;
import chatProtocol.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Observer;

public class View implements Observer {
    private JPanel panel;
    private JPanel loginPanel;
    private JPanel bodyPanel;
    private JTextField id;
    private JPasswordField clave;
    private JButton login;
    private JButton finish;
    private JTextPane messages;
    private JTextField mensaje;
    private JButton post;
    private JButton logout;
    private JTable table1;
    private JButton registerButton;
    private JPanel RegisterPanel;
    private JTextField textField2;
    private JTextField textField3;
    private JButton crearUsuarioButton;
    private JButton iniciarSesionButton;
    private JButton agregarContacto;
    private JTextField AgregarContacto;
    private JButton Buscar;
    private JTextField BuscarContacto;
    private JList Contactos;
    private JPanel PanelMensajes;
    private JPanel PanelContactos;

    private DefaultListModel<Contacto> modelContactos;
    Model model;
    Controller controller;

    public View() {
        mensaje.setEditable(false);
        modelContactos = new DefaultListModel<>();
        loginPanel.setVisible(true);
        Application.window.getRootPane().setDefaultButton(login);
        bodyPanel.setVisible(false);
        RegisterPanel.setVisible(false);

        DefaultCaret caret = (DefaultCaret) messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clave.getPassword().length != 0 && id.getText() != "") {
                    User u = new User(" ", new String(clave.getPassword()), id.getText());
                    id.setBackground(Color.white);
                    clave.setBackground(Color.white);
                    try {
                        controller.login(u);
                        Application.window.setSize(800, 400);
                        id.setText("");
                        clave.setText("");

                    } catch (Exception ex) {
                        id.setBackground(Color.orange);
                        clave.setBackground(Color.orange);
                    }
                }else{
                    JOptionPane.showMessageDialog(Application.window,"Debe ingresar nombre de usuario y contrasenia");
                }
            }
        });
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.logout();
            }
        });
        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        post.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = mensaje.getText();
                controller.post(text);
                mensaje.setText("");
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField2.setText("");
                textField3.setText("");
                loginPanel.setVisible(false);
                bodyPanel.setVisible(false);
                RegisterPanel.setVisible(true);

            }
        });
        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginPanel.setVisible(true);
                bodyPanel.setVisible(false);
                RegisterPanel.setVisible(false);
            }
        });
        crearUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textField2.getText() == "" || textField3.getText() == ""){
                    JOptionPane.showMessageDialog(RegisterPanel,"Debe Agregar un usuario y una contraseña");
                }else{
                    User u = new User("",textField3.getText(),textField2.getText());
                    try {
                        controller.Register(u);
                        setLogin();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(RegisterPanel,ex.getMessage());
                    }
                }
            }
        });
        agregarContacto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.AddContact(AgregarContacto.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(bodyPanel,ex.getMessage());
                }
            }
        });
        Buscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DefaultListModel aux = new DefaultListModel<>();
                aux.addElement("Contactos");
                if(BuscarContacto.getText().equals("")) {
                   controller.updateViewContact();
                }else{
                    Contacto co = controller.RetornaContacto(BuscarContacto.getText());
                    if (co != null) {
                        aux.addElement(co);
                    }
                    Contactos.setModel(aux);
                }

            }
        });
        Contactos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
              if(Objects.equals(Contactos.getSelectedValue(), "Contactos")){
                  model.setCurrentContact(null);
                  mensaje.setEditable(false);
              }else{
                  Contacto aux = (Contacto) Contactos.getSelectedValue();
                  controller.setConversacion(aux);
                  mensaje.setEditable(true);
              }
            }
        });
    }

    public void setLogin(){
        loginPanel.setVisible(true);
        bodyPanel.setVisible(false);
        RegisterPanel.setVisible(false);
    }

    public void setModel(Model model) {
        this.model = model;
        model.addObserver(this);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public JPanel getPanel() {
        return panel;
    }

    String backStyle = "margin:0px; background-color:#e6e6e6;";
    String senderStyle = "background-color:#c2f0c2;margin-left:30px; margin-right:5px;margin-top:3px; padding:2px; border-radius: 25px;";
    String receiverStyle = "background-color:white; margin-left:5px; margin-right:30px; margin-top:3px; padding:2px;";

    public void update(java.util.Observable updatedModel, Object properties) {

        int prop = (int) properties;
        if (model.getCurrentUser() == null) {
            Application.window.setTitle("CHAT");
            loginPanel.setVisible(true);
            Application.window.getRootPane().setDefaultButton(login);
            bodyPanel.setVisible(false);
        } else {
            Application.window.setTitle(model.getCurrentUser().getNombre().toUpperCase());
            loginPanel.setVisible(false);
            bodyPanel.setVisible(true);
            Application.window.getRootPane().setDefaultButton(post);
            if (prop == Model.CHAT) {
                this.messages.setText("");
                String text = "Chat con: "+model.getCurrentContact()+"\n";
                messages.setText(text);
                if(model.getCurrentContact() != null) {
                    for (Message m : model.getCurrentContact().getMensajes()) {
                        if (m.getSender().equals(model.getCurrentUser().getNombre())) {
                            text += ("Me:" + m.getMessage() + "\n");
                        } else {
                            text += (m.getSender() + ": " + m.getMessage() + "\n");
                        }
                    }
                    this.messages.setText(text);
                }
            } else if (prop  == Model.AddContact) {
                AgregarContacto.setText("");
                DefaultListModel aux = new DefaultListModel<>();
                aux.addElement("Contactos");
                for(Contacto c: model.contactos) {
                   aux.addElement(c);
                }
                Contactos.setModel(aux);

            }
        }
        panel.validate();
    }

    public static void main(String[] args) {
//        JFrame j = new JFrame();
//        JList lista = new JList<>();
//        DefaultListModel con = new DefaultListModel<>();
//        DefaultListModel c = new DefaultListModel<>();
//        JPanel p = new JPanel();
//        con.addElement(new Contacto(new ArrayList<Message>(),false,"Pedro","Juan"));
//        con.addElement(new Contacto(new ArrayList<Message>(),false,"Alonso","Juan"));
//        con.addElement(new Contacto(new ArrayList<Message>(),false,"Uwu","Juan"));
//        con.addElement(new Contacto(new ArrayList<Message>(),false,"Ney","Juan"));
//        JButton bot = new JButton();
//        bot.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                c.addElement(new Contacto(new ArrayList<Message>(),false,"siuuuuuu","Juan"));
//                lista.setModel(c);
//                p.repaint();
//                p.revalidate();
//            }
//        });
//        lista.setModel(con);
//        j.setSize(200,200);
//
//        p.add(lista);
//        p.add(bot);
//        j.add(p);
//
//        j.setVisible(true);
        System.out.println(5&2);
    }

}
