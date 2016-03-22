package c2.fw.secure.tm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// The inference engine classes are based on Professor Ninghui Li's code. 
// (http://www.cs.purdue.edu/people/ninghui)
public abstract class AbstractProofNode implements ProofNode {
	/** 
	 * The RoleExpression this node is about. 
	 */
	protected RoleExpression roleExp;

	/** 
	 * The proof graph this node is in, this reference is used while 
	 * processing this node 
	 */
	protected ProofGraph graph;

	/** 
	 * Nodes that can reach this node directly, evidences are credentials. 
	 */
	protected ResultEvidenceMap parents;

	/** 
	 * Nodes that this node can reach directly, evidecens are credentials. 
	 */
	protected ResultEvidenceMap children;

	/** 
	 * Backward solutions on this node  
	 */
	protected ResultEvidenceMap backwardSolutions;

	/** 
	 * Forward solutions on this node 
	 */
	protected ResultEvidenceMap forwardSolutions;

	protected List backwardListeners;

	protected List forwardListeners;

	boolean backwardProcessed;

	boolean forwardProcessed;

	AbstractProofNode(ProofGraph graph, RoleExpression re, int trackType) {
		this.graph = graph;
		roleExp = re;
		Utils.debugInfo("A new node is created for " + re);

		parents = new ResultEvidenceMap(trackType);
		children = new ResultEvidenceMap(trackType);
		backwardSolutions = new ResultEvidenceMap(trackType);
		forwardSolutions = new ResultEvidenceMap(trackType);

		backwardListeners = new ArrayList(10);
		forwardListeners = new ArrayList(10);
	}

	public ProofGraph getGraph() {
		return graph;
	}

	public RoleExpression getRoleExp() {
		return roleExp;
	}

	public ResultEvidenceMap getForwardSolutions() {
		return forwardSolutions;
	}

	public ResultEvidenceMap getBackwardSolutions() {
		return backwardSolutions;
	}

	public void invalidateForward() {
		Utils.debugInfo("Invalidate forward result on " + this);
		forwardProcessed = false;
	}

	public void invalidateBackward() {
		Utils.debugInfo("Invalidate backward result on " + this);
		backwardProcessed = false;
	}

	public void backwardProcess() {
		if (backwardProcessed) {
			return;
		}
		Utils.debugInfo("Backward processing " + this);
		additionalBackwardProcess();
		backwardProcessed = true;
	}

	// The method that does the work when we visit a node
	public void forwardProcess() {
		if (forwardProcessed) {
			return;
		}

		// first step: go over all credentials that have this as roleExpression
		Utils.debugInfo("Forward processing " + this);
		Iterator credIt = graph.findCredentialsByRoleExpression(roleExp);
		while (credIt.hasNext()) {
			StaticCredential credential = (StaticCredential) credIt.next();
			Utils.debugInfo("Find one credential: " + credential);
			ProofNode node = graph.addForwardNode(credential.getDefinedRole());
			node.addParent(this, credential);
			//_graph.addProofEdge(this, node, credential);
		}

		additionalForwardProcess();
		forwardProcessed = true;
	}

	abstract protected void additionalForwardProcess();

	abstract protected void additionalBackwardProcess();

	public void addBackwardListener(BackwardSolutionListener sl) {
		Utils.debugInfo(sl + " is now listenering on " + this
				+ " for backward solutions");
		backwardListeners.add(sl);
		propagateBackwardSolutionsTo(sl);
	}

	public void addForwardListener(ForwardSolutionListener sl) {
		Utils.debugInfo(sl + " is now listenering on " + this
				+ " for forward solutions");
		forwardListeners.add(sl);
		propagateForwardSolutionsTo(sl);
	}

	public void addParent(ProofNode node, Object evidence) {
		if (parents.putResultEvidence(node.getRoleExp(), evidence)) {
			addForwardListener(node);
		}
	}

	public void addChild(ProofNode node, Object evidence) {
		if (children.putResultEvidence(node.getRoleExp(), evidence)) {
			addBackwardListener(node);
		}
	}

	public void backwardSolutionAdded(ProofNode s, BackwardSolution r) {
		if (backwardSolutions.putResultEvidence(r, s)) { // when solution r is new
			Utils.debugInfo("Backward solution added to: " + this + " from: "
					+ s + " value: " + r);
			propagateBackwardSolution(r);
		}
	}

	public void forwardSolutionAdded(ProofNode s, ForwardSolution r) {
		if (forwardSolutions.putResultEvidence(r, s)) {
			Utils.debugInfo("Forward solution added to: " + this + " from: "
					+ s + " value: " + r);
			propagateForwardSolution(r);
		}
	}

	protected void propagateBackwardSolutionsTo(
			BackwardSolutionListener listener) {
		Object[] sols = backwardSolutions.resultSet().toArray();
		for (int i = 0; i < sols.length; i++) {
			listener.backwardSolutionAdded(this, (BackwardSolution) (sols[i]));
		}
	}

	protected void propagateForwardSolutionsTo(ForwardSolutionListener listener) {
		Object[] sols = forwardSolutions.resultSet().toArray();
		for (int i = 0; i < sols.length; i++) {
			listener.forwardSolutionAdded(this, (ForwardSolution) (sols[i]));
		}
	}

	protected void propagateBackwardSolution(BackwardSolution g) {
		Object[] listeners = backwardListeners.toArray();
		for (int i = 0; i < listeners.length; i++) {
			((BackwardSolutionListener) listeners[i]).backwardSolutionAdded(
					this, g);
		}
	}

	protected void propagateForwardSolution(ForwardSolution g) {
		Object[] listeners = forwardListeners.toArray();
		for (int i = 0; i < listeners.length; i++) {
			((ForwardSolutionListener) listeners[i]).forwardSolutionAdded(this,
					g);
		}
	}

	public String toString() {
		return "node " + roleExp;
	}
}

