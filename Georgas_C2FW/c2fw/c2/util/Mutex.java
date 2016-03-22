package c2.util;

public class Mutex{

	private int valueLeft;
	
	public Mutex(){
		valueLeft = 1;
	}
	
	public synchronized void acquire(){
		while(true){
			if(valueLeft > 0){
				valueLeft--;
				return;
			}
			else{
				try{
					wait();
				}
				catch(InterruptedException e){
				}
			}
		}
	}
	
	public synchronized void release(){
		valueLeft++;
		notify();
	}
	

}
