package c2demo;

import c2.fw.*;

public class DiagnosticArchitectureListener implements ArchitectureListener{
	
	public void brickAdded(ArchitectureManager manager, Identifier id){
		System.out.println("Brick added: " + id);
	}

	public void brickRemoving(ArchitectureManager manager, Identifier id){
		System.out.println("Brick being removed: " + id);
	}
	
	public void brickRemoved(ArchitectureManager manager, Identifier id){
		System.out.println("Brick removed: " + id);
	}
		
	public void weldAdded(ArchitectureManager manager, Weld w){
		System.out.println("Weld added: " + w);
	}
		
	public void weldRemoving(ArchitectureManager manager, Weld w){
		System.out.println("Weld being removed: " + w);
	}
	
	public void weldRemoved(ArchitectureManager manager, Weld w){
		System.out.println("Weld removed: " + w);
	}


}

