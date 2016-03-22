//!!(C)!!
package c2.fw;

import java.io.*;
import java.net.*;
import java.util.*;

public class NetClassLoader extends URLClassLoader{
	
	protected SimpleClassLoader parentClassLoader;
	
	public NetClassLoader(URL[] urls){
		super(urls, new SimpleClassLoader());
		parentClassLoader = (SimpleClassLoader)getParent();
	}
	
	public NetClassLoader(URL[] urls, SimpleClassLoader parentClassLoader){
		super(urls, parentClassLoader);
		this.parentClassLoader = parentClassLoader;
	}
	
	public void addURL(URL url){
		URL[] currentURLs = getURLs();
		for(int i = 0; i < currentURLs.length; i++){
			if(currentURLs[i].equals(url)){
				return;	//it's a duplicate
			}
		}
		super.addURL(url);
	}
	
	public void addURLs(URL[] urls){
		for(int i = 0; i < urls.length; i++){
			addURL(urls[i]);
		}
	}
	
}