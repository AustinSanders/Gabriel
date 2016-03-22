package c2.fw.secure.tm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)

public class Utils {
	static PrintStream out = System.out;

	//  static PrintStream out = new PrintStream(new FileOutputStream(new File("/home/bpalmer/log/debug")));

	static public void debugInfo(String s) {
	    if (s.length()==0)
	        out.println(s);
	}

	static public void setOutputStream(PrintStream out) {
		Utils.out = out;
	}

	public static void main(String args[]) throws Exception {
		test1();
		//test4(3, 3);
		//test3(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
	}

	static void test1() throws Exception {
		CredentialManager cs = new CredentialManager();
		cs.addCredential(new StaticCredential("university", "professors",
				"university.depts.professors"));
		cs.addCredential(new StaticCredential("dept_cs", "professors",
				"mitchell"));
		cs
				.addCredential(new StaticCredential("university", "depts",
						"dept_cs"));
		System.out.println(cs.toString());
		System.out.println();
		//cs.removeCredential(new StaticCredential("university", "depts", "dept_cs"));
		System.out.println(cs.toString());
		System.out.println();

		RoleDecentralizedImpl r = new RoleDecentralizedImpl("university", "professors");
		System.out.println(cs.backwardSearch(r));
		System.out.println(cs.forwardSearch(new DomainImpl("mitchell")));

	}

	static void test2() throws Exception {
		CredentialManager cs = new CredentialManager();
		DomainImpl tony = new DomainImpl("Tony"), carmela = new DomainImpl(
				"Carmela"), melfi = new DomainImpl("Jennifer Melfi"), livia = new DomainImpl(
				"Livia"), junior = new DomainImpl("Junior"), chris = new DomainImpl(
				"Chris Moltisanti"), meadow = new DomainImpl("Meadow"), anthony = new DomainImpl(
				"Anthony Jr."), paulie = new DomainImpl("Paulie Walnuts"), jackie = new DomainImpl(
				"Jackie Aprile");

		RoleDecentralizedImpl tonyWifeRole = new RoleDecentralizedImpl(tony, "Wife"), tonyFamilyRole = new RoleDecentralizedImpl(
				tony, "Family Members"), tonyChildrenRole = new RoleDecentralizedImpl(tony,
				"Children"), tonyCrewRole = new RoleDecentralizedImpl(tony, "Crew"), tonyMotherRole = new RoleDecentralizedImpl(
				tony, "Mother"), tonyBossRole = new RoleDecentralizedImpl(tony, "Boss"), tonyUncleRole = new RoleDecentralizedImpl(
				tony, "Uncle"), melfiPatientRole = new RoleDecentralizedImpl(melfi, "Patients"), melfiPreferredPatientRole = new RoleDecentralizedImpl(
				melfi, "Preferred Patients"), chrisBossRole = new RoleDecentralizedImpl(chris,
				"Boss"), chrisActingBossRole = new RoleDecentralizedImpl(chris, "ActingBoss"), chrisGangRole = new RoleDecentralizedImpl(
				chris, "Gang");

		ArrayList tonyCreds = new ArrayList(20);

		tonyCreds.add(new StaticCredential(tonyWifeRole, carmela));
		tonyCreds.add(new StaticCredential(tonyFamilyRole, tonyWifeRole));
		tonyCreds.add(new StaticCredential(tonyFamilyRole, tonyChildrenRole));
		tonyCreds.add(new StaticCredential(tonyChildrenRole, meadow));
		tonyCreds.add(new StaticCredential(tonyChildrenRole, anthony));
		tonyCreds.add(new StaticCredential(tonyCrewRole, chris));
		tonyCreds.add(new StaticCredential(tonyCrewRole, paulie));
		tonyCreds.add(new StaticCredential(tonyMotherRole, livia));
		tonyCreds.add(new StaticCredential(tonyBossRole, jackie));
		tonyCreds.add(new StaticCredential(tonyUncleRole, junior));
		// Setting up credentials for toni
		cs.addCredentials(tonyCreds);

		ArrayList melfiCreds = new ArrayList(5);
		melfiCreds.add(new StaticCredential(melfiPatientRole,
				melfiPreferredPatientRole));
		melfiCreds.add(new StaticCredential(melfiPreferredPatientRole, tony));
		cs.addCredentials(melfiCreds);

		cs.addCredential(new StaticCredential(chrisBossRole, tony));
		cs
				.addCredential(new StaticCredential(chrisActingBossRole,
						tonyBossRole));
		cs.addCredential(new StaticCredential(chrisGangRole, new LinkedRole(
				chrisBossRole, "Crew")));

		System.out.println(cs.toString());
		System.out.println();
		System.out.println(cs.forwardSearch(carmela));
		System.out.println();

		cs.removeCredential(new StaticCredential(chrisBossRole, tony));
		cs.removeCredential(new StaticCredential(chrisActingBossRole,
				tonyBossRole));

		cs.addCredential(new StaticCredential(chrisGangRole, new RoleDecentralizedImpl("Tony",
				"Wife")));
		cs.addCredential(new StaticCredential(new RoleDecentralizedImpl("Meadow", "Mother"),
				new RoleDecentralizedImpl("Tony", "Wife")));

		System.out.println(cs.toString());
		System.out.println();
		System.out.println(cs.forwardSearch(carmela));
		System.out.println();

		//System.out.println(cs.forwardSearch(jackie));
		//System.out.println();
	}

