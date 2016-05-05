package c2demo.fred;

import c2.fw.*;

public class FredDriverComponent extends AbstractBrick{

	public static final Identifier LOCAL_INTERFACE_ID = new SimpleIdentifier("IFACE_LOCAL");
	
	protected Interface localIface;
	
	private MessageEmitter me;
	private String localHostName;
	
	public FredDriverComponent(Identifier id){
		super(id);
		localIface = new SimpleInterface(LOCAL_INTERFACE_ID, this);
		try{
			localHostName = java.net.InetAddress.getLocalHost().getHostName();
		}
		catch(Exception e){
			localHostName = "[Can't Resolve Local Host]";
		}
	}
	
	public void handle(Message m){
		System.out.println("FredDriverComponent component got message: " + m);
		return;
	}
	
	public Interface getInterface(Identifier id){
		if(id.equals(LOCAL_INTERFACE_ID)){
			return localIface;
		}
		else{
			return null;
		}
	}
	
	public Interface[] getAllInterfaces(){
		return new Interface[]{
			localIface
		};
	}
	
	public void init(){}
	public void begin(){
		me = new MessageEmitter();
		me.start();
	}
	public void end(){
		me.terminate();
	}
	public void destroy(){}

	class MessageEmitter extends Thread{
		private boolean terminate = false;
		
		public synchronized void terminate(){
			terminate = true;
			interrupt();
		}
		
		public void run(){
			while(!terminate){
				c2.fw.NamedPropertyMessage m = new c2.fw.NamedPropertyMessage("Time");
				m.addParameter("time", System.currentTimeMillis());
				m.addParameter("host", localHostName);
				sendToAll(m, localIface);
				try{
					Thread.sleep(1500);
				}
				catch(InterruptedException e){}
			}
		}
	}
}

