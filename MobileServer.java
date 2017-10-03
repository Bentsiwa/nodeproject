import java.net.*; 
import java.io.*;
import java.util.Arrays;

public class MobileServer {
	public static void main (String args[]) {
		try{
			int serverPort = 7896;
			ServerSocket listenSocket = new ServerSocket(serverPort); 
			
			while(true) {
				Socket clientSocket = listenSocket.accept();
				Connection c = new Connection(clientSocket); 
				
			}
			
		}catch(IOException e){
			System.out.println("Listen :"+e.getMessage());
		} 
	}
}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	
	public Connection (Socket aClientSocket) {
		
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out =new DataOutputStream( clientSocket.getOutputStream()); 
			
			//start thread
			this.start();
		} catch(IOException e){
			System.out.println("Connection:"+e.getMessage());
		} 
	}
	
	
	public void run(){
		try { // an echo server
			String data = in.readUTF();
			System.out.println(data);
			
			//Create subject object
			Subject newSub = new Subject();
			
			//Serialise subject object and sent to ouptput stream
			ObjectOutputStream objectOut = new ObjectOutputStream(out);
			objectOut.writeObject(newSub);
			System.out.println("Class Sent.");
			
			
		}catch(EOFException e) {
			System.out.println("EOF:"+e.getMessage());
		}catch(IOException e) {
			System.out.println("IO:"+e.getMessage());
		}finally {
			try{
				clientSocket.close();
			}catch(IOException e){
				/*close failed*/
			}
		}
	} 
}

