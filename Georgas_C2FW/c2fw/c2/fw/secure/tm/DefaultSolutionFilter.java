package c2.fw.secure.tm;


//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
public class DefaultSolutionFilter implements SolutionFilter,
		java.io.Serializable {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	public ForwardSolution getForwardSolution(RoleExpression re) {
		if (re instanceof RoleDecentralized) {
			return re;
		}
		return null;
	}

	public BackwardSolution getBackwardSolution(RoleExpression re) {
		if (re instanceof Domain) {
			return re;
		}
		return null;
	}
}
