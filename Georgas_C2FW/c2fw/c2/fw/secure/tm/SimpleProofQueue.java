package c2.fw.secure.tm;

import java.util.LinkedList;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
/**
 * SimpleProofQueue maintains two queues of nodes that have not been visited, 
 * one for forward, and one for backward.
 */
public class SimpleProofQueue implements ProofQueue {
	LinkedList forwardQueue = new LinkedList();

	LinkedList backwardQueue = new LinkedList();

	public boolean hasUnexploredForwardNodes() {
		return !forwardQueue.isEmpty();
	}

	public boolean hasUnexploredBackwardNodes() {
		return !backwardQueue.isEmpty();
	}

	public ProofNode nextUnexploredForwardNode() {
		return (ProofNode) forwardQueue.removeFirst();
	}

	public ProofNode nextUnexploredBackwardNode() {
		return (ProofNode) backwardQueue.removeFirst();
	}

	public void addForwardNode(ProofNode node) {
		forwardQueue.add(node);
	}

	public void addBackwardNode(ProofNode node) {
		backwardQueue.add(node);
	}
}
