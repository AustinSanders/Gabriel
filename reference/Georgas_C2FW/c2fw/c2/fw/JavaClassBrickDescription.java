//!!(C)!!
package c2.fw;

/**
 * <code>BrickDescription</code> that describes <code>Brick</code>s that are
 * implemented as Java classes.
 *
 * @see c2.fw.Brick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class JavaClassBrickDescription implements BrickDescription{

	private String className;
	private Object[] sources = null;
	
	/**
	 * Creates a new <code>JavaClassBrickDescription</code> with the given class name.
	 * @param className Class name of the brick.
	 */
	public JavaClassBrickDescription(String className){
		this.className = className;
	}
	
	/**
	 * Creates a new <code>JavaClassBrickDescription</code> with the given class name
	 * and the given set of sources to search for the class.  For this kind of brick
	 * description, sources may be <code>java.io.File</code> objects referring to
	 * directories, or ZIP or JAR archives, Strings indicating the filename and location
	 * of one of these files, URLs pointing to directories, ZIP, or JAR archives, or
	 * URL strings pointing to same.  The most classloader capable of loading the
	 * class will be used; additional sources specified will only be tried if 
	 * all other methods of finding the class have been exhausted (e.g. 
	 * loading the given class as a system class (i.e. off the <code>classpath</code>).
	 * @param className Class name of the brick.
	 * @param sources Places to load the brick from.
	 */
	public JavaClassBrickDescription(String className, Object[] sources){
		this.className = className;
		this.sources = sources;
	}
		
	/**
	 * Gets the class name represented in this description.
	 * @return Java class name.
	 */
	public String getClassName(){
		return className;
	}
	
	/**
	 * Gets the sources from which to load the class.
	 * @return Sources from which to load the class.
	 */
	public Object[] getSources(){
		return sources;
	}
		
	/**
	 * Determines if this <code>JavaClassBrickDescription</code> matches another one.
	 * @param otherDescription Another object to compare to this one.
	 * @return <code>true</code> if the otherDescription is a <code>JavaClassBrickDescription</code>
	 * with the same class name.
	 */
	public boolean equals(Object otherDescription){
		if(!(otherDescription instanceof JavaClassBrickDescription)){
			return false;
		}
		
		return className.equals(((JavaClassBrickDescription)otherDescription).getClassName()) &&
			java.util.Arrays.equals(((JavaClassBrickDescription)otherDescription).sources, sources);
	}
	
	public int hashCode(){
		if(sources == null){
			return getClass().hashCode() ^ className.hashCode();
		}
		else{
			return getClass().hashCode() ^ className.hashCode() ^ sources.hashCode();
		}
	}
	
	/**
	 * Gets a string representation of this JavaClassBrickDescription.
	 * @return String representation of this description.
	 */
	public String toString(){
		if(sources == null){
			return "JavaClassBrickDescription:" + getClassName();
		}
		else{
			return "JavaClassBrickDescription:{name=\"" + getClassName() + "\"; sources=" +
				c2.util.ArrayUtils.arrayToString(sources) + "}";
		}
	}

}

