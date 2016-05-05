
package c2.fw;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class SimpleClassLoader extends ClassLoader{
	
	protected ClassLoader parentClassLoader;
	protected Collection sources;
	protected Hashtable classCache = new Hashtable();
	
	class CacheEntry{
		public Class actualClass;
		public File source;
		public long lastModified;
	}
	
	public SimpleClassLoader(){
		this(new HashSet(), null);
	}
	
	public SimpleClassLoader(Collection sources){
		this(new HashSet(sources), null);
		//System.out.println("sources = " + sources);
	}
	
	public static Collection getClassPathSources(){
		ArrayList l = new ArrayList();
		l.add(".");
		String classpath = System.getProperty("java.class.path");
		if(classpath == null){
			return l;
		}
		else{
			String pathSeparator = System.getProperty("path.separator");
			if(pathSeparator == null){
				return null;
			}
			StringTokenizer st = new StringTokenizer(classpath, pathSeparator);
			while (st.hasMoreTokens()){
				l.add(st.nextToken());
			}
		}
		return l;
	}
	
	public static Collection trimBadSources(Collection sources){
		ArrayList goodSources = new ArrayList();
		
		for(Iterator en = sources.iterator(); en.hasNext(); ){
			Object o = en.next();
			boolean isBad = false;
			if(o instanceof String){
				o = new File((String)o);
			}
			if(o instanceof File){
				File f = (File)o;
				if(!f.exists()){
					isBad = true;
				}
				if(!f.canRead()){
					isBad = true;
				}
				if(!f.isDirectory()){
					if(!isArchiveFile(f)){
						isBad = true;
					}
				}
			}
			if(!isBad){
				goodSources.add(o);
			}
		}
		
		return goodSources;
	}
			
	public SimpleClassLoader(Collection sources, ClassLoader parentClassLoader){
		//System.out.println(sources);
		this.sources = sources;
		this.parentClassLoader = parentClassLoader;
		for(Iterator en = sources.iterator(); en.hasNext(); ){
			Object src = en.next();
			if(src instanceof String){
				src = new File((String)src);
			}
			
			if(src instanceof File){
				checkSource((File)src);
			}
			else{
				throw new IllegalArgumentException("Unknown source: " + src);
			}
		}
		//Okay, all the sources seem to check out.
	}
	
	public void checkSource(File src){
		File f = (File)src;
		if(!f.exists()){
			throw new IllegalArgumentException("File or directory: " + f + " does not exist.");
		}
		if(!f.canRead()){
			throw new IllegalArgumentException("No read permission on file: " + f);
		}
		if(!f.isDirectory()){
			if(!isArchiveFile(f)){
				throw new IllegalArgumentException("File must be a directory or an archive.");
			}
		}
	}
	
	public void addSource(String src){
		addSource(new File(src));
	}
	
	public void addSource(File src){
		checkSource((File)src);
		sources.add(src);
	}
	
	public void addSources(Collection newSources){
		for(Iterator en = newSources.iterator(); en.hasNext(); ){
			Object src = en.next();
			if(src instanceof String){
				addSource((String)src);
			}
			else if(src instanceof File){
				addSource((File)src);
			}
		}	
	}
	
	protected static long getLastModified(File source){
		if(!source.exists()){
			throw new IllegalArgumentException("File or directory: " + source + " does not exist.");
		}
		if(!source.canRead()){
			throw new IllegalArgumentException("No read permission on file: " + source);
		}
		return source.lastModified();
	}
		
	public synchronized boolean needsReload(String className){
		//System.out.println("Determining if class: " + className + " needs a reload.");
		
		CacheEntry entry = (CacheEntry)classCache.get(className);
		if(entry == null){
			//System.out.println("No, this class hasn't ever been loaded at all.");
			return false;
		}
		else if(entry.source == null){
			//Was a system class or something
			//System.out.println("No, it was a system class or somesuch.");
			return false;
		}
		else{
			long newLastModified = getLastModified(entry.source);
			if(newLastModified != entry.lastModified){
				//System.out.println("class " + className + " needs a reload.");
				return true;
			}
			else{
				//System.out.println("No, it hasn't been modified.");
				return false;
			}
		}
	}
	
	public synchronized boolean needsReload(){
		for(Enumeration en = classCache.keys(); en.hasMoreElements(); ){
			String className = (String)en.nextElement();
			if(needsReload(className)){
				return true;
			}
		}
		return false;
	}
	
	public SimpleClassLoader createNewInstance(){
		return new SimpleClassLoader(sources, parentClassLoader);
	}

	protected static byte[] getStreamBytes(InputStream stream) throws IOException{
		byte[] buf = new byte[4096];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		while((len = stream.read(buf, 0, buf.length)) != -1){
			baos.write(buf, 0, len);
		}
		stream.close();
		baos.close();
		return baos.toByteArray();
	}
	
	private Class loadSystemClass(String name, boolean resolve) throws NoClassDefFoundError,
		ClassNotFoundException{
		if(parentClassLoader != null){
			return parentClassLoader.loadClass(name);
		}
		Class c = findSystemClass(name);
		
		CacheEntry cacheEntry = new CacheEntry();
		cacheEntry.actualClass = c;
		cacheEntry.source = null;
		cacheEntry.lastModified = 0L;
		classCache.put(name, cacheEntry);
		if(resolve){
			resolveClass(c);
		}
		return c;
	}
	
	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException{
		//System.out.println("Loading class: " + name);
		
		CacheEntry cacheEntry = (CacheEntry)classCache.get(name);
		if(cacheEntry != null){
			if(!needsReload(name)){
				//Found it in the cache
				Class c = cacheEntry.actualClass;
				if(resolve){
					resolveClass(c);
				}
				return c;
			}
		}
		//It wasn't in the cache.
		
		Class c = null;
		byte[] classBytes = null;
		cacheEntry = new CacheEntry();

		//System.out.println("loadclass, sources = " + sources);
		//Try to load the class from the given sources first, then from the system
		
		boolean isDesignatedSystemClass = false;
		if(name.startsWith("java.")){
			isDesignatedSystemClass = true;
		}
		if(name.startsWith("javax.")){
			isDesignatedSystemClass = true;
		}			
		if(name.startsWith("c2.fw")){
			isDesignatedSystemClass = true;
		}
		if(name.startsWith("com.sun")){
			isDesignatedSystemClass = true;
		}
		
		if(!isDesignatedSystemClass){
			for(Iterator en = sources.iterator(); en.hasNext(); ){
				Object o = en.next();
				File f = null;
				
				if(o instanceof String){
					f = new File((String)o);
				}
				else if(o instanceof File){
					f = (File)o;
				}
				
				if(!f.exists()){
					//throw new IllegalArgumentException("Source " + f + " does not exist.");
					continue;
				}
				
				//Figure out the file name of the class file.
				String fileName = name.trim();
				fileName = fileName.replace('.', File.separatorChar);
				fileName = fileName + ".class";
				
				//System.out.println("FileName = " + fileName);
								
				if(f.isDirectory()){
					if(!f.canRead()){
						continue;
						//throw new IllegalArgumentException("Can't read directory : " + f);
					}
					
					File classFile = new File(f, fileName);
					//System.out.println("ClassFile = " + classFile);
					if(classFile.exists()){
						if(!classFile.canRead()){
							//throw new IllegalArgumentException("Can't read file : " + f);
							continue;
						}
						try{
							FileInputStream fis = new FileInputStream(classFile);
							classBytes = getStreamBytes(fis);
							cacheEntry.source = classFile;
							cacheEntry.lastModified = classFile.lastModified();
							//We loaded it, so no need to go check other sources.
							break;
						}
						catch(IOException e){
							throw new ClassNotFoundException("I/O Error reading class : " + name);
						}
					}
				}
				else if(isArchiveFile(f)){
					if(!f.canRead()){
						//throw new IllegalArgumentException("Can't read archive : " + f);
						continue;
					}
					
					String zipFileName = name.trim();
					zipFileName = zipFileName.replace('.', '/');
					zipFileName = zipFileName + ".class";
					
					try{
						ZipFile zipFile = new ZipFile(f);
						ZipEntry zipEntry = zipFile.getEntry(zipFileName);
						if(zipEntry != null){
							InputStream is = zipFile.getInputStream(zipEntry);
							classBytes = getStreamBytes(is);
							cacheEntry.source = f;
							cacheEntry.lastModified = f.lastModified();
							//We found it
							break;
						}
					}
					catch(ZipException ze){
						//throw new IllegalArgumentException("Can't read archive : " + f);
						continue;
					}
					catch(IOException e){
						throw new ClassNotFoundException("I/O Error reading class : " + name);
					}
				}
			}
		}
		
		//Try to load the class from the system.
		if(classBytes == null){
			//System.out.println("Coldn't load class: " + name + ", loading from system.");
			c = loadSystemClass(name, resolve);
			if(c != null){
				//Resolve it if necessary
				if(resolve){
					resolveClass(c);
				}
				return c;
			}
		}
		
		if(classBytes != null){
			//System.out.println("Loading class: " + name + " from SimpleClassLoader.");
			Class loadedClass = defineClass(name, classBytes, 0, classBytes.length);
			//System.out.println("Done defining class: " + name + " from SimpleClassLoader.");
			
			cacheEntry.actualClass = loadedClass;
			classCache.put(name, cacheEntry);
			
			//Resolve!
			if(resolve){
				resolveClass(loadedClass);
			}
			
			return loadedClass;
		}
		throw new ClassNotFoundException();
	}
		
	
	public static boolean isArchiveFile(File f){
		ZipFile zf = null;
		try{
			zf = new ZipFile(f);
			int size = zf.size();
			zf.close();
			return true;
		}
		catch(Throwable t){
			try{
				if(zf != null){
					zf.close();
				}
			}
			catch(Throwable t2){}
			return false;
		}
	}

}

