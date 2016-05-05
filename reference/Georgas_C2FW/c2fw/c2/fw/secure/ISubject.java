/*
 * Created on Jul 10, 2005
 *
 */
package c2.fw.secure;

/**
 * A subject is the entity for which software executes.
 * 
 * @author Jie Ren
 */
public interface ISubject {
	public final static String	SUBJECT = "Subject";

	/**
	 * Get the identifier of the subject
	 * @return a string representing the identifier
	 */
	String	getSubjectId();
}
