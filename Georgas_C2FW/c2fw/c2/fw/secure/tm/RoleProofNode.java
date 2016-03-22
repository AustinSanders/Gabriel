package c2.fw.secure.tm;

import java.util.Iterator;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)

/** 
 * A role proof node is a proof node that is centered around a simple role. 
 */
class RoleProofNode extends AbstractProofNode {
	private ForwardLinkingMonitor monitor;

	protected RoleProofNode(ProofGraph graph, RoleDecentralized re, int trackType) {
		super(graph, re, trackType);
	}

	public void additionalBackwardProcess() {
		Iterator credIt = getGraph().findCredentialsDefiningRole(
				(RoleDecentralized) getRoleExp());
		while (credIt.hasNext()) {
			StaticCredential credential = (StaticCredential) credIt.next();
			ProofNode node = getGraph()
					.addBackwardNode(credential.getRoleExpression());
			node.addChild(this, credential);
		}

		// In the original RBTM framework, a backward solution is the entities
		// that can act as the roles. Since we use the framework to answer the 
		// query of "which roles are trusted by this role?", the solutions are 
		// not the terminal entities. 
		//
		// The entities are renamed as domains, a 
		// term better describes their relationships with other RBAC concepts.
		// and we will not supply the entities for credentials, since those 
		// entities(domains) become the defining owners for RBAC's roles and 
		// users. They are no longer the final link of access control. (Users
		// are.) A possibility of treating the entity(domain) as a type of 
		// user of RBAC has been considered, but we feel that the domain should
		// be an independent concept, and we should keep the RBTM framework 
		// for inter-domain trust use, and use the RBAC for the intra domain
		// management. 
		//
		// Based on this adptation, the backward solution would be the set of 
		// decentralized roles that can act for the role in query. These would
		// be the intermediate roles in the original backward search process. 
		// A more straighward transative closure solution is possible, but 
		// reusing the code seems working. 
		backwardSolutionAdded(this, getRoleExp());
	}

	/**
	 * Role-specific extension which marks itself as a solution to itself, and
	 * sets up a ForwardLinkingMonitor to check for new solutions.
	 */
	public void additionalForwardProcess() {
		RoleDecentralized thisRole = (RoleDecentralized) getRoleExp();
		// Make itself a forward solution to itself
		forwardSolutionAdded(this, thisRole);

		if (monitor == null) {
			ProofNode node = getGraph().addForwardNode(thisRole.getDomain());
			monitor = new ForwardLinkingMonitor();
			node.addForwardListener(monitor);
		}
	}

	/**
	 * Internal class which, whenever the solution is not an 'RPermission', goes
	 * ahead and creates a new linked role 
	 */
	class ForwardLinkingMonitor implements ForwardSolutionListener {
		public void forwardSolutionAdded(ProofNode s, ForwardSolution sol) {
			if (sol instanceof RoleDecentralized) {
				RoleDecentralized role = (RoleDecentralized) sol;
				LinkedRole linkedRole = new LinkedRole(role,
						((RoleDecentralized) getRoleExp()).getName());
				ProofNode node = getGraph().addForwardNode(linkedRole);
				node.addParent(RoleProofNode.this, s);
			}
		}

		public String toString() {
			return "Monitor " + getRoleExp();
		}
	}
}
