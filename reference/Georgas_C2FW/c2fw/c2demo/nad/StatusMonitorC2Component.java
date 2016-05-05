package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

public class StatusMonitorC2Component extends AbstractC2DelegateBrick{
	
	protected Object tableLock = new Object();
	
	protected Map deadlineIntervals = Collections.synchronizedMap(new HashMap());
	protected Map lastHeartbeatReceived = Collections.synchronizedMap(new HashMap());
	
	//The main application window
	protected StatusMonitorFrame smFrame = null;
	
	protected StatusMonitorThread smt;
	
	protected DefaultTableModel tableModel;

	public StatusMonitorC2Component(Identifier id){
		super(id);
		tableModel = new DefaultTableModel();
		tableModel.addColumn("Component");
		tableModel.addColumn("Status");
		addLifecycleProcessor(new StatusMonitorLifecycleProcessor());
		addMessageProcessor(new StatusMonitorMessageProcessor());
	}

	class StatusMonitorLifecycleProcessor extends LifecycleAdapter{
		public void begin(){
			smt = new StatusMonitorThread(250);
			smt.start();
			newWindow();
		}
		
		public void end(){
			if(smt != null){
				smt.terminate();
				smt = null;
			}
			if(smFrame != null){
				smFrame.setVisible(false);
				smFrame.dispose();
			}
		}
	}

	class StatusMonitorMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof ComponentStatusMessage){
				synchronized(tableLock){
					ComponentStatusMessage csm = (ComponentStatusMessage)m;
					String componentName = csm.getComponentID();
					int deadlineInterval = csm.getDeadlineInterval();
					String status = csm.getStatus();
					if(status.equals(ComponentStatusMessage.STATUS_REMOVED)){
						removeComponent(componentName);
						return;
					}
					deadlineIntervals.put(componentName, new Integer(deadlineInterval));
					long t = System.currentTimeMillis();
					lastHeartbeatReceived.put(componentName, new Long(t));
					setStatus(componentName, csm.getStatus());
				}
			}
		}
	}
	
	public void newWindow(){
		//This makes sure we only have one active window open.
		if(smFrame == null){
			smFrame = new StatusMonitorFrame();
		}
		else{
			smFrame.requestFocus();
		}
	}
	
		
	public void setStatus(String component, String status){
		synchronized(tableLock){
			int numRows = tableModel.getRowCount();
			for(int i = 0; i < numRows; i++){
				String existingCompName = (String)tableModel.getValueAt(i, 0);
				if(existingCompName.equals(component)){
					tableModel.setValueAt(status, i, 1);
					return;
				}
			}
			tableModel.addRow(new Object[]{component, status});
		}
	}
	
	public void removeComponent(String component){
		synchronized(tableLock){
			deadlineIntervals.remove(component);
			lastHeartbeatReceived.remove(component);

			int numRows = tableModel.getRowCount();
			for(int i = 0; i < numRows; i++){
				String existingCompName = (String)tableModel.getValueAt(i, 0);
				if(existingCompName.equals(component)){
					tableModel.removeRow(i);
					return;
				}
			}

		}
	}
	
	class StatusMonitorFrame extends JFrame{
		
		protected JTable table;
		
		public StatusMonitorFrame(){
			super("AWACS Status Monitor");
			init();
		}
		
		//This is pretty standard Swing GUI stuff in Java.
		private void init(){
			Toolkit tk = getToolkit();
			Dimension screenSize = tk.getScreenSize();
			double xSize = (300);
			double ySize = (120);
			double xPos = (screenSize.getWidth() * 0.70);
			double yPos = (screenSize.getHeight() * 0.60);

			table = new JTable(tableModel);

			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add("Center", new JScrollPane(table));

			setVisible(true);
			setSize((int)xSize, (int)ySize);
			setLocation((int)xPos, (int)yPos);
			setVisible(true);
			paint(getGraphics());
		}
		
	}

	class StatusMonitorThread extends Thread{
		private int interval;
		private boolean terminate;
		
		public StatusMonitorThread(int interval){
			this.interval = interval;
			this.terminate = false;
			this.setDaemon(true);
		}
		
		public void terminate(){
			this.terminate = true;
			interrupt();
		}
		
		public void run(){
			while(true){
				if(terminate){
					return;
				}
				synchronized(tableLock){
					long currentTime = System.currentTimeMillis();
					for(Iterator it = lastHeartbeatReceived.keySet().iterator(); it.hasNext(); ){
						String componentName = (String)it.next();
						Integer deadlineIntervalInt = (Integer)deadlineIntervals.get(componentName);
						if(deadlineIntervalInt == null){
							continue;
						}
						Long lastHeartbeatReceivedLong = (Long)lastHeartbeatReceived.get(componentName);
						if(lastHeartbeatReceivedLong == null){
							continue;
						}
						
						int di = deadlineIntervalInt.intValue();
						long lhr = lastHeartbeatReceivedLong.longValue();
						
						long deltaT = currentTime - lhr;
						if(deltaT > (di * 6)){
							setStatus(componentName, "Fail: No Response");
						}
						else if(deltaT > (di * 3)){
							setStatus(componentName, "Warning: Missed Deadline");
						}
					}
				}
				try{
					sleep(interval);
				}
				catch(InterruptedException e){}
			}
		}
	}
	
}