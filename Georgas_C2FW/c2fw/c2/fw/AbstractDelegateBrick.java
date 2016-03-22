package c2.fw;

import java.util.*;

/**
 * This class should be used as the base class for <CODE>DelegateBrick</CODE>s
 * when possible.  It implements boilerplate methods required of each <CODE>DelegateBrick</CODE>.
 *
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public abstract class AbstractDelegateBrick extends AbstractBrick implements DelegateBrick{
	
	/** Message processors for this brick. */
	//protected Vector messageProcessors;	
	protected MessageProcessor[] messageProcessors;
	
	/** Lifecycle processors for this brick. */
	protected Vector lifecycleProcessors;
	
	/** Delegate brick listeners for this brick. */
	protected Vector delegateBrickListeners;
	
	/**
	 * Create a new <CODE>AbstractDelegateBrick</CODE> with the given identifier.
	 * @param thisId Identifier of this Brick
	 */
	public AbstractDelegateBrick(Identifier thisId){
		super(thisId);
		messageProcessors = new MessageProcessor[0];
		//messageProcessors = new Vector();
		lifecycleProcessors = new Vector();
		delegateBrickListeners = new Vector();
	}
	
	public void addMessageProcessor(MessageProcessor mp){
		List l = Arrays.asList(messageProcessors);
		ArrayList al = new ArrayList(l);
		al.add(mp);
		MessageProcessor[] newmps = (MessageProcessor[])al.toArray(new MessageProcessor[0]);
		messageProcessors = newmps;
		fireMessageProcessorAdded(mp);
	}
	
	public void removeMessageProcessor(MessageProcessor mp){
		List l = Arrays.asList(messageProcessors);
		ArrayList al = new ArrayList(l);
		al.remove(mp);
		MessageProcessor[] newmps = (MessageProcessor[])al.toArray(new MessageProcessor[0]);
		messageProcessors = newmps;
		fireMessageProcessorRemoved(mp);
	}
	
	public MessageProcessor[] getMessageProcessors(){
		return messageProcessors;
		/*
		synchronized(messageProcessors){
			MessageProcessor[] arr = new MessageProcessor[messageProcessors.size()];
			messageProcessors.copyInto(arr);
			return arr;
		}
		*/
	}

	public void addLifecycleProcessor(LifecycleProcessor lp){
		synchronized(lifecycleProcessors){
			lifecycleProcessors.addElement(lp);
		}
		fireLifecycleProcessorAdded(lp);
	}
	
	public void removeLifecycleProcessor(LifecycleProcessor lp){
		synchronized(lifecycleProcessors){
			lifecycleProcessors.removeElement(lp);
		}
		fireLifecycleProcessorRemoved(lp);
	}
	
	public LifecycleProcessor[] getLifecycleProcessors(){
		synchronized(lifecycleProcessors){
			LifecycleProcessor[] arr = new LifecycleProcessor[lifecycleProcessors.size()];
			lifecycleProcessors.copyInto(arr);
			return arr;
		}
	}
	
	public final void init(){
		Object[] mps = lifecycleProcessors.toArray();
		int size = mps.length;
		for(int i = 0; i < size; i++){
			LifecycleProcessor lp = (LifecycleProcessor)mps[i];
			lp.init();
		}
	}
	
	public final void begin(){
		Object[] mps = lifecycleProcessors.toArray();
		int size = mps.length;
		for(int i = 0; i < size; i++){
			LifecycleProcessor lp = (LifecycleProcessor)mps[i];
			lp.begin();
		}
	}
	
	public final void end(){
		Object[] mps = lifecycleProcessors.toArray();
		int size = mps.length;
		for(int i = 0; i < size; i++){
			LifecycleProcessor lp = (LifecycleProcessor)mps[i];
			lp.end();
		}
	}

	public final void destroy(){
		Object[] mps = lifecycleProcessors.toArray();
		int size = mps.length;
		for(int i = 0; i < size; i++){
			LifecycleProcessor lp = (LifecycleProcessor)mps[i];
			lp.destroy();
		}
	}
	
	/**
	 * Handles incoming messages on this brick.  Calls each <CODE>MessageProcessor</CODE>'s
	 * handle method in turn to process the message.
	 * @param m Message to handle.
	 */
	public final void handle(Message m){
		MessageProcessor[] mps = messageProcessors;
		for(int i = 0; i < mps.length; i++){
			mps[i].handle(m);
		}
		/*
		Object[] mps = messageProcessors.toArray();
		int size = mps.length;
		for(int i = 0; i < size; i++){
			MessageProcessor mp = (MessageProcessor)mps[i];
			mp.handle(m);
		}
		*/
	}
	
	public void addDelegateBrickListener(DelegateBrickListener l){
		this.delegateBrickListeners.addElement(l);
	}
	
	public DelegateBrickListener[] getDelegateBrickListeners(){
		synchronized(delegateBrickListeners){
			DelegateBrickListener[] arr = new DelegateBrickListener[delegateBrickListeners.size()];
			delegateBrickListeners.copyInto(arr);
			return arr;
		}
	}
	
	public void removeDelegateBrickListener(DelegateBrickListener l){
		this.delegateBrickListeners.removeElement(l);
	}
	
	protected void fireMessageProcessorAdded(MessageProcessor mp){
		synchronized(delegateBrickListeners){
			for(Iterator it = delegateBrickListeners.iterator(); it.hasNext(); ){
				DelegateBrickListener l = (DelegateBrickListener)it.next();
				l.messageProcessorAdded(this, mp);
			}
		}
	}

	protected void fireMessageProcessorRemoved(MessageProcessor mp){
		synchronized(delegateBrickListeners){
			for(Iterator it = delegateBrickListeners.iterator(); it.hasNext(); ){
				DelegateBrickListener l = (DelegateBrickListener)it.next();
				l.messageProcessorRemoved(this, mp);
			}
		}
	}
		
	protected void fireLifecycleProcessorAdded(LifecycleProcessor lp){
		synchronized(delegateBrickListeners){
			for(Iterator it = delegateBrickListeners.iterator(); it.hasNext(); ){
				DelegateBrickListener l = (DelegateBrickListener)it.next();
				l.lifecycleProcessorAdded(this, lp);
			}
		}
	}

	protected void fireLifecycleProcessorRemoved(LifecycleProcessor lp){
		synchronized(delegateBrickListeners){
			for(Iterator it = delegateBrickListeners.iterator(); it.hasNext(); ){
				DelegateBrickListener l = (DelegateBrickListener)it.next();
				l.lifecycleProcessorRemoved(this, lp);
			}
		}
	}
}

