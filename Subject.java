import java.io.Serializable;

public class Subject implements java.io.Serializable{
	String name;
	String[] register;
	
	public Subject(){
		name = "";
		register = new String[0];
	}
	
	public void setProperties( String name, String[] register){
		this.name = name;
		this.register = register;
	}
	
	
	public void displayInfo(){
		System.out.println("=================");
		System.out.println("Class name: " + name);
		System.out.print("Students: ");
		
		for(String s: register){
			System.out.print(s + " ");
		}
		
		System.out.println("\nSize: " + register.length);
	}
	
}