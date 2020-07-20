import java.io.*;
import java.net.*;
import java.util.*;

class Download extends Observable implements Runnable{
    private static final int MAX_BUFFER_SIZE = 1024;
    public static final String [] status = {"Downloading","Paused","Complete","Cancelled","Error"};

    //status codes, constants used by the class
    public static final int downloading=0;
    public static final int paused=1;
    public static final int complete=2;
    public static final int cancelled=3;
    public static final int error=4;

    private URL url; //url of file being downloaded
    private int size; //size of the file to be downloaded in bytes 
    private int downloaded; //bytes downloaded this far
    private int stat; //current status of the download

    public Download(URL url){
        this.url=url;
        size=-1;
        downloaded=0;
        stat=downloading;
        startDownload();
    }

    public String getURL(){
        return url.toString();
    }

    public int getSize(){
        return size;
    }

    public float getPrg(){
        return ((float)downloaded/size)*100;
    }

    public int getStat(){
        return stat;
    }

    public void pause(){
        stat=paused;
        statAltered();
    }

    public void resume(){
        stat = downloading;
        statAltered();
    }

    public void cancel(){
        stat = cancelled;
        statAltered();
    }

    public void error(){
        stat = error;
        statAltered();
    }

    private void startDownload(){
        Thread thread=new Thread(this);
        thread.start();
        //this method creates a new thread object and pass it to the instance of this class which invoked this method
    }

    private String getFilename(URL url){
        String filename=url.getFile();
        return filename.substring(filename.lastIndexOf('/')+1);
    }

    public void run(){
        RandomAccessFile file =null;
        InputStream instr=null;
    
        try{
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Range", "bytes="+downloaded+"-");
            connection.connect();

            if(connection.getResponseCode()>299 || connection.getResponseCode()<200){
                error();
            }

            if(connection.getContentLength()<1){
                error();
            }

            if(size==-1){
                size=connection.getContentLength();
                statAltered();
            }

            file=new RandomAccessFile(getFilename(url), "rw");
            file.seek(downloaded);

            instr=connection.getInputStream();
            while(stat==downloading){
                byte buff[];
                if(size-downloaded>MAX_BUFFER_SIZE){
                    buff=new byte[MAX_BUFFER_SIZE];
                }else{
                    buff=new byte[size-downloaded];
                }

                int read=instr.read(buff);
                if(read==-1)break;

                file.write(buff,0,read);
                downloaded+=read;
                statAltered();
            }
            if(stat==downloading){
                stat=complete;
            }
        } catch(Exception e){
        error();    
        }   finally{
                if(file!=null){
                    try{
                        file.close();            
                    }catch(Exception e){}
                }
            }
    }

    private void statAltered(){
        setChanged();
        notifyObservers();
    }
}        
