package c2.pcwrap;

import c2.fw.*;
import c2.util.*;
import java.util.*;

class ThreadMessageProcessor extends Thread implements MessageProcessor{
	
	private Object lock = new Object();
	private GrowableWraparoundQueue messageQueue = new GrowableWraparoundQueue();
	private boolean terminate = false;
	
	private Vector processors = new Vector();
	private boolean processorListChanged = true;
	
	private static int counter = 0;
	
	private int thisCount;
	
	public ThreadMessageProcessor(){
		setDaemon(true);
		thisCount = counter++;
		setName("ThreadMessageProcessor-" + (thisCount));
		start();
	}
	
	public void setBrickName(String brickName){
		setName("ThreadMessageProcessor-" + brickName + "-" + (thisCount));
	}
	
	public void addMessageProcessor(MessageProcessor mp){
		synchronized(processors){
			processors.addElement(mp);
			processorListChanged = true;
		}
	}
	
	public void removeMessageProcessor(MessageProcessor mp){
		synchronized(processors){
			processors.removeElement(mp);
			processorListChanged = true;
		}
	}
	
	static final MessageProcessor[] emptyMessageProcessorArray = new MessageProcessor[0];
	
	public MessageProcessor[] getMessageProcessors(){
		return (MessageProcessor[])processors.toArray(emptyMessageProcessorArray);
	}
	
	public void handle(Message m){
		//System.err.println("ThreadedMessageProcessor got a message: " + m);

		synchronized(lock){
			messageQueue.enqueue(m);
			lock.notify();
		}
		//Thread.yield();
	}
	
	public void terminate(boolean t){
		this.terminate = t;
		synchronized(lock){
			lock.notifyAll();
		}
	}
	
	private MessageProcessor[] messageProcessorArray = null;
	 
	public void run(){
		while(true){
			if(terminate){
				return;
			}
		

			//System.out.println("Checking for messages.");
			while(!messageQueue.isEmpty()){
				Message m = null;
				m = (Message)messageQueue.dequeue();

				if(processorListChanged){
					messageProcessorArray = getMessageProcessors();
					processorListChanged = false;
				}

				for(int i = 0; i < messageProcessorArray.length; i++){
					messageProcessorArray[i].handle(m);
				}
				//Thread.yield();
			}
			synchronized(lock){
				if(messageQueue.isEmpty()){
					try{
						lock.wait();
					}
					catch(InterruptedException e){}
				}
			}
		}
	}
			
}


