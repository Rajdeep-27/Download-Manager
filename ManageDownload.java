import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;


public class ManageDownload extends JFrame implements Observer {
    private JTextField addTextField;
    private TableMod tablemod;
    private JTable table;

    private JButton pauseButt, resumeButt, cancelButt, clearButt;
    private Download selection;
    private boolean clearing;
    public ManageDownload(){
        setTitle("Download Manager");
        setSize(640,480);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                actionExit();
            }
        });

        JMenuBar menuBar= new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExit =new JMenuItem("Exit",KeyEvent.VK_X);
        fileExit.addActionListener((new ActionListener(){
            public void actionPerformed(ActionEvent e){
                actionExit();
            }
        }));
        fileMenu.add(fileExit);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel addPanel=new JPanel();
        addTextField=new JTextField(40);
        addPanel.add(addTextField);
        JButton addButt=new JButton("Add Download");
        addButt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                actionAdd();
            }
        });
        addPanel.add(addButt);

        tablemod=new TableMod();
        table=new JTable(tablemod);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                SelectionChanged();
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        CurrentProgress render=new CurrentProgress(0,100);
        render.setStringPainted(true);
        table.setDefaultRenderer(JProgressBar.class, render);
        table.setRowHeight((int)render.getPreferredSize().getHeight());

        JPanel downloadsPanel=new JPanel();
        downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonsPanel=new JPanel();

        pauseButt=new JButton("Pause");
        pauseButt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                actionPause();
            }
        });
        pauseButt.setEnabled(false);
        buttonsPanel.add(pauseButt);

        resumeButt= new JButton("Resume");
        resumeButt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                actionResume();
            }
        });
        resumeButt.setEnabled(false);
        buttonsPanel.add(resumeButt);

        cancelButt= new JButton("cancel");
        cancelButt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                actionCancel();
            }
        });
        cancelButt.setEnabled(false);
        buttonsPanel.add(cancelButt);

        clearButt= new JButton("Clear");
        resumeButt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                actionClear();
            }
        });
        clearButt.setEnabled(false);
        buttonsPanel.add(clearButt);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(addPanel,BorderLayout.NORTH);
        getContentPane().add(downloadsPanel,BorderLayout.CENTER);
        getContentPane().add(buttonsPanel,BorderLayout.SOUTH);
    }

        private void actionExit(){
            System.exit(0);
        }

        private void actionAdd(){
            URL urlVerified= verifyURL(addTextField.getText());
            if(urlVerified !=null){
                tablemod.addNewDownload(new Download(urlVerified));
                addTextField.setText("");
            }else{
                JOptionPane.showMessageDialog(this, "URL is invalid!","Error",JOptionPane.ERROR_MESSAGE);
            }
        }

        private URL verifyURL(String url){
            if(!url.toLowerCase().startsWith("http://"))
                return null;
            
            URL u=null;
            try{
                u=new URL(url);
            }catch(Exception e){
                return null;
            }
            if(u.getFile().length()<2)
                return null;
            return u;        
        }

        private void SelectionChanged(){
            if(selection!=null) selection.deleteObserver(ManageDownload.this);
            if(!clearing && table.getSelectedRow()>-1){
                selection=tablemod.getDownload(table.getSelectedRow());
                selection.addObserver(ManageDownload.this);
                updateButtons();
            }
        }

        private void actionPause() {
            selection.pause();
            updateButtons();
        }

        private void actionResume() {
            selection.resume();
            updateButtons();
        }

        private void actionCancel() {
            selection.cancel();
            updateButtons();
        }

        private void actionClear() {
           clearing=true;
           tablemod.clearDownload(table.getSelectedRow());
           clearing=false;
           selection=null;
           updateButtons();
        }

        private void updateButtons(){
            if(selection!=null){
                int status= selection.getStat();
                switch(status){
                    case Download.downloading:
                        pauseButt.setEnabled(true);
                        resumeButt.setEnabled(false);
                        cancelButt.setEnabled(true);
                        clearButt.setEnabled(false);
                        break;

                    case Download.paused:
                        pauseButt.setEnabled(false);
                        resumeButt.setEnabled(true);
                        cancelButt.setEnabled(true);
                        clearButt.setEnabled(false);
                        break;    
                    
                    case Download.error:
                        pauseButt.setEnabled(false);
                        resumeButt.setEnabled(true);
                        cancelButt.setEnabled(false);
                        clearButt.setEnabled(true);
                        break;   
                    
                    default:
                        pauseButt.setEnabled(false);
                        resumeButt.setEnabled(false);
                        cancelButt.setEnabled(false);
                        clearButt.setEnabled(true);
                        break;    

                }
            }else{
                pauseButt.setEnabled(false);
                resumeButt.setEnabled(false);
                cancelButt.setEnabled(false);
                clearButt.setEnabled(false);
            }
        }

        public void update(Observable o, Object arg ){
            if(selection!=null && selection.equals(o))
                updateButtons();
        }
    

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                ManageDownload manager=new ManageDownload();
                manager.setVisible(true);
            }
        });
    }

}