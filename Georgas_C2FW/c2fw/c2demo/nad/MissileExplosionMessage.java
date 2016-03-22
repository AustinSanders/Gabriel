package c2demo.nad;

import c2.fw.*;

public class MissileExplosionMessage extends NamedPropertyMessage{
	public MissileExplosionMessage(){
		super("MissileExplosionMessage");
	}

	protected MissileExplosionMessage(MissileExplosionMessage copyMe){
		super(copyMe);
	}

	public Message duplicate(){
		return new MissileExplosionMessage(this);
	}

}

