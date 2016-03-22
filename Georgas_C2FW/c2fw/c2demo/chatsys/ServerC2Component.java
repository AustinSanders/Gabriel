package c2demo.chatsys;

//Import framework classes
import c2.fw.*;

//Import "legacy" (i.e. standard 2-interface) C2
//support
import c2.legacy.*;

public class ServerC2Component extends AbstractC2DelegateBrick{

  //Boilerplate constructor
  public ServerC2Component(Identifier id){
    super(id);
    addMessageProcessor(new ServerC2ComponentMessageProcessor());
  }

  class ServerC2ComponentMessageProcessor implements MessageProcessor{
    public void handle(Message m){
      //Only handle chat messages from the bottom interface

      //bottomIface is the interface variable representing the
      //bottom interface of this component, available in
      //the AbstractC2Brick superclass.
      if(m.getDestination().getInterfaceIdentifier().equals(bottomIface.getIdentifier())){
        if(m instanceof NamedPropertyMessage){
          NamedPropertyMessage npm = (NamedPropertyMessage)m;
          if(npm.getName().equals("ChatMessage")){
            //This is a message we should relay

            //sendToAll sends a message to all bricks connected
            //to the specified interface; it is provided in the
            //AbstractBrick superclass.
            sendToAll(m, bottomIface);
          }
        }
      }
    }
  }
}