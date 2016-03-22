package c2.fw.secure.tm;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)

public class LinkedRoleProofNode extends AbstractProofNode {
	private BackwardLinkingMonitor monitor;

	protected LinkedRoleProofNode(ProofGraph graph, LinkedRole re, int trackType) {
		super(graph, re, trackType);
	}

	public void additionalBackwardProcess() {
		if (monitor != null) {
			return;
		}
		RoleDecentralized firstRole = ((LinkedRole) getRoleExp()).getFirstRole();
		ProofNode node = getGraph().addBackwardNode(firstRole);
		monitor = new BackwardLinkingMonitor();
		node.addBackwardListener(monitor);
	}

	class BackwardLinkingMonitor implements BackwardSolutionListener {
		public void backwardSolutionAdded(ProofNode s, BackwardSolution re) {
			if (re instanceof Domain) {
				RoleDecentralized r = new RoleDecentralizedImpl((Domain) re, 
						((LinkedRole) getRoleExp()).getSecondRoleName());
				ProofNode node = getGraph().addBackwardNode(r);
				node.addChild(LinkedRoleProofNode.this, s);
			}
		}

		public String toString() {
			return "Monitor " + getRoleExp();
		}
	}

	public void additionalForwardProcess() {
	}
}
