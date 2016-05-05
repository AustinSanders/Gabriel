package c2.fw.secure.tm;

//The inference engine classes are based on Professor Ninghui Li's code. 
//(http://www.cs.purdue.edu/people/ninghui)
public interface ForwardSolutionListener
{
    // A new solution r is added to Node s
    void forwardSolutionAdded(ProofNode source, ForwardSolution solution);
}

