import java.net.*; 
import java.io.*;


public class MobileClient {
	public static void main (String args[]) {
		Socket s = null;
		
		try{
			//Connect to localhost MobileServer
			int serverPort = 7896;
			s = new Socket("127.0.0.1", serverPort);
			
			DataInputStream in = new DataInputStream( s.getInputStream()); 
			DataOutputStream out = new DataOutputStream( s.getOutputStream()); 
			
			
			//Send message to server
			out.writeUTF("Client Requesting for Subject class"); 
			
			
			
			
			//Reading the serialized subject class the server is sending
			//Deserialize the class and
			ObjectInputStream objIn = new ObjectInputStream(in);
			
			
			//Data on subject being put into and array
			String data = "English,Andrew,Efua,Bill,Withers";
			String[] dataArray = data.split(",");
			String[] subArray = new String[dataArray.length-1];
			
			for(int i=1; i<dataArray.length; i++){
				subArray[i-1] = dataArray[i];
			}
			
			
			//Deserialise Object received from server and assign data
			Subject d = (Subject) objIn.readObject();
			d.setProperties(dataArray[0], subArray);
			
			System.out.println("Received: Subject class");
			
			//Running method in subject class
			d.displayInfo();
			
		}catch (UnknownHostException e){ 
			System.out.println("Sock:"+e.getMessage());
		} catch (ClassNotFoundException c){
			System.out.println("Student class not Found");
			c.printStackTrace();
		} catch (EOFException e){
			System.out.println("EOF:"+e.getMessage());
		} catch (IOException e){
			System.out.println("IO:"+e.getMessage());
		} finally {
			if(s!=null) 
				try{
					s.close();
				}catch(IOException e){
					/*close failed*/
				}
		}
		
		
	}
}