package c2.fw;

/**
 * Interface implemented by objects that want to be notified
 * of timer ticks from a timer.
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface TimerListener{

	/**
	 * Called when the timer ticks.
	 * @param tickCount The tick count of the timer.
	 */
	public void tick(int tickCount);

}

