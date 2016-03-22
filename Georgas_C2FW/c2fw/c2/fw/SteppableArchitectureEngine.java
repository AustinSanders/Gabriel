package c2.fw;

/**
 * Interface implemented by <CODE>ArchitectureEngine</CODE>s that
 * can run in lock-step.
 * 
 * @see c2.fw.Timer
 * @see c2.util.GUISteppableEngineManager
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface SteppableArchitectureEngine extends ArchitectureEngine, Timer{
	/**
	 * Step the architecture once.  This function may return
	 * before the step is complete.
	 */
	public void step();
	
	/**
	 * Determine whether the architecture is idle (no step
	 * is in progress).
	 * @return <CODE>true</CODE> if the architecture is idle,
	 * <CODE>false</CODE> otherwise.
	 */
	public boolean isIdle();
	
	/**
	 * Block the calling thread until the architecture is idle.
	 * @exception InterruptedException if the thread was interrupted while waiting. 
	 */
	public void waitUntilIdle() throws InterruptedException;
}

