//!!(C)!!
package c2.fw;

import java.lang.reflect.*;

/**
 * Brick loader that loads bricks as Java classes from the current CLASSPATH.
 *
 * @deprecated The functionality in this class has been superceded by JavaNetBrickLoader
 * 
 * @see c2.fw.BrickLoader
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class JavaBrickLoader implements BrickLoader{

	/** <code>ClassLoader</code> for this <code>BrickLoader</code> */
	protected SimpleClassLoader defaultClassLoader;
	
	/**
	 * Creates a new JavaBrickLoader capable of loading bricks off the current CLASSPATH.
	 */
	public JavaBrickLoader(){
		//defaultClassLoader = new SimpleClassLoader();
		//Change to allow class reloading:
		defaultClassLoader = new SimpleClassLoader(SimpleClassLoader.trimBadSources(SimpleClassLoader.getClassPathSources()));
		
		//classLoader = getClass().getClassLoader();
	}

	public void addFileSource(java.io.File f){
		defaultClassLoader.addSource(f);
	}
	
	public void addFileSource(String s){
		defaultClassLoader.addSource(s);
	}
	

	/**
	 * Given a <code>JavaClassBrickDescription</code>, loads the brick from the current
	 * CLASSPATH.
	 * @param description <code>JavaClassBrickDescription</code> with the class name to load.
	 * @exception BrickNotFoundException if the given class cannot be found on the CLASSPATH.
	 * @exception BrickLoadFailureException if the given class cannot be loaded from the CLASSPATH.
	 * @exception UnsupportedBrickDescriptionException if the description given is not a <code>JavaClassBrickDescription</code>.
	 * @return a <code>BrickFactory</code> capable of generating instances of the Java class.
	 */
	public BrickFactory load(BrickDescription description) throws BrickNotFoundException, BrickLoadFailureException, UnsupportedBrickDescriptionException{
		if(!(description instanceof JavaClassBrickDescription)){
			throw new UnsupportedBrickDescriptionException("JavaBrickLoader can only load bricks with descriptions that are instances of JavaClassBrickDescription");
		}
		String className = ((JavaClassBrickDescription)description).getClassName();
		Object[] sources = ((JavaClassBrickDescription)description).getSources();
		
		if(sources == null){
			try{
				Class c = defaultClassLoader.loadClass(className);
				return new JavaClassBrickFactory(c);
			}
			catch(ClassNotFoundException cnfe){
				throw new BrickNotFoundException(cnfe.toString());
			}
		}
		else{
			try{
				ClassLoader customClassLoader = new SimpleClassLoader(java.util.Arrays.asList(sources));
				Class c = customClassLoader.loadClass(className);
				return new JavaClassBrickFactory(c);
			}
			catch(ClassNotFoundException cnfe){
				throw new BrickNotFoundException(cnfe.toString());
			}
		}
	}
}

