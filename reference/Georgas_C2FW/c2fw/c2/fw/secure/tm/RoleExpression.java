package c2.fw.secure.tm;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)

/**
 * An RoleExpression can be: a Domain, a RoleDecentralized, a LinkedRole, or an Intersection.
 * Right now, it is just a marker interface
 */
public interface RoleExpression extends ForwardSolution, BackwardSolution,
		java.io.Serializable {
}
