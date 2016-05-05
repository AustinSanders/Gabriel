//!!(C)!!
package c2.fw;

import java.lang.reflect.*;
import java.io.*;
import java.net.*;

/**
 * <code>BrickLoader</code> that can load classes from the current CLASSPATH
 * and from a local or remote HTTP server.
 *
 * @see c2.fw.BrickLoader
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class JavaNetBrickLoader implements BrickLoader{

	/** The HTTP-enabled <code>ClassLoader</code> */
	protected NetClassLoader netClassLoader;
	protected SimpleClassLoader baseClassLoader;
	
	public JavaNetBrickLoader(){
		baseClassLoader = new SimpleClassLoader(SimpleClassLoader.trimBadSources(SimpleClassLoader.getClassPathSources()));
		netClassLoader = new NetClassLoader(new URL[0], baseClassLoader);
	}

	protected void addFileSource(File f){
		baseClassLoader.addSource(f);
	}
	
	protected void addFileSource(String s){
		baseClassLoader.addSource(s);
	}
	
	protected void addURLSource(String s){
		try{
			URL url = new URL(s);
			addURLSource(url);
		}
		catch(MalformedURLException mue){
			throw new IllegalArgumentException(mue.toString());
		}
	}
	
	protected void addURLSource(URL url){
		netClassLoader.addURL(url);
	}
	
	/**
	 * Loads a Brick from the local CLASSPATH or any other paths that have previously been
	 * added to the local <code>NetClassLoader</code>.  Failing that, adds additional sources
	 * specified in the <code>JavaClassBrickDescription</code> to the <code>NetClassLoader</code> and
	 * attempts to load the class again.  If that fails, a <code>BrickNotFoundException</code>
	 * is thrown.
	 *
	 * @param description A <code>JavaClassBrickDescription</code> with the class name to load.
	 * @return a <code>BrickFactory</code> that can produce instances of the brick.
	 * @exception BrickNotFoundException if this loader couldn't find the class on the net or the local classpath.
   * @exception BrickLoadFailureException if this loader found the brick but could not load it.
	 * @exception UnsupportedBrickDescriptionException if the description was not a <code>JavaClassBrickDescription</code>.
	 */
	public BrickFactory load(BrickDescription description) throws BrickNotFoundException, BrickLoadFailureException, UnsupportedBrickDescriptionException{
		if(!(description instanceof JavaClassBrickDescription)){
			throw new UnsupportedBrickDescriptionException("JavaNetBrickLoader can only load bricks with descriptions that are instances of JavaClassBrickDescription");
		}
		String className = ((JavaClassBrickDescription)description).getClassName();
		try{
			Class c = netClassLoader.loadClass(className);
			return createBrickFactory(c);
		}
		catch(ClassNotFoundException cnfe){
			//We couldn't load the brick as-is.
			//Let's try adding the suggested sources and see if we can find it
			JavaClassBrickDescription jcbd = (JavaClassBrickDescription)description;
			Object[] sources = jcbd.getSources();
			if(sources != null){
				for(int i = 0; i < sources.length; i++){
					if(sources[i] instanceof String){
						String stringSource = (String)sources[i];
						try{
							URL urlSource = new URL(stringSource);
							//It's a valid URL.
							addURLSource(urlSource);
						}
						catch(MalformedURLException mue){
							//It's probably a filename or dirname
							addFileSource(stringSource);
						}
					}
					else if(sources[i] instanceof File){
						addFileSource((File)sources[i]);
					}
					else if(sources[i] instanceof URL){
						addURLSource((URL)sources[i]);
					}
				}
				try{
					Class c = netClassLoader.loadClass(className);
					return new JavaClassBrickFactory(c);					
				}
				catch(ClassNotFoundException cnfe2){
					throw new BrickNotFoundException(cnfe2.toString());
				}
			}
			throw new BrickNotFoundException(cnfe.toString());
		}
	}

	/**
	 * Create a brick factory for a class 
	 * @param c	the class
	 * @return	the brick factory for the class
	 */
	protected BrickFactory createBrickFactory(Class c) {
		return new JavaClassBrickFactory(c);
	}
}

