/*
 * Created on Jul 14, 2005
 *
 */
package c2.fw.secure;

/**
 * An exception to signify security issues for ArchStudio. It derives
 * from {@link java.lang.SecurityException}, which is a 
 * {@link java.lang.RuntimeException}. The reason is so the existing 
 * ArchStudio exception would not have to be modified by adding and catching
 * throw SecurityException.
 *  
 * @author Jie Ren
 */
public class SecurityException extends java.lang.SecurityException {
	private static final long serialVersionUID = 1L;

	public SecurityException() {
		super();
	}
	
	public SecurityException(String m) {
		super(m);
	}
}
