package c2.fw;

import java.lang.reflect.*;

/**
 * <code>BrickFactory</code> that can generate Bricks from a Java class.
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class JavaClassBrickFactory implements BrickFactory{
	protected Class c;
	
	/**
	 * Create a new <code>JavaClassBrickFactory</code> that creates <code>Brick</code>s of
	 * the given class.
	 * @param c Java class representing the Brick type.
	 */
	public JavaClassBrickFactory(Class c){
		
		if(!Brick.class.isAssignableFrom(c)){
			throw new IllegalArgumentException("Class " + c + " does not implement Brick.");
		}
		
		this.c = c;
	}

	/**
	 * Create a new Brick of the factory class with the given parameters
	 * passed to the Class' constructor.
   * @param initParams Initial parameters passed to the class constructor.
   * @exception BrickCreationException if the parameters do not match any constructor.
	 */
	public Brick create(Identifier id, InitializationParameter[] initParams) throws BrickCreationException{
		Object[] params;
		if(initParams == null){
			params = new Object[]{id};
		}
		else{
			params = new Object[]{id, initParams};
		}
		
		try{
			Object o = createObject(c, params);
			return (Brick)o;
		}
		catch(ClassCastException cce){
			throw new BrickCreationException("Could not create brick with identifier: " + id + "; " + cce.toString());
		}
		catch(Throwable e){
			e.printStackTrace();
			throw new BrickCreationException("Could not create brick with identifier: " + id + "; " + e.toString());
		}
	}
	
	private static Object createObject(Class c, Object[] params) throws Throwable{
		//System.out.println("params.length = " + params.length);
		//System.out.println("params[0] = " + params[0].getClass().getName());
		//System.out.println("params[0] = " + params[0]);
		Constructor[] cArr = c.getConstructors();
		Exception lastException = null;
		for(int i = 0; i < cArr.length; i++){
			try{
				//System.out.println("Creating class with constructor: " + cArr[i]);
				Object o = cArr[i].newInstance(params);
				//System.out.println("Created it!  Object = " + o);
				return o;
			}
			catch(Exception e){
				lastException = e;
			}
		}
		throw lastException;
	}
	
}
