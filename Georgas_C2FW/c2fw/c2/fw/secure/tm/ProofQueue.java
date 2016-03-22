package c2.fw.secure.tm;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
public interface ProofQueue {
	boolean hasUnexploredForwardNodes();

	boolean hasUnexploredBackwardNodes();

	ProofNode nextUnexploredForwardNode();

	ProofNode nextUnexploredBackwardNode();

	void addForwardNode(ProofNode node);

	void addBackwardNode(ProofNode node);
}