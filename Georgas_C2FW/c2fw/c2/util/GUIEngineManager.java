package c2.util;

import c2.fw.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class GUIEngineManager extends Frame implements ArchitectureListener, ArchitectureEngineListener, ActionListener{

	private ArchitectureController controller;
	
	private List readyBrickList;
	private Vector readyBrickVector;
	
	private List startedBrickList;
	private Vector startedBrickVector;
	
	private List suspendedBrickList;
	private Vector suspendedBrickVector;
	
	private List stoppedBrickList;
	private Vector stoppedBrickVector;

	private Button bStartReady;
	
	private Button bSuspendStarted;
	private Button bStopStarted;
	
	private Button bResumeSuspended;
	private Button bStopSuspended;
	
	public GUIEngineManager(ArchitectureController controller){
		super("Architecture Engine Manager");
		this.controller = controller;
		if(controller == null){
			throw new IllegalArgumentException("Manager has not yet been assigned an engine.");
		}
		
		controller.addArchitectureListener(this);
		controller.addArchitectureEngineListener(this);
		init();
		this.setSize(400, 200);
		this.setLocation(200, 200);
		this.setVisible(true);
		this.paint(getGraphics());
		System.err.println("constructor returning.");
	}
	
	private void init(){
		readyBrickList = new List(15, true);
		readyBrickVector = new Vector();
		
		startedBrickList = new List(15, true);
		startedBrickVector = new Vector();
		
		suspendedBrickList = new List(15, true);
		suspendedBrickVector = new Vector();
		
		stoppedBrickList = new List(15, true);
		stoppedBrickVector = new Vector();
	
		bStartReady = new Button("Start Selected");
		bStartReady.addActionListener(this);

		bSuspendStarted = new Button("Suspend Selected");
		bSuspendStarted.addActionListener(this);
		bStopStarted = new Button("Stop Selected");
		bStopStarted.addActionListener(this);
		
		bResumeSuspended = new Button("Resume Selected");
		bResumeSuspended.addActionListener(this);
		bStopSuspended = new Button("Stop Selected");
		bStopSuspended.addActionListener(this);
		
		this.setLayout(new GridLayout(1,4));
		
		Panel readyPanel = new Panel();
		readyPanel.setLayout(new BorderLayout());
		readyPanel.add("North", new Label("Ready:"));
		readyPanel.add("Center", readyBrickList);
		Panel readyButtonPanel = new Panel();
		readyButtonPanel.setLayout(new GridLayout(1,1));
		readyButtonPanel.add(bStartReady);
		readyPanel.add("South", readyButtonPanel);
		
		Panel startedPanel = new Panel();
		startedPanel.setLayout(new BorderLayout());
		startedPanel.add("North", new Label("Started:"));
		startedPanel.add("Center", startedBrickList);
		Panel startedButtonPanel = new Panel();
		startedButtonPanel.setLayout(new GridLayout(2,1));
		startedButtonPanel.add(bSuspendStarted);
		startedButtonPanel.add(bStopStarted);
		startedPanel.add("South", startedButtonPanel);
		
		Panel suspendedPanel = new Panel();
		suspendedPanel.setLayout(new BorderLayout());
		suspendedPanel.add("North", new Label("Suspended:"));
		suspendedPanel.add("Center", suspendedBrickList);
		Panel suspendedButtonPanel = new Panel();
		suspendedButtonPanel.setLayout(new GridLayout(2,1));
		suspendedButtonPanel.add(bResumeSuspended);
		suspendedButtonPanel.add(bStopSuspended);
		suspendedPanel.add("South", suspendedButtonPanel);
		
		Panel stoppedPanel = new Panel();
		stoppedPanel.setLayout(new BorderLayout());
		stoppedPanel.add("North", new Label("Stopped:"));
		stoppedPanel.add("Center", stoppedBrickList);
		Panel stoppedButtonPanel = new Panel();
		stoppedButtonPanel.setLayout(new GridLayout(2,1));
		stoppedPanel.add("South", stoppedButtonPanel);
		
		this.add(readyPanel);
		this.add(startedPanel);
		this.add(suspendedPanel);
		this.add(stoppedPanel);
		
		Identifier[] brickIds = controller.getBrickIdentifiers();
		for(int i = 0; i < brickIds.length; i++){
			checkBrickState(brickIds[i]);
		}
		System.err.println("init returning.");
	}
	
	private void removeBrick(Identifier id){
		boolean done = false;

		for(int i = 0; i < readyBrickVector.size(); i++){
			if(((Identifier)readyBrickVector.elementAt(i)).equals(id)){
				readyBrickVector.removeElementAt(i);
				readyBrickList.remove(i);
				done = true;
				break;
			}
		}
		
		if(!done){
			for(int i = 0; i < startedBrickVector.size(); i++){
				if(((Identifier)startedBrickVector.elementAt(i)).equals(id)){
					startedBrickVector.removeElementAt(i);
					startedBrickList.remove(i);
					done = true;
					break;
				}
			}
		}
		
		if(!done){
			for(int i = 0; i < suspendedBrickVector.size(); i++){
				if(((Identifier)suspendedBrickVector.elementAt(i)).equals(id)){
					suspendedBrickVector.removeElementAt(i);
					suspendedBrickList.remove(i);
					done = true;
					break;
				}
			}
		}
		
		if(!done){
			for(int i = 0; i < stoppedBrickVector.size(); i++){
				if(((Identifier)stoppedBrickVector.elementAt(i)).equals(id)){
					stoppedBrickVector.removeElementAt(i);
					stoppedBrickList.remove(i);
					done = true;
					break;
				}
			}
		}
	}
	
	private synchronized void checkBrickState(Identifier id){
		removeBrick(id);
		
		int state = controller.getState(id);
		switch(state){
		case ArchitectureEngine.STATE_OPEN_RUNNING:
			startedBrickVector.addElement(id);
			startedBrickList.add(id.toString());
			break;
		case ArchitectureEngine.STATE_OPEN_NOTRUNNING_SUSPENDED:
			suspendedBrickVector.addElement(id);
			suspendedBrickList.add(id.toString());
			break;
		case ArchitectureEngine.STATE_OPEN_NOTRUNNING:
			readyBrickVector.addElement(id);
			readyBrickList.add(id.toString());
			break;
		case ArchitectureEngine.STATE_CLOSED_COMPLETED:
		case ArchitectureEngine.STATE_CLOSED_ABNORMALCOMPLETED:
		case ArchitectureEngine.STATE_CLOSED_ABNORMALCOMPLETED_ABORTED:
		case ArchitectureEngine.STATE_CLOSED_ABNORMALCOMPLETED_TERMINATED:
			stoppedBrickVector.addElement(id);
			stoppedBrickList.add(id.toString());
			break;
		}
		repaint();
	}

	public void engineStarted(ArchitectureEngine engine){
	}
	
	public void engineStopped(ArchitectureEngine engine){
	}
	
	public void bricksStarted(ArchitectureEngine engine, Identifier[] ids){
		System.err.println("Got notice: bricks started: " + ids);
		for(int i = 0; i < ids.length; i++){
			checkBrickState(ids[i]);
		}
		System.err.println("Done processing.");
	}
	
	public void bricksSuspended(ArchitectureEngine engine, Identifier[] ids){
		for(int i = 0; i < ids.length; i++){
			checkBrickState(ids[i]);
		}
	}

	public void bricksResumed(ArchitectureEngine engine, Identifier[] ids){
		for(int i = 0; i < ids.length; i++){
			checkBrickState(ids[i]);
		}
	}

	public void bricksStopped(ArchitectureEngine engine, Identifier[] ids){
		for(int i = 0; i < ids.length; i++){
			checkBrickState(ids[i]);
		}
	}
	
	public synchronized void actionPerformed(ActionEvent evt){
		Object src = evt.getSource();

		if(src == bStartReady){
			int[] selectedIndexes = readyBrickList.getSelectedIndexes();
			Identifier[] ids = new Identifier[selectedIndexes.length];
			for(int i = 0; i < selectedIndexes.length; i++){
				ids[i] = (Identifier)readyBrickVector.elementAt(selectedIndexes[i]);
			}
			controller.start(ids);
		}
		else if(src == bSuspendStarted){
			int[] selectedIndexes = startedBrickList.getSelectedIndexes();
			
			Identifier[] ids = new Identifier[selectedIndexes.length];
			for(int i = 0; i < selectedIndexes.length; i++){
				ids[i] = (Identifier)startedBrickVector.elementAt(selectedIndexes[i]);
			}
			controller.suspend(ids);
		}
		else if(src == bStopStarted){
			int[] selectedIndexes = startedBrickList.getSelectedIndexes();
			Identifier[] ids = new Identifier[selectedIndexes.length];
			for(int i = 0; i < selectedIndexes.length; i++){
				ids[i] = (Identifier)startedBrickVector.elementAt(selectedIndexes[i]);
			}
			controller.stop(ids);
		}
		else if(src == bResumeSuspended){
			int[] selectedIndexes = suspendedBrickList.getSelectedIndexes();
			Identifier[] ids = new Identifier[selectedIndexes.length];
			for(int i = 0; i < selectedIndexes.length; i++){
				ids[i] = (Identifier)suspendedBrickVector.elementAt(selectedIndexes[i]);
			}
			controller.resume(ids);
		}
		else if(src == bStopSuspended){
			int[] selectedIndexes = suspendedBrickList.getSelectedIndexes();
			Identifier[] ids = new Identifier[selectedIndexes.length];
			for(int i = 0; i < selectedIndexes.length; i++){
				ids[i] = (Identifier)suspendedBrickVector.elementAt(selectedIndexes[i]);
			}
			controller.stop(ids);
		}
		
	}

	public void brickAdded(ArchitectureManager manager, Identifier id){
		checkBrickState(id);
	}
	
	public void brickRemoving(ArchitectureManager manager, Identifier id){
	}
		
	public void brickRemoved(ArchitectureManager manager, Identifier id){
		removeBrick(id);
	}
	
	public void weldAdded(ArchitectureManager manager, Weld w){}
	public void weldRemoving(ArchitectureManager manager, Weld w){}
	public void weldRemoved(ArchitectureManager manager, Weld w){}

}

