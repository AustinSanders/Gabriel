/*
 * J.A.D.E. Java(TM) Addition to Default Environment.
 * Latest release available at http://jade.dautelle.com/
 * This class is public domain (not copyrighted).
 */
package c2.util;
import java.util.*;

/**
 * <p> This class provides cache access to <code>Map</code> collections.</p>
 * <p> Instance of this class can be used as "proxy" for any collection
 *     implementing the <code>java.util.Map</code> interface.</p>
 * <p> For example, <code>java.util.HashMap</code> access using
 *     this class is 50% faster! For slower collections (e.g. <code>
 *     java.util.TreeMap</code> or database access) the performance gain can be
 *     of several orders of magnitude.</p>
 * <p> Note: The keys used to access elements of a {@link CachedMap} do not
 *           need to be immutable as they are not stored in the cache
 *           (only keys specified by the {@link #put} method are).
 *           In other words, access can be performed using mutable keys as long
 *           as these keys can be compared for equality with the real map's keys
 *           (e.g. same <code>hashCode</code> values).</p>
 * <p> This implementation is not synchronized. Multiple threads accessing
 *     or modifying the collection must be synchronized externally.</p>
 *
 * <p><i> This class is <b>public domain</b> (not copyrighted).</i></p>
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, October 22, 2002
 */
public class CachedMap implements Map {
	
	/**
	 * Holds the map backing this collection.
	 */
	private final Map _backingMap;
	
	/**
	 * Holds the keys of the backing map (key-to-key mapping).
	 */
	private final Map _keysMap;
	
	/**
	 * Holds the cache's mask (capacity - 1).
	 */
	private final int _mask;
	
	/**
	 * Holds the keys being cached.
	 */
	private final Object[] _keys;
	
	/**
	 * Holds the values being cached.
	 */
	private final Object[] _values;
	
	/**
	 * Creates a cached map backed by a <code>java.util.HashMap</code>.
	 * The default cache size is set to <code>256</code> entries.
	 */
	public CachedMap() {
		this(256, new HashMap());
	}
	
	/**
	 * Creates a cached map backed by a <code>java.util.HashMap</code> and
	 * having the specified cache size.
	 *
	 * @param  cacheSize the cache size, the actual cache size is the
	 *         first power of 2 greater or equal to <code>cacheSize</code>.
	 *         This is also the initial capacity of the backing <code>
	 *         java.util.HashMap</code>.
	 */
	public CachedMap(int cacheSize) {
		this(cacheSize, new HashMap(cacheSize));
	}
	
	/**
	 * Creates a cached map backed by the specified map and having the specified
	 * cache size. In order to maitain cache veracity, it is critical
	 * that <b>all</b> update to the backing map is accomplished through the
	 * {@link CachedMap} instance; otherwise {@link #flush} has to be called.
	 *
	 * @param  cacheSize the cache size, the actual cache size is the
	 *         first power of 2 greater or equal to <code>cacheSize</code>.
	 * @param  backingMap the backing map to be "wrapped" in a cached map.
	 */
	public CachedMap(int cacheSize, Map backingMap) {
		
		// Find a power of 2 >= minimalCache
		int actualCacheSize = 1;
		while (actualCacheSize < cacheSize) {
			actualCacheSize <<= 1;
		}
		
		// Populates keys from backing map.
		_keysMap = new HashMap();
		for (Iterator i= backingMap.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			_keysMap.put(key, key);
		}
		
		_backingMap = backingMap;
		_mask = actualCacheSize - 1;
		_keys = new Object[actualCacheSize];
		_values = new Object[actualCacheSize];
	}
	
	/**
	 * Returns the actual cache size.
	 *
	 * @return the cache size (power of 2).
	 */
	public int getCacheSize() {
		return _keys.length;
	}
	
	/**
	 * Returns the backing map. If the backing map is modified directly,
	 * this {@link CachedMap} has to be flushed.
	 *
	 * @param  map the backing map to be "wrapped" in a cached map.
	 * @see    #flush
	 */
	public Map getBackingMap() {
		return _backingMap;
	}
	
	/**
	 * Flushes the key/value pairs being cached. This method should be called
	 * if the backing map is externally modified.
	 */
	public void flush() {
		for (int i=0; i < _keys.length; i++) {
			_keys[i] = null;
			_values[i] = null;
		}
		
		// Re-populates keys from backing map.
		_keysMap.clear();
		for (Iterator i= _backingMap.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			_keysMap.put(key, key);
		}
	}
	
	/**
	 * Returns the value to which this map maps the specified key.
	 * First, the cache is being checked, then if the cache does not contains
	 * the specified key, the backing map is accessed and the key/value
	 * pair is stored in the cache.
	 *
	 * @param  key the key whose associated value is to be returned.
	 * @return the value to which this map maps the specified key, or
	 *	       <code>null</code> if the map contains no mapping for this key.
	 * @throws ClassCastException if the key is of an inappropriate type for
	 *         the backing map (optional).
	 * @throws NullPointerException if the key is <code>null</code>.
	 */
	public Object get(Object key) {
		int index = key.hashCode() & _mask;
		if (key.equals(_keys[index]) ) {
			return _values[index];
		} else { // Cache miss.
			Object value = _backingMap.get(key);
			_keys[index] = _keysMap.get(key);
			_values[index] = value;
			return value;
		}
	}
	
