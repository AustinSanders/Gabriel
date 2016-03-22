package c2.fw;

/**
 * Interface for a timer that emits a 'tick' event regularly.
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface Timer{

	/**
	 * Add a listener who will be called whenever the timer ticks.
	 * @param l Listener to add.
	 */
	public void addTimerListener(TimerListener l);
	
	/**
	 * Remove a listener who will no longer be called whenever the timer ticks.
	 * @param l Listener to remove.
	 */
	public void removeTimerListener(TimerListener l);

}

