package c2.fw;

import java.lang.reflect.*;

/**
 * Factory object for <CODE>ArchitectureController</CODE>s.  Dynamically constructs
 * a new <CODE>ArchitectureController</CODE> object given a
 * <CODE>MessageHandler</CODE>, an <CODE>ArchitectureManager</CODE>, and
 * an <CODE>ArchitectureEngine</CODE>.
 *
 * @see c2.fw.ArchitectureController
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class ArchitectureControllerFactory{

	/**
	 * Create an architecture controller.
	 * @param manager <CODE>LocalArchitectureManager</CODE> to manage the topology in the
	 * new architecture controller.
	 * @param handler <CODE>MessageHandler</CODE> to handle the queuing policy for the
	 * new architecture controller.
	 * @param engine <CODE>ArchitectureEngine</CODE> to handle the threading policy
	 * for the new architecture controller.
	 * @return new <CODE>ArchitectureController</CODE> composed of the three objects
	 * passed in.
	 */
	public static ArchitectureController createController(LocalArchitectureManager manager,
	MessageHandler handler, ArchitectureEngine engine){
		InvocationHandler ih = new ProxyArchitectureControllerInvocationHandler(
			manager, handler, engine);
		ArchitectureController ac = (LocalArchitectureController)Proxy.newProxyInstance(
			LocalArchitectureController.class.getClassLoader(), new Class[] { LocalArchitectureController.class }, ih);
		manager.setController(ac);
		handler.setController(ac);
		engine.setController(ac);
 		return ac;
	}
	
	/**
	 * Create an architecture controller.  Use this function when one or more of the
	 * three parts (<CODE>ArchitectureManager</CODE>, <CODE>MessageHandler</CODE>,
	 * or <CODE>ArchitectureEngine</CODE>) implements additional interfaces, like,
	 * for instance, <CODE>SteppableArchitectureEngine</CODE>.
	 * @param manager <CODE>LocalArchitectureManager</CODE> to manage the topology in the
	 * new architecture controller.
	 * @param handler <CODE>MessageHandler</CODE> to handle the queuing policy for the
	 * new architecture controller.
	 * @param engine <CODE>ArchitectureEngine</CODE> to handle the threading policy
	 * for the new architecture controller.
	 * @param additionalInterfaceClasses Additional interface class(es) implemented
	 * by one of the three base objects that you would also like to expose through
	 * the <CODE>ArchitectureController</CODE>.
	 * @return new <CODE>ArchitectureController</CODE> composed of the three objects
	 * passed in.
	 */
	public static ArchitectureController createController(
		LocalArchitectureManager manager, MessageHandler handler, 
		ArchitectureEngine engine, Class[] additionalInterfaceClasses){
			
		InvocationHandler ih = new ProxyArchitectureControllerInvocationHandler(
			manager, handler, engine);
			
		Class[] interfaceClasses = new Class[additionalInterfaceClasses.length + 4];
		
		interfaceClasses[0] = LocalArchitectureController.class;
		interfaceClasses[1] = LocalArchitectureManager.class;
		interfaceClasses[2] = MessageHandler.class;
		interfaceClasses[3] = ArchitectureEngine.class;
		for(int i = 4; i < interfaceClasses.length; i++){
			interfaceClasses[i] = additionalInterfaceClasses[i - 4];
		}
			
		ArchitectureController ac = (ArchitectureController)Proxy.newProxyInstance(
			LocalArchitectureController.class.getClassLoader(), interfaceClasses, ih);
		manager.setController(ac);
		handler.setController(ac);
		engine.setController(ac);
 		return ac;
	}

}

