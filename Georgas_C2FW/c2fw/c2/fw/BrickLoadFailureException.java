//!!(C)!!
package c2.fw;

/**
 * Exception that occurs when a <code>BrickLoader</code> is able to locate
 * a brick, but is not able to load it.
 *
 * @see c2.fw.BrickLoader
 * @see c2.fw.Brick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class BrickLoadFailureException extends java.lang.Exception{
	
	private String message;
	
	/**
	 * Creates a new <code>BrickLoadFailureException</code> with no detail message.
	 */
	public BrickLoadFailureException(){
		this("");
	}
	
	/**
	 * Creates a new <code>BrickLoadFailureException</code> with the given detail message.
	 * @param message Message describing the failure.
	 */
	public BrickLoadFailureException(String message){
		this.message = message;
	}
	
	/**
	 * Gets the detail message for this exception. 
	 * @return Detail message.
	 */
	public String getMessage(){
		return message;
	}
	
	/**
	 * Gets a String representation of this exception.
	 * @return String representation of this exception.
	 */
	public String toString(){
		return "BrickLoadFailureException:" + ((message == null) ? "[no message]" : message);
	}

}

