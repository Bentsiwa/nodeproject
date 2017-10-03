package pdcfinalproject;

import java.net.*; 
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;


public class ParalledComputationClient {	
	static ArrayList<String[]> nodeDataset = new ArrayList<String[]>();
         static DataOutputStream out;
	
	public static void main (String args[]) throws InterruptedException {
		// arguments supply message and hostname of destination 
		Socket s = null;
		
		boolean keepConnection = true;
		
		try{
			int serverPort = 4445;
			s = new Socket("127.0.0.1", serverPort);
			DataInputStream in = new DataInputStream( s.getInputStream()); 
			 out= new DataOutputStream( s.getOutputStream());
                        
                        out.writeUTF("heloo"); 
                      
			
			while(keepConnection){
				String data = in.readUTF();
				System.out.println("Received: "+ data);
				
				if(data != null){
					useData(data);
					data=null;
				}
				Thread.sleep(2000);
				
				
			}

			// UTF is a string encoding; see Sec 4.3 
//			String data = in.readUTF();
//			System.out.println("Received: "+ data);
//
//			useData(data);

		}catch (UnknownHostException e){ 
			System.out.println("Sock:"+e.getMessage());
			keepConnection = false;
		} catch (EOFException e){
			System.out.println("EOF:"+e.getMessage());
			keepConnection = false;
		} catch (IOException e){
			System.out.println("IO:"+e.getMessage());
			keepConnection = false;
		}
//				finally {
//				keepConnection = false;
//				if(s!=null) 
//					try{
//						s.close();
//					}catch(IOException e){
//						/*close failed*/
//					}
//			}
		
		
	
	}
	
	
	public static void useData(String stringData) throws IOException{
		String[] dataReceived = stringData.split(",");
		Iterator<String[]> iter = nodeDataset.iterator();
		
		switch(Integer.parseInt(dataReceived[0])){
				
			//store a subject Dataset
			case 1:
				String [] singleDataset = new String[dataReceived.length-1];
				
				for(int i=1; i < dataReceived.length; i++)
					singleDataset[i-1] = dataReceived[i];
				
				nodeDataset.add(singleDataset);
				System.out.println("Dataset received.");
				break;
			
				
			
			case 2:
				
				while(iter.hasNext()){
					String[] current = iter.next();
					int classSize = current.length-1;
					System.out.println(current[0] + ": " + classSize);
                                        out.writeUTF(current[0] + ": " + classSize);
					
				}
				break;
			
			
			case 3:
				while(iter.hasNext()){
					String[] current = iter.next();
                                       
					for(int i = 1; i < current.length; i++){
                                           
						if(current[i] .equalsIgnoreCase(dataReceived[1])){
							System.out.println(current[0]);
                                                        out.writeUTF(current[0] );
						}
					}
					
				}
				break;
				
			default:
				break;
		}
		
	}
}