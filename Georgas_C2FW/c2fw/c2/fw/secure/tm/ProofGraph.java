package c2.fw.secure.tm;

import java.util.Iterator;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
/**
 * The class AbstractProofGraph represents a proof graph. It 1) makes sure that
 * the same node is not added more than once 2) makes sure that the same edge is
 * not added more than once If one wants to implement priority queue, one should
 * extend this class.
 */
interface ProofGraph extends SolutionFilter {
	ProofNode addForwardNode(RoleExpression roleExp);

	ProofNode addBackwardNode(RoleExpression roleExp);

	Iterator findCredentialsByRoleExpression(RoleExpression re);

	Iterator findCredentialsDefiningRole(RoleDecentralized r);
}