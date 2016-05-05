package c2.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ApprovalProcessor{
	protected Set approverSet = new HashSet();
	protected Runnable action;
	
	public ApprovalProcessor(Object[] approvers, Runnable action){
		for(int i = 0; i < approvers.length; i++){
			approverSet.add(approvers[i]);
		}
		this.action = action;
	}
	
	public boolean approve(Object approver){
		synchronized(approverSet){
			approverSet.remove(approver);
			if(approverSet.size() == 0){
				//Everyone has approved the operation.
				if(action != null) action.run();
				return true;
			}
			return false;
		}
	}
	
	public boolean isApproved(){
		synchronized(approverSet){
			return approverSet.size() == 0;
		}
	}
}
