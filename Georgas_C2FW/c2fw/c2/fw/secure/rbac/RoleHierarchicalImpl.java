/*
 * Created on Aug 24, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements the RoleHierarchical interface.
 * 
 * @author Jie Ren
 */
public class RoleHierarchicalImpl extends RoleImpl implements RoleHierarchical {
	/**
	 * Create a role
	 * 
	 * @param name the name of the role
	 */
	public RoleHierarchicalImpl(String name) {
		super(name);
		includeSelf();
	}
	
	/**
	 * Create a role
	 * 
	 * @param name the name of the role
	 */
	public RoleHierarchicalImpl(Name name) {
		super(name);
		includeSelf();
	}

	// The inheritnace relationship, >=, is transitive, reflective, and 
	//  antisymmetric, so a role is always a junior and a senior of itself
	private void includeSelf() {
	    juniors.add(this);
	    seniors.add(this);
	}
	
	public Set getAuthorizedUsers() {
		Set authorizedUsers = new HashSet();
		for (Iterator i = seniors.iterator(); i.hasNext(); ) {
			authorizedUsers.addAll(((Role)i.next()).getUsers());
		}
		return authorizedUsers;
	}

	public Set getAuthorizedPermissions() {
	    Set	authorizedPermissions = new HashSet();
	    for (Iterator i = juniors.iterator(); i.hasNext(); ) {
	        authorizedPermissions.addAll(((Role)i.next()).getPermissions());
	    }
	    return authorizedPermissions;
	}
	
	public Set getAuthorizedRoles() {
	    return juniors;
	}
	
	// ascendants and descendants keep the direct inheritances
	Set	ascendants = new HashSet();
	Set descendants = new HashSet();
	// juniors and seniors keep both direct and indirect inheritances
	Set juniors = new HashSet();
	Set	seniors = new HashSet();
	
	public void addAscendant(RoleHierarchical ascendant) {
		ascendants.add(ascendant);
		seniors.add(ascendant);
	}
	
	public void addDescendant(RoleHierarchical descendant) {
		descendants.add(descendant);
		juniors.add(descendant);
	}
	
	public boolean isAscendantOf(RoleHierarchical descendant) {
		return descendants.contains(descendant);
	}
	
	public boolean isDescendantOf(RoleHierarchical ascendant) {
		return ascendants.contains(ascendant);
	}
	
	public Set getAsendants() {
		return ascendants;
	}
	
	public Set getDescendants() {
		return descendants;
	}

	public void deleteAscendant(RoleHierarchical ascendant) {
		ascendants.remove(ascendant);
	}
	
	public void deleteDescendant(RoleHierarchical descendant) {
		descendants.remove(descendant);
	}
	
	public void addJunior(RoleHierarchical junior) {
		juniors.add(junior);
	}
	
	public boolean addJuniors(Set juniors) {
		return this.juniors.addAll(juniors);
	}
	
	public void addSenior(RoleHierarchical senior) {
		seniors.add(senior);
	}
	
	public boolean addSeniors(Set seniors) {
		return this.seniors.addAll(seniors);
	}
	
	public boolean isJuniorOf(RoleHierarchical senior) {
		return seniors.contains(senior);
	}
	
	public boolean isSeniorOf(RoleHierarchical junior) {
		return juniors.contains(junior);
	}

	public Set getJuniors() {
		return juniors;
	}
	
	public Set getSeniors() {
		return seniors;
	}

	public void deleteJunior(RoleHierarchical junior) {
	    juniors.remove(junior);
	}
	
	public void deleteSenior(RoleHierarchical senior) {
	    seniors.remove(senior);
	}
	
	public void clearJuniors() {
		juniors.clear();
		includeSelf();
	}
	
	public void clearSeniors() {
		seniors.clear();
		includeSelf();
	}
	
	public void deleteSelfFromHierarchy() {
	    for (Iterator i = ascendants.iterator(); i.hasNext(); ) {
	        RoleHierarchical r = (RoleHierarchical)i.next();
	        r.deleteDescendant(this);
	    }
	    for (Iterator i = descendants.iterator(); i.hasNext(); ) {
	        RoleHierarchical r = (RoleHierarchical)i.next();
	        r.deleteAscendant(this);
	    }
	    for (Iterator i = seniors.iterator(); i.hasNext(); ) {
	        RoleHierarchical r = (RoleHierarchical)i.next();
	        r.deleteJunior(this);
	    }
	    for (Iterator i = juniors.iterator(); i.hasNext(); ) {
	        RoleHierarchical r = (RoleHierarchical)i.next();
	        r.deleteSenior(this);
	    }
	}
}
