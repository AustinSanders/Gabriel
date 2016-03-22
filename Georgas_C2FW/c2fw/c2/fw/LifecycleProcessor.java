package c2.fw;

/**
 * Classes implementing this interface are capable of performing
 * processing at the four lifecycle phases of a brick,
 * <CODE>init()</CODE>, <CODE>begin()</CODE>, <CODE>end()</CODE>, 
 * and <CODE>destroy()</CODE>.  These can be plugged
 * into a <CODE>DelegateBrick</CODE> much like a <CODE>MessageProcessor</CODE>.
 * 
 * @see c2.fw.DelegateBrick
 * @see c2.fw.MessageProcessor
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */ 
public interface LifecycleProcessor{
	
	public void init();
	public void begin();
	public void end();
	public void destroy();

}
