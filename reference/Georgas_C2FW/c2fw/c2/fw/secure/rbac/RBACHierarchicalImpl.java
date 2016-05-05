/*
 * Created on Aug 24, 2005
 *
 */
package c2.fw.secure.rbac;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements the RBACHierarchical interface, by implementing the 
 * new methods and redefining some existing methods of RBACCoreImpl.
 * 
 * @author Jie Ren
 */
public class RBACHierarchicalImpl extends RBACCoreImpl 
			implements RBACHierarchical {

	// whether this is a general hierarchical RBACCore
	boolean		isGeneral;

	public RBACHierarchicalImpl(int activeSessionStrategy, boolean autoAddOnGet, boolean isGeneral) {
	    super(activeSessionStrategy, autoAddOnGet);
		this.isGeneral = isGeneral;
    }

	// p18
    public void addInheritance(RoleHierarchical ascendant, RoleHierarchical descendant)
            	throws 	RoleDoesNotExistException, RoleCycleException,
            			RoleMultipleInheritanceException {
        if (!allRoles.contains(ascendant))
            throw new RoleDoesNotExistException(ascendant);

        if (!allRoles.contains(descendant))
            throw new RoleDoesNotExistException(descendant);

        if (descendant.isSeniorOf(ascendant))
            throw new RoleCycleException(ascendant, descendant);

        // 6.2.2.1, p21
        if (!isGeneral() && !ascendant.getDescendants().isEmpty())
            throw new RoleMultipleInheritanceException(ascendant);

        if (ascendant.isAscendantOf(descendant))
            return;

        // Add direct inheritance
        ascendant.addDescendant(descendant);
        descendant.addAscendant(ascendant);

        // Add indirect inheritance
        // We do not detect cycle here
        doAddInheritance(ascendant, descendant);
    }

    /**
     * Propagate changes of one inheritance
     * 
     * @param ascendant
     *            the ascendant (inheriting) roles of the inheritance
     * @param descendant
     *            the descendant (inherited) roles of the inheritance
     * @return true if the inheritnace brings some changes, false otherwise
     */
    private boolean doAddInheritance(RoleHierarchical ascendant, RoleHierarchical descendant) {
        Set seniors = new HashSet();
        seniors.addAll(ascendant.getSeniors());

        Set juniors = new HashSet();
        juniors.addAll(descendant.getJuniors());

        boolean changed = false;
        for (Iterator i = seniors.iterator(); i.hasNext();) {
            RoleHierarchical s = (RoleHierarchical) i.next();
            if (s.addJuniors(juniors))
                changed = true;
        }

        for (Iterator i = juniors.iterator(); i.hasNext();) {
            RoleHierarchical j = (RoleHierarchical) i.next();
            if (j.addSeniors(seniors))
                changed = true;
        }

        return changed;
    }

    // p18
    // At p18, it seems after deleting an immediate inheritance relationship
    // the overall junior/senior relationship should be recalculated as 
    // the transitive closure of the new immediate inheritance relationships.
    // However, at p38, it suggests that existing junior/senior relationships
    // could also be preserved, depending on the choice of the implementations.
    // Anyway, we choose an automatic propagating semantics: so the permissions
    // propagation is instant after addition and deletion of inheritance.
    // This provides a clear semantics, and is also the easiest to implement.
    //
    public void deleteInheritance(RoleHierarchical ascendant, RoleHierarchical descendant)
            	throws 	RoleDoesNotExistException,
            			RoleInheritanceDoesNotExistException {
        if (!allRoles.contains(ascendant))
            throw new RoleDoesNotExistException(ascendant);

        if (!allRoles.contains(descendant))
            throw new RoleDoesNotExistException(descendant);

        if (!ascendant.isAscendantOf(descendant))
            throw new RoleInheritanceDoesNotExistException(descendant,
                    ascendant);

        // Delete the direct inheritance
        ascendant.deleteDescendant(descendant);
        descendant.deleteAscendant(ascendant);

        // Clear the junior and senior roles
        for (Iterator i = allRoles.iterator(); i.hasNext();) {
            RoleHierarchical r = (RoleHierarchical) i.next();
            r.clearJuniors();
            r.clearSeniors();
        }

        // Initialize junior and senior roles with direct ascendants and
        // descendants. We go across the whole set of roles, along the 
        // ascendant direction
        for (Iterator i = allRoles.iterator(); i.hasNext();) {
            RoleHierarchical aRole = (RoleHierarchical) i.next();
            Set ascendants = aRole.getAsendants();
            for (Iterator a = ascendants.iterator(); a.hasNext();) {
                RoleHierarchical anAscendant = (RoleHierarchical) a.next();
                aRole.addSenior(anAscendant);
                anAscendant.addJunior(aRole);
            }
        }

        // Now keep calculating the inheritance relationships, until no more
        // changes. This is the transitive closure
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Iterator i = allRoles.iterator(); i.hasNext();) {
                RoleHierarchical aRole = (RoleHierarchical) i.next();
                Set ascendants = aRole.getAsendants();
                for (Iterator a = ascendants.iterator(); a.hasNext();) {
                    RoleHierarchical anAscendant = (RoleHierarchical) a.next();
                    if (doAddInheritance(anAscendant, aRole))
                        changed = true;
                }
            }
        }
    }

    // p18
    public RoleHierarchical addAscendant(Name ascendantName, RoleHierarchical descendant)
            throws RoleExistsException, RoleDoesNotExistException,
            RoleCycleException, RoleMultipleInheritanceException {
        RoleHierarchical ascendant = (RoleHierarchical)addRole(ascendantName);
        addInheritance(ascendant, descendant);
        return ascendant;
    }

    // p18
    public RoleHierarchical addDescendant(Name descendantName, RoleHierarchical ascendant)
            throws RoleExistsException, RoleDoesNotExistException,
            RoleCycleException, RoleMultipleInheritanceException {
        RoleHierarchical descendant = (RoleHierarchical)addRole(descendantName);
        addInheritance(ascendant, descendant);
        return ascendant;
    }

    // Override to always create hierarchical roles
	public Role createARole(Name roleName) {
	    return new RoleHierarchicalImpl(roleName);
	}
	
	// remove the role from its asendants/descendants/juniors/seniors
	protected void moreDeleteRole(Role role) {
	    if (!(role instanceof RoleHierarchical))
	        return;
	    
	    ((RoleHierarchical)role).deleteSelfFromHierarchy();
	}
	
	public boolean isGeneral() {
		return isGeneral;
	}
	
	public Set getJuniors(RoleHierarchical senior) {
	    return senior.getJuniors();
	}
	
	public Set getSeniors(RoleHierarchical junior) {
	    return junior.getSeniors();
	}
	
	public static void main(String[] args) {
		try {
			RBACHierarchicalImpl	rbac = new RBACHierarchicalImpl(RBACCore.SESSION_CONTINUE_DOWNGRADE, false, true);
			Role roleStudent = rbac.addRole(new NameImpl("student"));
			Role roleGrad = rbac.addRole(new NameImpl("grad"));
			Role roleCandidate = rbac.addRole(new NameImpl("candidate"));
			Role roleAlien = rbac.addRole(new NameImpl("alien"));
			User userJie = rbac.addUser(new NameImpl("jie"));
			User userJoe = rbac.addUser(new NameImpl("joe"));
			rbac.assignUser(userJie, roleCandidate);
			rbac.assignUser(userJoe, roleGrad);
			Permission openFile = rbac.createPermission(new OperationImpl(
					new NameImpl("open")), new ObjectImpl(new NameImpl("file")));
			Permission deductTax = rbac.createPermission(new OperationImpl(
					new NameImpl("deduct")), new ObjectImpl(new NameImpl("tax")));
			Permission buyTicket = rbac.createPermission(new OperationImpl(
					new NameImpl("buy")), new ObjectImpl(new NameImpl("ticket")));
			rbac.grantPermission(openFile, roleGrad);
			rbac.grantPermission(deductTax, roleAlien);
			rbac.grantPermission(buyTicket, roleStudent);
			rbac.addInheritance((RoleHierarchical)roleGrad, (RoleHierarchical)roleStudent);
			rbac.addInheritance((RoleHierarchical)roleCandidate, (RoleHierarchical)roleGrad);
			rbac.addInheritance((RoleHierarchical)roleCandidate, (RoleHierarchical)roleAlien);
			rbac.deleteInheritance((RoleHierarchical)roleCandidate, (RoleHierarchical)roleGrad);
			Set permissionsOfJie = rbac.userPermissions(userJie);
			for (Iterator i = permissionsOfJie.iterator(); i.hasNext(); ) {
				Permission	p = (Permission)i.next();
				System.out.println(p.toString());
			}
			Set rolesOfJie = rbac.authorizedRoles(userJie);
			for (Iterator i = rolesOfJie.iterator(); i.hasNext(); ) {
				Role	r = (Role)i.next();
				System.out.println(r.toString());
			}
			Set usersOfGrad = rbac.authorizedUsers(roleGrad);
			for (Iterator i = usersOfGrad.iterator(); i.hasNext(); ) {
				User	u = (User)i.next();
				System.out.println(u.toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
