package c2.fw.secure.tm;

import c2.fw.secure.rbac.Name;
import c2.fw.secure.rbac.NameImpl;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
/**
 * StaticCredential is a credential of the form role <- roleExpression. The role is of
 * type 'RoleDecentralized' (formed by a domain, and a rolename), and roleExpression is of type
 * 'RoleExpression'.
 * 
 * @see RoleDecentralized
 * @see RoleExpression
 */
public class StaticCredential extends Credential {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	private RoleDecentralized role;

	private RoleExpression roleExpression;

	public StaticCredential(RoleDecentralized r, RoleExpression s) {
		role = r;
		roleExpression = s;
	}

	public StaticCredential(Domain domain, Name n, RoleExpression s) {
		role = new RoleDecentralizedImpl(domain, n);
		roleExpression = s;
	}

	public StaticCredential(String e, String rn, String es)
			throws CredentialParsingException {
		role = new RoleDecentralizedImpl(new DomainImpl(e), new NameImpl(rn));
		roleExpression = StaticCredential.getRoleExpression(es);
	}

	public Domain getDomain() {
		return role.getDomain();
	}

	public RoleDecentralized getDefinedRole() {
		return role;
	}

	public RoleExpression getRoleExpression() {
		return roleExpression;
	}

	public String toString() {
		return role.toString() + "<-" + roleExpression.toString();
	}

	public int hashCode() {
		return role.hashCode() * 2 + roleExpression.hashCode();
	}

	public boolean equals(Object o) {
		return (o instanceof StaticCredential)
				&& role.equals(((StaticCredential) o).role)
				&& roleExpression.equals(((StaticCredential) o).roleExpression);
	}

	// convert a String into an RoleExpression, only deals with RoleDecentralized
	// and LinkedRole for now
	static RoleExpression getRoleExpression(String str)
			throws CredentialParsingException {
		int i = 0;
		String[] names = new String[3];
		int n = 0;
		while (i < str.length()) {
			int j = str.indexOf(".", i);
			if (j == -1) {
				j = str.length();
			}
			names[n++] = str.substring(i, j).trim();
			i = j + 1;
		}
		if (n == 0) {
			throw new CredentialParsingException(
					"can't parse RoleExpression from '" + str + "'");
		} else if (n == 1) {
			return new DomainImpl(names[0]);
		} else if (n == 2) {
			return new RoleDecentralizedImpl(names[0], names[1]);
		} else if (n == 3) {
			return new LinkedRole(names[0], names[1], names[2]);
		} else {
			throw new CredentialParsingException(
					"can't parse RoleExpression from '" + str + "'");
		}
	}

}
