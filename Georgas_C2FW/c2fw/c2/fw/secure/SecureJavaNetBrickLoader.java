/*
 * Created on Jul 3, 2005
 *
 */
package c2.fw.secure;

import c2.fw.BrickFactory;
import c2.fw.BrickLoader;
import c2.fw.JavaNetBrickLoader;

public class SecureJavaNetBrickLoader extends JavaNetBrickLoader implements BrickLoader{

	protected BrickFactory createBrickFactory(Class c) {
		return new SecureJavaClassBrickFactory(c);
	}
}
