package chatProtocol;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class tablaContacto extends AbstractTableModel {
    private LinkedList Listen;
    public tablaContacto() {
        super();
        contactos = new LinkedList<>();
        NumeroContactos = 0;
        Listen = new LinkedList<>();
    }
    public List<Contacto> getContactos() {
        return contactos;
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0: return "Contacto";
            case 1: return "Estado";
            default:return null;
        }
    }

    public void setContactos(List<Contacto> contactos) {
        this.contactos = contactos;
    }

    public int getNumeroContactos() {
        return NumeroContactos;
    }

    public void setNumeroContactos(int numeroContactos) {
        NumeroContactos = numeroContactos;
    }

    int NumeroContactos;

    List<Contacto> contactos;

    @Override
    public int getRowCount() {
        return contactos.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0: return contactos.get(rowIndex).NombreContacto;
            case 1:return contactos.get(rowIndex).estado;
        }
        return null;
    }
}
