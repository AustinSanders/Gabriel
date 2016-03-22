package c2.fw;

/**
 * Provides empty implementations of the brick Lifecycle methods;
 * to be used as a base class for <CODE>LifecycleProcessor</CODE>s that
 * only implement a subset of the lifecycle methods.
 * @see c2.fw.LifecycleProcessor
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class LifecycleAdapter implements LifecycleProcessor{
	
	public void init(){
	}
	
	public void begin(){
	}
	
	public void end(){
	}
	
	public void destroy(){
	}

}
