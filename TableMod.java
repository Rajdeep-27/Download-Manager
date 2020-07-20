import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

class TableMod extends AbstractTableModel implements Observer{
    private static final String[] columnNames={"URL","Sze","Progress","Status"};    
    private static final Class[] columnClasses= {String.class, String.class,JProgressBar.class,String.class};
    private ArrayList<Download>downloads=new ArrayList<Download>();
    
    public void addNewDownload(Download download){
        download.addObserver(this);
        downloads.add(download);
        fireTableRowsInserted(getRowCount()-1, getRowCount()-1);
    }

    public Download getDownload(int row){
        return downloads.get(row);
    }

    public void clearDownload(int row){
        downloads.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public int getColumnCount(){
        return columnNames.length;
    }

    public String getNameColumn(int col){
        return columnNames[col];
    }

    public Class getColClass(int col){
        return columnClasses[col];
    }

    public int getRowCount(){
        return downloads.size();
    }

    public Object getValueAt(int r,int c){
        Download download= downloads.get(r);
        if(c==0) return download.getURL();
        if(c==1){
            int s=download.getSize();
            return (s==-1)? "" : Integer.toString(s);
        } 
        if(c==2) return new Float(download.getPrg());
        if(c==3) return Download.status[download.getStat()];
        return "";
    }

    public void update(Observable obs,Object a){
        int ind=downloads.indexOf(obs);
        fireTableCellUpdated(ind, ind);
    }    
    
}