	/**
	 * Associates the specified value with the specified key in this map.
	 *
	 * @param  key the key with which the specified value is to be associated.
	 * @param  value the value to be associated with the specified key.
	 * @return the previous value associated with specified key, or
	 *         <code>null</code> if there was no mapping for the key.
	 * @throws UnsupportedOperationException if the <code>put</code> operation
	 *         is not supported by the backing map.
	 * @throws ClassCastException if the class of the specified key or value
	 * 	       prevents it from being stored in this map.
	 * @throws IllegalArgumentException if some aspect of this key or value
	 *	       prevents it from being stored in this map.
	 * @throws NullPointerException if the key is <code>null</code>.
	 */
	public Object put(Object key, Object value) {
		// Updates the cache.
		int index = key.hashCode() & _mask;
		if (key.equals(_keys[index]) ) {
			_values[index] = value;
		} else {  // Possibly new key.
			_keysMap.put(key, key);
		}
		
		// Updates the backing map.
		return _backingMap.put(key, value);
	}
	
	/**
	 * Removes the mapping for this key from this map if it is present.
	 *
	 * @param  key key whose mapping is to be removed from the map.
	 * @return previous value associated with specified key,
	 *         or <code>null</code> if there was no mapping for key.
	 * @throws ClassCastException if the key is of an inappropriate type for
	 * 	       the backing map (optional).
	 * @throws NullPointerException if the key is <code>null</code>.
	 * @throws UnsupportedOperationException if the <code>remove</code> method
	 *         is not supported by the backing map.
	 */
	public Object remove(Object key) {
		// Removes from cache.
		int index = key.hashCode() & _mask;
		if (key.equals(_keys[index]) ) {
			_keys[index] = null;
		}
		_keysMap.remove(key);
		
		// Removes from backing map.
		return _backingMap.remove(key);
	}
	
	/**
	 * Indicates if this map contains a mapping for the specified key.
	 *
	 * @param   key the key whose presence in this map is to be tested.
	 * @return <code>true</code> if this map contains a mapping for the
	 *         specified key; <code>false</code> otherwise.
	 */
	public boolean containsKey(Object key) {
		// Checks the cache.
		int index = key.hashCode() & _mask;
		if (key.equals(_keys[index]) ) {
			return true;
		} else { // Checks the key map.
			return _keysMap.containsKey(key);
		}
	}
	
	/**
	 * Returns the number of key-value mappings in this map.  If the
	 * map contains more than <code>Integer.MAX_VALUE</code> elements,
	 * returns <code>Integer.MAX_VALUE</code>.
	 *
	 * @return the number of key-value mappings in this map.
	 */
	public int size() {
		return _keysMap.size();
	}
	
	/**
	 * Returns <code>true</code> if this map contains no key-value mappings.
	 *
	 * @return <code>true</code> if this map contains no key-value mappings.
	 */
	public boolean isEmpty() {
		return _keysMap.isEmpty();
	}
	
	/**
	 * Returns <code>true</code> if this map maps one or more keys to the
	 * specified value.
	 *
	 * @param  value value whose presence in this map is to be tested.
	 * @return <code>true</code> if this map maps one or more keys to the
	 *         specified value.
	 * @throws ClassCastException if the value is of an inappropriate type for
	 * 	       the backing map (optional).
	 * @throws NullPointerException if the value is <code>null</code> and the
	 *         backing map does not not permit <code>null</code> values.
	 */
	public boolean containsValue(Object value) {
		return _backingMap.containsValue(value);
	}
	
	/**
	 * Copies all of the mappings from the specified map to this map
	 * (optional operation).  This method automatically flushes the cache.
	 *
	 * @param map the mappings to be stored in this map.
	 * @throws UnsupportedOperationException if the <code>putAll</code> method
	 *         is not supported by the backing map.
	 * @throws ClassCastException if the class of a key or value in the
	 * 	       specified map prevents it from being stored in this map.
	 * @throws IllegalArgumentException some aspect of a key or value in the
	 *	       specified map prevents it from being stored in this map.
	 * @throws NullPointerException the specified map is <code>null</code>, or
	 *         if the backing map does not permit <code>null</code> keys or
	 *         values, and the specified map contains <code>null</code> keys or
	 *         values.
	 */
	public void putAll(Map map) {
		_backingMap.putAll(map);
		flush();
	}
	
	/**
	 * Removes all mappings from this map (optional operation). This method
	 * automatically flushes the cache.
	 *
	 * @throws UnsupportedOperationException if clear is not supported by the
	 * 	       backing map.
	 */
	public void clear() {
		_backingMap.clear();
		flush();
	}
	
	/**
	 * Returns an <b>unmodifiable</b> view of the keys contained in this
	 * map.
	 *
	 * @return an unmodifiable view of the keys contained in this map.
	 */
	public Set keySet() {
		return Collections.unmodifiableSet(_backingMap.keySet());
	}
	
	/**
	 * Returns an <b>unmodifiable</b> view of the values contained in this map.
	 *
	 * @return an unmodifiable view of the values contained in this map.
	 */
	public Collection values() {
		return Collections.unmodifiableCollection(_backingMap.values());
	}
	
	/**
	 * Returns an <b>unmodifiable</b> view of the mappings contained in this
	 * map.  Each element in the returned set is a <code>Map.Entry</code>.
	 *
	 * @return an unmodifiable view of the mappings contained in this map.
	 */
	public Set entrySet() {
		return Collections.unmodifiableSet(_backingMap.entrySet());
	}
	
	
	/**
	 * Compares the specified object with this map for equality.  Returns
	 * <tt>true</tt> if the given object is also a map and the two Maps
	 * represent the same mappings.
	 *
	 * @param  o object to be compared for equality with this map.
	 * @return <code>true</code> if the specified object is equal to this map.
	 */
	public boolean equals(Object o) {
		return _backingMap.equals(o);
	}
	
	/**
	 * Returns the hash code value for this map.
	 *
	 * @return the hash code value for this map.
	 */
	public int hashCode() {
		return _backingMap.hashCode();
	}
}