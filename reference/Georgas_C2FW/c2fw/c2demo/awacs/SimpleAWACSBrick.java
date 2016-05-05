package c2demo.awacs;

import java.util.*;

import c2.fw.*;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.types.*;

/* AWACS brick with no behavior */
public class SimpleAWACSBrick extends AbstractAWACSBrick{

	public SimpleAWACSBrick(IComponent iComp){
		super(iComp);
	}
	
	public SimpleAWACSBrick(IConnector iConn){
		super(iConn);
	}

	public void init(){
	}
	
	public void begin(){
	}
	
	public void end(){
	}
	
	public void destroy(){
	}
	
	public void handle(Message m){
		//Message sink.
		System.out.println(getIdentifier() + " received a message; m=" + m + " at tickCount={" + currentTickCount + "};");
	}

	public void tick(int t){
		super.tick(t);
	}

}

