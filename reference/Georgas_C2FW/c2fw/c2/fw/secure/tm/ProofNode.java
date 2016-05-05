package c2.fw.secure.tm;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
public interface ProofNode extends BackwardSolutionListener,
		ForwardSolutionListener {
	RoleExpression getRoleExp();

	void addBackwardListener(BackwardSolutionListener sl);

	/**
	 * Process this node for search in the backward direction. The
	 * implementation of this method should find all nodes that can reach this
	 * node directly and do appropriate things.
	 */
	void backwardProcess();

	void invalidateForward();

	void invalidateBackward();

	void addForwardListener(ForwardSolutionListener sl);

	void forwardProcess();

	void addParent(ProofNode node, Object evidence);

	void addChild(ProofNode node, Object evidence);

	ResultEvidenceMap getForwardSolutions();

	ResultEvidenceMap getBackwardSolutions();

}
