package c2.util;

import java.util.*;

/**
 * Objects of this class are used to synchronize on a set (array) of
 * objects, as opposed to a single object.  A target thread can
 * <CODE>waitFor()</CODE> a set of objects.  When any one of these
 * objects is <CODE>notifyWaiters()</CODE>ed, the thread wakes up.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public class SetSynchronizer{

	private HashMap map;
	private HashMap reverseMap;
	private Object mapLock = new Object();
	private LockPool lockPool = new LockPool();
	
	public SetSynchronizer(){
		map = new HashMap();
		reverseMap = new HashMap();
	}
	
	public void waitFor(Object[] objArr) throws InterruptedException{
		//Wait on any one of these objects
		//Object lock = new Object();
		Object lock = lockPool.getLock();
		
		//Add the lock to the lock list of each
		//thing the thread wants to watch.
		synchronized(mapLock){
			reverseMap.put(lock, objArr);
			for(int i = 0; i < objArr.length; i++){
				HashSet lockList = (HashSet)map.get(objArr[i]);
				if(lockList == null){
					lockList = new HashSet();
				}
				synchronized(lockList){
					lockList.add(lock);
				}
				map.put(objArr[i], lockList);
			}
		}
		synchronized(lock){
			//Block the current thread on the lock.
			//System.out.println(map);
			lock.wait();
		}
	}
	
	public synchronized void notifyWaiters(Object o){
		//Wake up any thread that is waiting for this object
		Object[] locksToNotify;
		synchronized(mapLock){
			HashSet lockList = (HashSet)map.get(o);
			if((lockList == null) || (lockList.size() == 0)){
				//Nobody is waiting on this object.  Return;
				return;
			}
			
			locksToNotify = lockList.toArray();
			
			for(int i = 0; i < locksToNotify.length; i++){
				Object lock = locksToNotify[i];
				//Remove that lock from any lists it is on
				Object[] baseObjects = (Object[])reverseMap.get(lock);
				for(int j = 0; j < baseObjects.length; j++){
					HashSet individualLockList = (HashSet)map.get(baseObjects[j]);
					individualLockList.remove(lock);
				}
				reverseMap.remove(lock);
			}
		}
		
		for(int i = 0; i < locksToNotify.length; i++){
			synchronized(locksToNotify[i]){
				locksToNotify[i].notifyAll();
				lockPool.returnLock(locksToNotify[i]);
			}
		}
	}

}

