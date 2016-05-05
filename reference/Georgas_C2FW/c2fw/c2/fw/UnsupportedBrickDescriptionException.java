//!!(C)!!
package c2.fw;

/**
 * Exception thrown when a <code>BrickLoader</code> is asked to load a brick
 * with a description it cannot process.
 *
 * @see c2.fw.BrickLoader
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class UnsupportedBrickDescriptionException extends java.lang.Exception{
	
	private String message;
	
	/**
	 * Creates a new <code>UnsupportedBrickDescriptionException</code> with no detail message.
	 */
	public UnsupportedBrickDescriptionException(){
		this("");
	}
	
	/**
	 * Creates a new <code>UnsupportedBrickDescriptionException</code> with the given detail message.
	 * @param message Detail message.
	 */
	public UnsupportedBrickDescriptionException(String message){
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
	 * Gets a String representation of this error.
	 * @return String representation of this error.
	 */
	public String toString(){
		return "UnsupportedBrickDescriptionException:" + ((message == null) ? "[no message]" : message);
	}

}

