package chatServer.data;

import chatProtocol.Contacto;
import chatProtocol.Message;
import chatProtocol.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<User> users;
    Connection cn;


    public Data() {
        users = UsuariosRegistrados();
    }

    public Connection conect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto2_chat?allowPublicKeyRetrieval=true&useSSL=false", "root", "andres4646");
            System.out.println("conexion establecida");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return cn;
    }
    public void close() throws SQLException {
        cn.close();
    }

    public boolean IdRepetido(Integer n){

        String sql = "select * from usuario where id_usuario = " +n;
        try {
            Statement st;
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                return true;
            }
            else{
                return false;
            }

        } catch (Exception e) {
            System.out.println("Exception in connection: " + e.toString());
        }

        return false;
    }

    public String RetornaNUsuario(Integer n){

        String sql = "select * from usuario where id_usuario = " +n;
        try {
            Statement st;
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                String nU = rs.getString("nombre_usuario");
                return nU;
            }
            else{
                return "";
            }

        } catch (Exception e) {
            System.out.println("Exception in connection: " + e.toString());
        }
        return"";
    }

    public boolean ContactoRepetido(Integer n,Integer Co){
        String sql = "select * from contacto where dueno_lista = "+n+" and contacto_lista = "+Co;
        try {
            Statement st;
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                return true;
            }
            else{
                return false;
            }

        } catch (Exception e) {
            System.out.println("Exception in connection: " + e.toString());
        }
        return false;
    }

    public Integer retornaIdPorNombre(String no){

        String sql = "select * from usuario where nombre_usuario = '" +no+"'";
        try {
            Statement st;
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                Integer nU = rs.getInt("id_usuario");

                return nU;
            }
            else{

                return -1;
            }

        } catch (Exception e) {
            System.out.println("Exception in connection: " + e.toString());
        }
        return -1;
    }

    public Contacto InsertaContacto(String n,String Co){
        conect();
        Integer idDu = retornaIdPorNombre(n);
        Integer idCo = retornaIdPorNombre(Co);
        String sql = "INSERT INTO contacto (dueno_lista,contacto_lista) " +
                "VALUES " +
                "(?,?)";
        try {

            if (!ContactoRepetido(idDu, idCo)) {
                if (IdRepetido(idCo)) {
                    PreparedStatement statement = cn.prepareStatement(sql);
                    //setting parameter values
                    statement.setInt(1, idDu);
                    statement.setInt(2, idCo);
                    int rowsInserted = statement.executeUpdate();
                    //if rowInserted is greater than 0 mean rows are inserted
                    if (rowsInserted > 0) {
                        Contacto co = new Contacto(new ArrayList<Message>(),false,Co,n);
                        System.out.println("contacto insertado");
                        return co;
                    }
                }
                System.out.println("Nombre del contacto no existe");
                close();
                return null;
            }
            System.out.println("Contacto Repetido");
            close();
            return null;
        }catch(Exception e){

        }
        try {
            close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<User> UsuariosRegistrados(){
        List<User> Usuarios = new ArrayList<>();
        this.conect();
            String sql = "select * from usuario";
            try {
                Statement st;
                st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);

                while (rs.next()) {
                    String id = String.valueOf(rs.getInt("id_usuario"));
                    String n = rs.getString("nombre_Usuario");
                    String clave = rs.getString("clave_usuario");
                    User u = new User(id,clave,n);
                    Usuarios.add(u);
                }

            } catch (Exception e) {
                System.out.println("Exception in connection: " + e.toString());
            }
            finally {
                try {
                    this.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        return Usuarios;
    }

    public void ingresaMensaje(String Remitente,String Receptor,String Mensaje){
        conect();
        Integer Idremi =retornaIdPorNombre(Remitente);
        Integer IdRecep =retornaIdPorNombre(Receptor);
        String sql = "INSERT INTO lista_mensaje (id_remitente,id_receptor,mensaje,fechahora) " +
                "VALUES " +
                "(?,?,?,current_timestamp())";
        try {
                    PreparedStatement statement = cn.prepareStatement(sql);
                    //setting parameter values
                    statement.setInt(1, Idremi);
                    statement.setInt(2, IdRecep);
                    statement.setString(3, Mensaje);
                    int rowsInserted = statement.executeUpdate();
                    //if rowInserted is greater than 0 mean rows are inserted
                    if (rowsInserted > 0) {
                        System.out.println("Mensajes Ingresados");
                    }

        }catch(Exception e){
        }finally {
            try {
                close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }



    }

    public List<Message> mensajesRecibidos(String nUsuario){
        conect();
        List<Message> mes = new ArrayList<>();
        Integer idReceptor = retornaIdPorNombre(nUsuario);
        String sql = "select * from lista_mensaje where id_receptor = "+idReceptor;
        try {
            Statement st;
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                Integer idMensaje = rs.getInt("id_lista_mensaje");
                Integer id_Remitente = rs.getInt("id_remitente");
                String userRemitente = RetornaNUsuario(id_Remitente);
                String Mensaje = rs.getString("mensaje");
                Message m = new Message(userRemitente,Mensaje);
                m.setUserDeliver(nUsuario);
                mes.add(m);
                borrarMensajes(idMensaje);
            }
            if(mes.isEmpty()){
                close();
                return null;
            }
            else{
                close();
                return mes;
            }

        } catch (Exception e) {
            System.out.println("Exception in connection: " + e.toString());
        }
        return null;
    }

    public void borrarMensajes(Integer Id){
        String sql = "delete from lista_mensaje where id_lista_mensaje = ?";
        try {
            PreparedStatement statement = cn.prepareStatement(sql);
            //setting parameter values
            statement.setInt(1, Id);
            int rowsInserted = statement.executeUpdate();
            //if rowInserted is greater than 0 mean rows are inserted
            if (rowsInserted > 0) {
                System.out.println("Mensaje Eliminado");
            }

        } catch (Exception e) {
            System.out.println("Exception in connection: " + e.toString());
        }
    }


    public boolean UsuarioRepetido(String n){

        String sql = "select * from usuario where nombre_usuario = '"+n+"'";
        try {
            Statement st;
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){

                return true;
            }
            else{
                return false;
            }

        } catch (Exception e) {
            System.out.println("Exception in connection: " + e.toString());
        }

        return false;
    }

    public void InsertaUsuario(String n,String c) throws Exception {
        conect();
        if(!UsuarioRepetido(n)) {
            String sql = "INSERT INTO usuario (nombre_usuario,clave_usuario) " +
                    "VALUES " +
                    "(?,?)";
            //getting input from user

            try {
                PreparedStatement statement = cn.prepareStatement(sql);
                //setting parameter values
                statement.setString(1, n);
                statement.setString(2, c);

                //executing query which will return an integer value
                int rowsInserted = statement.executeUpdate();
                //if rowInserted is greater than 0 mean rows are inserted
                if (rowsInserted > 0) {
                    setUsers(UsuariosRegistrados());
                    System.out.println("Usuario Insertado");
                }
            } catch (Exception e) {
                System.out.println("Exception in connection: " + e.toString());
            }
            finally {
                close();
            }
        }else{
            close();
            throw new Exception("Usuario Repetido");
        }

    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public static void main(String[] args) {
        Data d = new Data();
        d.conect();
        d.borrarMensajes(5);
        try {
            d.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

