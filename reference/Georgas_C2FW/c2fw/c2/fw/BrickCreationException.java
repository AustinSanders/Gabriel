//!!(C)!!
package c2.fw;

/**
 * Exception that occurs when a <code>BrickFactory</code> cannot create a brick.
 *
 * @see c2.fw.Brick
 * @see c2.fw.BrickFactory
 * @see c2.fw.BrickLoader
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class BrickCreationException extends java.lang.Exception{
	
	//A message that describes the problem.
	private String message;
	
	/**
	 * Creates a new <code>BrickCreationException<code> that has no detail message.
	 */
	public BrickCreationException(){
		this("");
	}
	
	/**
	 * Creates a new <code>BrickCreationException<code> with the given detail message.
	 * @param message Detail message.
	 */
	public BrickCreationException(String message){
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
	 * Gets a string representation of this exception.
	 * @return String representation of this exception.
	 */
	public String toString(){
		return "BrickCreationException:" + ((message == null) ? "[no message]" : message);
	}

}