	static void test3() throws Exception {
		CredentialManager store = new CredentialManager();

		store.addCredential(new StaticCredential("sbose", "picturesRead",
				"sbose.family"));
		store.addCredential(new StaticCredential("sbose", "picturesWrite",
				"sbose.family"));
		store.addCredential(new StaticCredential("sbose", "picturesRead",
				"sbose.family.friends"));
		store.addCredential(new StaticCredential("sbose", "picturesRead",
				"patrick"));
		store.addCredential(new StaticCredential("sbose", "picturesWrite",
				"andrew"));
		store.addCredential(new StaticCredential("sbose", "picturesWrite",
				"patrick"));

		System.out.println(store.toString() + "\n\n");

		ResultEvidenceMap result = store.forwardSearch(new DomainImpl(
				"andrew"));
		Set resultSet = result.resultSet();
		System.out.println("forward andrew is: " + resultSet);
		Iterator it = resultSet.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}

		//add a credential
		store.addCredential(new StaticCredential("sbose", "picturesRead",
				"andrew"));

		System.out
				.println("\n\n added credential sbose.picturesRead <- andrew");

		System.out.println("\n" + store.toString() + "\n\n"
				+ "forward search on entity andrew:");

		result = store.forwardSearch(new DomainImpl("andrew"));
		resultSet = result.resultSet();
		System.out.println("forward andrew is: " + resultSet);
		it = resultSet.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}

		//remove a credential
		//
		//

		store.removeCredential(new StaticCredential("sbose", "picturesWrite",
				"patrick"));

		System.out
				.println("\n\n removed credential sbose.picturesWrite <- patrick");

		System.out.println("\n" + store.toString() + "\n\n"
				+ "forward search on patrick:");

		result = store.forwardSearch(new DomainImpl("patrick"));
		resultSet = result.resultSet();
		it = resultSet.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}

	static void test4(int l, int n) throws Exception {
		CredentialManager cs = new CredentialManager();
		for (int j = 0; j < n; j++) {
			cs.addCredential(new StaticCredential("K", "C", "K.C" + l + ".B"
					+ j));
		}
		cs.addCredential(new StaticCredential("K", "C1", "K0.A"));
		for (int i = 1; i < l; i++) {
			int j = i + 1;
			cs.addCredential(new StaticCredential("K", "C" + j, "K.C" + i
					+ ".A"));
		}
		for (int i = 0; i < n; i++) {
			cs.addCredential(new StaticCredential("K0", "A", "K" + i));
		}
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			cs
					.addCredential(new StaticCredential("K" + i, "A", "K" + j
							+ ".A"));
		}
		System.out.println(cs.toString());
		cs.forwardSearch(new DomainImpl("K0"));

		cs.backwardSearch(new RoleDecentralizedImpl("K", "C"));

		//engine.bidirectionSearch(new Role("K0", "B0"), new Role("K", "C"));
	}

	static final public int TRACK_NONE = 0;

	static final public int TRACK_ANY = 1;

	static final public int TRACK_ALL = 2;

}
