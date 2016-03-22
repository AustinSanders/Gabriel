package c2.fw.secure.tm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
/**
 * CredentialManager stores credentials and answer queries.
 */

public class CredentialManager implements java.io.Serializable, ProofGraph {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	static final public Iterator emptyIt = new ArrayList(1).iterator();

	static final public Collection emptyCollection = new ArrayList(1);

	// stores all the credentials
	private Set credentials = new HashSet();

	// an index from domains to credential collections
	private Map indexByDomain = new HashMap();

	// an index from roles to credential collections
	private Map indexByRole = new HashMap();

	// an index from roleExpressions to collections of credentials
	private Map indexByRoleExpression = new HashMap();

	private int track;

	private SolutionFilter solutionFilter;

	// a proof queue
	transient private ProofQueue proofQueue = new SimpleProofQueue();

	transient private Map nodeTable = new HashMap();

	public CredentialManager(Set credSet, int trackType,
			SolutionFilter solFilter) {
		this(trackType, solFilter);
		addCredentials(credSet);
	}

	public CredentialManager(int trackType, SolutionFilter solFilter) {
		track = trackType;
		solutionFilter = solFilter;
	}

	public CredentialManager() {
		this(Utils.TRACK_NONE, new DefaultSolutionFilter());
	}

	public void reinit() {
		proofQueue = new SimpleProofQueue();
		nodeTable = new HashMap();
	}

	synchronized public void addCredentials(Collection credentials) {
		reinit();
		Iterator credIt = credentials.iterator();
		while (credIt.hasNext()) {
			addCredential((StaticCredential) credIt.next());
		}
	}

	synchronized public void removeCredentials(Collection credentials) {
		reinit();
		Iterator credIt = credentials.iterator();
		while (credIt.hasNext()) {
			removeCredential((StaticCredential) credIt.next());
		}
	}

	synchronized public boolean addCredential(StaticCredential cred) {
		// Make sure that no duplicate Credential exists
		if (credentials.contains(cred)) {
			Utils.debugInfo("trying to add existing credential: " + cred);
			return false;
		}
		Utils.debugInfo("adding credential: " + cred);
		credentials.add(cred);
		addIndexFor(cred);
		ProofNode node = getNode(cred.getDefinedRole());
		if (node != null) {
			node.invalidateBackward();
		}
		node = getNode(cred.getRoleExpression());
		if (node != null) {
			node.invalidateForward();
		}
		return true;
	}

	synchronized public void removeCredential(StaticCredential cred) {
		if (credentials.contains(cred)) {
			Utils.debugInfo("removing credential: " + cred);
			reinit();
			credentials.remove(cred);
			removeIndexFor(cred);
		} else {
			Utils.debugInfo("trying to remove nonexistent credential: " + cred);
			//// throw Exception
		}
	}

	synchronized public boolean containsCredential(StaticCredential cred) {
		return credentials.contains(cred);
	}
	
	private void addIndexFor(StaticCredential cred) {
		Domain e = cred.getDomain();
		List credList = (List) indexByDomain.get(e);
		if (credList == null) {
			credList = new ArrayList(40);
			indexByDomain.put(e, credList);
		}
		credList.add(cred);

		// Code to deal with indexByRole
		RoleDecentralized r = cred.getDefinedRole();
		credList = (List) indexByRole.get(r);
		if (credList == null) {
			credList = new ArrayList(10);
			indexByRole.put(r, credList);
		}
		credList.add(cred);

		RoleExpression roleExpression = cred.getRoleExpression();
		credList = (List) indexByRoleExpression.get(roleExpression);
		if (credList == null) {
			credList = new ArrayList(10);
			indexByRoleExpression.put(roleExpression, credList);
		}
		credList.add(cred);
	}

	private void removeIndexFor(StaticCredential cred) {
		((List) indexByDomain.get(cred.getDomain())).remove(cred);
		((List) indexByRole.get(cred.getDefinedRole())).remove(cred);
		((List) indexByRoleExpression.get(cred.getRoleExpression())).remove(cred);
	}

	synchronized public Collection getCredentialsIssuedBy(Domain e) {
		Collection credentials = (Collection) indexByDomain.get(e);
		if (credentials == null) {
			return emptyCollection;
		} else {
			return credentials;
		}
	}

	synchronized public Iterator findCredentialsDefiningRole(RoleDecentralized r) {
		List credList = (List) indexByRole.get(r);
		if (credList == null) {
			return emptyIt;
		} else {
			return credList.iterator();
		}
	}

