
package pdcfinalproject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 *
 * @author Efua Bainson and Andrew Abbequaye
 */
public class ReplicaitionAndRebalancingServerAndShared {

    
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
    static String result="";
    static Connection newconnection;
   
    
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
     * finds student offering a particular course
     */
    static public String getStringFromArray(String subject){
        String result="1,"+subject+",";
        for(String[] subject_student: studentsubject){
            if(subject_student[0].equals(subject)){
                if(subject_student[1]!=null){
                    result=result+subject_student[1]+",";
                }
                if(subject_student[2]!=null){
                    result=result+subject_student[2]+",";
                }
                if(subject_student[3]!=null){
                    result=result+subject_student[3];
                }
            }
        }
        return result;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        //creating an instance of ReplicaitionAndRebalancingServerAndShared
        ReplicaitionAndRebalancingServerAndShared test=new ReplicaitionAndRebalancingServerAndShared();
        
        //reading file and storing values in array
        test.readFile();
        String[] data = null;
       
          try{
            //server port 
            int serverPort = 4444;
            //opens a socket on the defined port number
            ServerSocket listenSocket = new ServerSocket(serverPort); 
            
            while(true) {
               
               
                
                clientlistcounter++;
                //checking for dead clients
                  for(int i=0;i<client.size()-1;i++){
                    Socket clientsocket=(Socket) client.get(i).get(0); 
                    Connection thread=(Connection) client.get(i).get(1);
                    if(thread.isDead==false){                         
                                        
                    }
                    else{
                        //if a client dies, find the data it stored from array and send to next clients
                         System.out.println(clientsocket+" died");
                          for(int j=2;j<=client.get(i).size()-1;j++){
                            if((j+i-1)<client.size()){
                                Socket nextsocket=(Socket) client.get(i+j-1).get(0); 
                                
                                out =new DataOutputStream( nextsocket.getOutputStream());
                                client.get(i+j-1).add(client.get(i).get(j));
                                
                                out.writeUTF(getStringFromArray((String) client.get(i).get(j)));
                                
                                System.out.print(client.get(i+j-1).get(0)+" gets ");
                                System.out.println(getStringFromArray((String) client.get(i).get(j)));
                            }
                        }
                          //identifies died clients
                          client.get(i).add("removed");
//                         
                    }


                }
                  //accepts request from client
               Socket clientSocket = listenSocket.accept();
                
               //stores client socket in array
               client.add(new ArrayList<Object>());
               client.get(clientlistcounter).add(clientSocket);
               System.out.println();
               //creating a thread to communicate with client
               Connection c = new Connection(clientSocket,studentsubject, args[0],clientlistcounter,client,isDead,  result); 
              
               //stores client thread in array
                client.get(clientlistcounter).add(c);
                //prints out current nodes and distribution
                System.out.println("Current nodes and distribution");
                
                   for(int i=0;i<client.size();i++){
                       if((client.get(i).get(client.get(i).size()-1)).equals("removed")){
                           
                       }else{
                          for(int j=0;j<client.get(i).size();j++){
                             
                                System.out.print((Object) client.get(i).get(j));
    
                            }
                            System.out.println();
                       }

                }
                  
            }
            
        } catch(IOException e) {
            System.out.println("Listen :"+e.getMessage());
        }         
     
    }
    
}

/**
     * Class that creates threads for clients
     */
class Connection extends Thread {
        
     
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
        public static String finalresult="";
        static int counter=0;
      
        
        
        public Connection (Socket aClientSocket, String [][]data, String replication, int counter, ArrayList<ArrayList<Object>> client, boolean isDead, String result) {
            finalresult=result;
            clientarray=data;
          
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
                System.out.println("Connection:"+e.getMessage());
            }
            
        }
        
       

        public void run(){
            
            try { 
                //reading in sent message
          
            String data = in.readUTF();
            //sends data to clients based on replication factor
            for(int j=0; j<=replicationfactor;j++){               
                
                String clientdataString = "1";
                //sends data to connected client
                if((datacounter+j)==4){
                    clientdata=clientarray[0];
                    client.get(clientlistcounter).add(clientdata[0]);
                         for(int i=0;i<clientdata.length;i++){
                            if(!(clientdata[i]==null)){
                                
                                clientdataString=clientdataString+","+clientdata[i];
                                
                            }else{

                            }
                        }
                        out.writeUTF(clientdataString);
                        //sends nothing if data is done replicated
                }else if((datacounter+j)>4){
                     out.writeUTF("0,No data to send");
                     datacounter=datacounter+4;
                     break;
                    
                }
                else{
                      //sends data to connected client
                        clientdata=clientarray[datacounter+j];
                        client.get(clientlistcounter).add(clientdata[0]);
                        for(int i=0;i<clientdata.length;i++){
                            if(!(clientdata[i]==null)){  
                                clientdataString=clientdataString+","+clientdata[i];

                            }else{

                            }
                             
                        
                        }
                       //writes result to client
                    out.writeUTF(clientdataString);
                }     
            }   
         
             finalresult="";
             datacounter++;
             
             //uncomment for shared computation
             out.writeUTF("2");
             data = in.readUTF();
             counter++;
             finalresult=data;
             System.out.println(finalresult);
           
             
            
             data = in.readUTF();
            } catch(EOFException e) {
                //identifies a dead client
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

