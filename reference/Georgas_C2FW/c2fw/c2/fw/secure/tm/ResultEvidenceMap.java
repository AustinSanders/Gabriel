package c2.fw.secure.tm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)

/** 
 * Stores results and their evidences. 
 */
public class ResultEvidenceMap {
	static final public int DEFAULT_CAPACITY = 23;

	private int type = Utils.TRACK_NONE;

	private Map map;

	public ResultEvidenceMap(int trackType, int capacity) {
		type = trackType;
		map = new HashMap(capacity);
	}

	public ResultEvidenceMap() {
		this(Utils.TRACK_NONE, DEFAULT_CAPACITY);
	}

	public ResultEvidenceMap(int trackType) {
		this(trackType, DEFAULT_CAPACITY);
	}

	/*
	 * Add a Result-to-Evidence mapping.  
	 * returns true if this is a new Result, false if it is not
	 */
	public boolean putResultEvidence(Object result, Object evidence) {
		if (!map.containsKey(result)) { // New result
			switch (type) {
			case Utils.TRACK_NONE:
				map.put(result, null);
				break;
			case Utils.TRACK_ANY:
				map.put(result, evidence);
				break;
			case Utils.TRACK_ALL:
				Set eviSet = new HashSet(7);
				eviSet.add(evidence);
				map.put(result, eviSet);
				break;
			}
			return true;
		} else { // Result already exist
			if (type == Utils.TRACK_ALL) {
				((Set) map.get(result)).add(evidence);
			}
			return false;
		}
	}

	public boolean containsResult(Object result) {
		return map.containsKey(result);
	}

	public Set resultSet() {
		return map.keySet();
	}

	public String toString() {
		return map.toString();
	}
}