	synchronized public Iterator findCredentialsByRoleExpression(
			RoleExpression roleExpression) {
		List credList = (List) indexByRoleExpression.get(roleExpression);
		if (credList == null) {
			return emptyIt;
		} else {
			return credList.iterator();
		}
	}

	/**
	 * Find out all Roles that roleExp belongs to.
	 *
	 * @param roleExp the role expression that, whenever it is on the right
	 * hand side of a credential statement, the left hand side will be returned.
	 */
	synchronized public ResultEvidenceMap forwardSearch(RoleExpression roleExp) {
		Utils.debugInfo("Brian: CredentialManager.forwardSearch");
		ProofNode goalNode = addForwardNode(roleExp);
		while (proofQueue.hasUnexploredForwardNodes()) {
			proofQueue.nextUnexploredForwardNode().forwardProcess();
		}
		return goalNode.getForwardSolutions();
	}

	// Find out all members of roleExp
	synchronized public ResultEvidenceMap backwardSearch(
			RoleExpression roleExp) {
		ProofNode goalNode = addBackwardNode(roleExp);
		while (proofQueue.hasUnexploredBackwardNodes()) {
			proofQueue.nextUnexploredBackwardNode().backwardProcess();
		}
		return goalNode.getBackwardSolutions();
	}

	public ProofNode addForwardNode(RoleExpression roleExp) {
		ProofNode node = addNode(roleExp);
		proofQueue.addForwardNode(node);
		return node;
	}

	public ProofNode addBackwardNode(RoleExpression roleExp) {
		ProofNode node = addNode(roleExp);
		proofQueue.addBackwardNode(node);
		return node;
	}

	/**
	 * Add a node corresponding to a role expression, make sure that the 
	 * node is not added twice. 
	 * @param roleExp the expression associated with the node to be added.
	 * @return the node associated with that expression. This may not be a new
	 * node, if it was already present in the node table.
	 */
	private ProofNode addNode(RoleExpression roleExp) {
		ProofNode node = (ProofNode) nodeTable.get(roleExp);
		if (node == null) {
			node = createProofNode(roleExp);
			nodeTable.put(roleExp, node);
		}
		return node;
	}

	private ProofNode getNode(RoleExpression roleExp) {
		return (ProofNode) nodeTable.get(roleExp);
	}

	/**
	 * Create a proof node of the appropriate type. That is, it tries to figure
	 * out what type of element roleExp really is, and create an appropriate
	 * ProofNode for it.
	 * 
	 * @param roleExp an expression that will be the basis for the new node.
	 * @return a proof node corresponding to the role expression. 
	 */
	private ProofNode createProofNode(RoleExpression roleExp) {
		if (roleExp instanceof RoleDecentralized) {
			return new RoleProofNode(this, (RoleDecentralized) roleExp, track);
		} else if (roleExp instanceof LinkedRole) {
			return new LinkedRoleProofNode(this, (LinkedRole) roleExp, track);
		} else if (roleExp instanceof Intersection) {
			return null; //new IntersectionProofNode(this, (Intersection)roleExp, track);
		} else {
			return new DomainProofNode(this, (Domain) roleExp, track);
		}
	}

	public ForwardSolution getForwardSolution(RoleExpression re) {
		return solutionFilter.getForwardSolution(re);
	}

	public BackwardSolution getBackwardSolution(RoleExpression re) {
		return solutionFilter.getBackwardSolution(re);
	}

	public String toString() {
		getRoleExpressions();
		return "credentials=" + credentials.toString() + "\n"
				+ "indexByDomain=" + indexByDomain.toString() + "\n"
				+ "indexByRole=" + indexByRole.toString() + "\n"
				+ "indexByRoleExpression=" + indexByRoleExpression.toString();
	}

	private String getRoleExpressions() {
		StringBuffer outBuf = new StringBuffer();
		Iterator keyIt = indexByRoleExpression.keySet().iterator();
		while (keyIt.hasNext()) {
			Object o = keyIt.next();
			if (o instanceof RoleDecentralized) {
				outBuf.append("Role ").append(o.toString()).append("\n");
				RoleDecentralized r = (RoleDecentralized) o;
				outBuf.append("Base is ").append(r.getDomain().getClass())
						.append("\n");
				outBuf.append("RoleName is ").append(r.getName().getClass())
						.append("\n");
			}
		}
		return outBuf.toString();
	}
}
