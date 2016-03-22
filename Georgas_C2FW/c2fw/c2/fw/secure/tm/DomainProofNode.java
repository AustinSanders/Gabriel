package c2.fw.secure.tm;


//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
public class DomainProofNode extends AbstractProofNode {
	protected DomainProofNode(ProofGraph graph, Domain re, int trackType) {
		super(graph, re, trackType);
	}

	public void additionalBackwardProcess() {
		backwardSolutionAdded(this, getRoleExp());
	}

	public void additionalForwardProcess() {
	}
}
