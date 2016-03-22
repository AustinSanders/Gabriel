package c2.pcwrap;

import c2.fw.*;

public class EBIInterestFilter implements c2.fw.MessageFilter, java.io.Serializable{

	protected String targetInterface;
	
	public EBIInterestFilter(String targetInterface){
		this.targetInterface = targetInterface;
	}
	
	public boolean accept(Message m){
		if(m instanceof NamedPropertyMessage){
			//System.out.println("targetInterface = " + targetInterface);
			String ti = (String)((NamedPropertyMessage)m).getParameter("targetInterface");
			//System.out.println("ti = " + ti);
			if(ti != null){
				if(ti.equals(targetInterface)){
					String ebiMessageType = (String)((NamedPropertyMessage)m).getParameter("ebiMessageType");
					if(ebiMessageType.equals("outgoingCall")){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String toString(){
		return "EBI Interest Filter on target interface: " + targetInterface;
	}

}
