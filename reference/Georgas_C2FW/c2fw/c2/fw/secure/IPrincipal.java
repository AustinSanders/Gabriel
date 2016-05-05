/*
 * Created on Jul 11, 2005
 *
 */
package c2.fw.secure;

/**
 * A principal can be any entify. More specifically, it can be a role,
 * which a subject can activate.
 *  
 * @author Jie Ren
 */
public interface IPrincipal {
	public final static String	PRINCIPAL = "Principal";
	
	/**
	 * Get the name of the principal
	 * @return the name of the principal
	 */
	public String getName();
}
