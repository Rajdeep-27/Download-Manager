import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class CurrentProgress extends JProgressBar implements TableCellRenderer{
    public CurrentProgress(int min,int max){
        super(min,max);
    }

    public Component getTableCellRendererComponent(JTable table,Object val, boolean isSelec, boolean hasFoc,int r,int col){
        setValue((int)((Float)val).floatValue());
        return this;
    }
}