/*
 * Created on Jul 10, 2005
 *
 */
package c2.fw.secure;

/**
 * A simple subject that has a string identifier
 * 
 * @author Jie Ren
 */
public class SimpleSubject implements ISubject {
	protected	String	subjectId;

	/**
	 * Create a new subject based on a string identifier (i.e. a name)
	 * 
	 * @param subjectId		the identifier of the subject
	 */
	public SimpleSubject(String subjectId) {
		this.subjectId = subjectId;
	}
	
	public String	getSubjectId() {
		return subjectId;
	}
	
	public boolean equals(Object other) {
	    if (other == null)
	        return false;
	    if (!(other instanceof SimpleSubject))
	        return false;
	    SimpleSubject os = (SimpleSubject)other;
	    if (subjectId == null) {
	        return subjectId == os.subjectId;
	    }
	    else {
	        return subjectId.equals(os.subjectId);
	    }
	}
}
