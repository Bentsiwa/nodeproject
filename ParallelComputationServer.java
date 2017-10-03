
package pdcfinalproject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Efua Bainson and Andrew Abbequaye
 */
public class ParallelComputationServer {
    
    /**
     * @param args the command line arguments
     */
    static ArrayList<ArrayList<Object>> client=new ArrayList<ArrayList<Object>>();
    static String[][] studentsubject = new String[4][4];
    static int clientlistcounter=-1;
    int Englishcounter=0;
    int Mathcounter=0;
    int Frenchcounter=0;
    int PEcounter=0;  
    static DataInputStream in;
    static DataOutputStream out;
    static boolean isDead=false;
   
    
     /**
     * reads a file and saves result in an array
     */
    public void readFile(){
        
        Scanner file=null;
        //putting subjects into array
        studentsubject[0][0]="English";
        studentsubject[1][0]="Math";
        studentsubject[2][0]="French";
        studentsubject[3][0]="PE";
        
        try{
            //opens input file
            file =new Scanner(new File("student_courses.txt"));
            String text;
             //loops through file and stores values in an array
            while ((text= file.nextLine()) != null){
               
                 String[] results= text.split(",");
                  String name=results[0];
                  String subject1=results[1];
                  
                  String subject2=results[2];
                  
                  String subject3=results[3];
                  
                  passToArray(subject1,name);
                  passToArray(subject2,name);
                  passToArray(subject3,name);
                  
                       
            }
              
             
        }
        catch(Exception error){
        
        }
        //closes file
        file.close();
        
        
    }
    
     /**
     * sort values by subjects and store in array
     */
    public void passToArray(String value, String name){
        if(value.equalsIgnoreCase(studentsubject[0][0])){
             Englishcounter++;         
             studentsubject[0][Englishcounter]=name;
   
        }else if(value.equalsIgnoreCase(studentsubject[1][0])){
            Mathcounter++;  
            studentsubject[1][Mathcounter]=name;    
        }else if(value.equalsIgnoreCase(studentsubject[2][0])){
             Frenchcounter++; 
            studentsubject[2][Frenchcounter]=name;  
        }else{
             PEcounter++; 
             studentsubject[3][PEcounter]=name;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
     
        //creating an instance of ParallelComputationServer
        ParallelComputationServer test=new ParallelComputationServer();
       
        //reading file and storing values in array
        test.readFile();
        String[] data = null;
       
          try{
            //server port 
            int serverPort = 4445;
            //opens a socket on the defined port number
            ServerSocket listenSocket = new ServerSocket(serverPort); 
            
            while(true) {
           
             //accepts request from client
               Socket clientSocket = listenSocket.accept();
               
                //creating a thread to communicate with client
               
               ConnectionToClient c = new ConnectionToClient(clientSocket,studentsubject, args[0],clientlistcounter,client,isDead); 
             
                  
            }
            
        } catch(IOException e) {
            System.out.println("Listen :"+e.getMessage());
        }         
     
    }
    
}
/**
     * Class that creates threads for clients
     */
class ConnectionToClient extends Thread {
        
     
        DataInputStream in;
        DataOutputStream out;
        Socket clientSocket;
        String[][] clientarray;
        static int datacounter=0;
        static String[] clientdata;
        int clientlistcounter=0;
        int replicationfactor=0;
        ArrayList<ArrayList<Object>> client;
        public boolean isDead;
        
      
        
        
        public ConnectionToClient (Socket aClientSocket, String [][]data, String replication, int counter, ArrayList<ArrayList<Object>> client, boolean isDead) {
            clientarray=data;
           
           // this.isDead=isDead
            replicationfactor=Integer.parseInt(replication)-1;
            this.clientlistcounter=counter;
            this.client=client;
        
            try {
                clientSocket = aClientSocket;              

                    //creates a DataInputStream from its socket’s input
                    in = new DataInputStream( clientSocket.getInputStream());
                    //creates a DataOutputStream from its socket’s input
                    out =new DataOutputStream( clientSocket.getOutputStream()); 
                    this.start();
                
                
            } catch(IOException e) {
                System.out.println("ConnectionToClient:"+e.getMessage());
            }
            
        }
        
       
        
        public void run(){
            
            try { 
                //reading in sent message
          
            String data = in.readUTF();
            int counter=0;
           //sends dataset to connected client
            while(clientarray.length>counter){  
                String clientdataString="1";
                    clientdata=clientarray[counter];
                   
                         for(int i=0;i<clientdata.length;i++){
                            if(!(clientdata[i]==null)){
                                
                                clientdataString=clientdataString+","+clientdata[i];
                                
                            }else{

                            }
                        }
                        out.writeUTF(clientdataString); 
                        counter++;
                }
                 
            //comment out to find the number of students taking each subject
                out.writeUTF("2");
            
            
              data = in.readUTF();
              System.out.println(clientSocket+" "+data);
              data = in.readUTF();
              System.out.println(clientSocket+" "+data);
              data = in.readUTF();
              System.out.println(clientSocket+" "+data);
              data = in.readUTF();
              System.out.println(clientSocket+" "+data);
              
                //comment out to find subjects taking by Efua
                out.writeUTF("3,Efua");
            
          
              data = in.readUTF();
              System.out.println(clientSocket+" "+data);
              data = in.readUTF();
              System.out.println(clientSocket+" "+data);
              data = in.readUTF();
              System.out.println(clientSocket+" "+data);
              data = in.readUTF();
              System.out.println(clientSocket+" "+data);
               System.out.println();
             data = in.readUTF();
            } catch(EOFException e) {
              
                isDead=true;
                 System.out.println("dead: "+isDead);
                
            } catch(IOException e) {
                System.out.println("IO:"+e.getMessage());
            } finally { 
                try {clientSocket.close();
                }catch (IOException e){
                /*close failed*/}
            }
           
        }
}
