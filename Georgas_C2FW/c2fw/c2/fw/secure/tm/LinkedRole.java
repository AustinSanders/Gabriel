package c2.fw.secure.tm;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.NameImpl;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
/**
 * Represent a LinkedRole. A linked role is expressed as 
 * <samp><pre>R.a <- S.b.c</pre></samp>; parsed another way, it is
 * <samp><pre>R.a <- (S.b) c</pre></samp> where S.b is a role, and c is the
 * name of another role. Every user in S.b's transitive closure will have
 * the rolename c appended to it to form a new role, and every member of that
 * role will belong to R.a.
 */
public class LinkedRole implements RoleExpression {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	private RoleDecentralized firstRole;

	private Name secondName;

	public LinkedRole(RoleDecentralized r, Name n) {
		firstRole = r;
		secondName = n;
	}

	public LinkedRole(RoleDecentralized r, String n) {
		firstRole = r;
		secondName = new NameImpl(n);
	}

	public LinkedRole(Domain domain, String n1, String n2) {
		firstRole = new RoleDecentralizedImpl(domain.getName(), new NameImpl(n1));
		secondName = new NameImpl(n2);
	}

	public LinkedRole(String domain, String n1, String n2) {
		firstRole = new RoleDecentralizedImpl(domain, n1);
		secondName = new NameImpl(n2);
	}

	public int hashCode() {
		return firstRole.hashCode() * 2 + secondName.hashCode();
	}

	public boolean equals(Object o) {
		return (o instanceof LinkedRole)
				&& firstRole.equals(((LinkedRole) o).firstRole)
				&& secondName.equals(((LinkedRole) o).secondName);
	}

	public String toString() {
		return firstRole.toString() + "." + secondName.toString();
	}

	public RoleDecentralized getFirstRole() {
		return firstRole;
	}

	public Name getSecondRoleName() {
		return secondName;
	}
}
