package c2.fw;

/**
 * Interface implemented by messages that should not be processed
 * before a given time, specified as a long integer.
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface DeferredMessage{

	/**
	 * Sets the earliest process time on this message.
	 * @param time The earliest time that this message should be processed.
	 */
	public void setEarliestProcessTime(long time);
	
	/**
	 * Get the earliest process time on this message.
	 * @return The earliest time this message should be processed.
	 */
	public long getEarliestProcessTime();

}

