package c2demo;

import c2.fw.*;
import edu.uci.ics.xarchutils.*;

public class Test{

	public static void main(String[] args){
		String[] foo = new String[]{"foo"};
		Class c = foo.getClass();
		Class[] carr = new Class[]{c};
		String[] sarr = c2.util.ClassArrayEncoder.classArrayToStringArray(carr);
		for(int i = 0; i < sarr.length; i++){
			System.out.println(sarr[i]);
		}
		try{
			Class[] carr2 = c2.util.ClassArrayEncoder.stringArrayToClassArray(sarr);
			System.out.println(carr2.length);
			System.out.println(carr2[0]);
			System.out.println(c.equals(carr2[0]));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

