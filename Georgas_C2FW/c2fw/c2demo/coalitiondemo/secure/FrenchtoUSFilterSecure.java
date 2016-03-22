/*
 * Created on Jul 3, 2005
 *
 */
package c2demo.coalitiondemo.secure;

import c2.fw.Identifier;
import c2.fw.InitializationParameter;
import c2.fw.Message;
import c2.fw.MessageProcessor;
import c2.fw.NamedPropertyMessage;
import c2.fw.secure.SecureC2Brick;
import c2demo.coalitiondemo.EntityType;

public class FrenchtoUSFilterSecure extends SecureC2Brick {
    public FrenchtoUSFilterSecure(Identifier id) {
    	this(id, null);
    }
    
    public FrenchtoUSFilterSecure(Identifier id, InitializationParameter[] params)    {
        super(id, params);
        this.addMessageProcessor( new MessageProcessor(){
            public void handle(Message m) {
                if (m instanceof NamedPropertyMessage) {
                    NamedPropertyMessage npm;
                    npm = (NamedPropertyMessage)m;
                    if (npm.getName().equals("entity")) {
                    	// This demo should not be in the c2.fw.secure package, 
                    	// the SimpleClassLoader probably loads them differently
                    	// and the following cast would fail
                        EntityType entity = (EntityType)npm.getParameter("type");
                        // forward only TROUPS messages
                        if (entity.equals(EntityType.TROUPS) ||
                            entity.equals(EntityType.AIRDEFENSEMISSILE) ||
                            entity.equals(EntityType.BATTLESHIP)) {
                            sendNotification(npm);
                        }
                    }
                }
            }
        });   
    }
}