package c2.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import c2.fw.*;

public class GUISteppableEngineManager extends JFrame implements ActionListener{
	
	private SteppableArchitectureEngine engine;
	
	private JCheckBox cbSmallDelay; 
	private JButton bStartStop;
	private JButton bStep;
	private boolean shouldRun;
	private Runner runner;
	
	public GUISteppableEngineManager(SteppableArchitectureEngine engine){
		super("Steppable Engine Manager");
		
		this.engine = engine;
		
		runner = new Runner();
		runner.start();
		
		cbSmallDelay = new JCheckBox("Nice");
		
		bStartStop = new JButton("Start");
		bStartStop.addActionListener(this);
		
		bStep = new JButton("Step");
		bStep.addActionListener(this);
		
		this.setVisible(true);
		this.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
		this.getContentPane().add(cbSmallDelay);
		this.getContentPane().add(bStartStop);
		this.getContentPane().add(bStep);
		this.setSize(300, 70);
		this.setLocation(200, 200);
		this.setVisible(true);
		this.paint(getGraphics());
	}

	public void actionPerformed(ActionEvent evt){
		if(evt.getSource() == bStartStop){
			if(bStartStop.getText().equals("Start")){
				bStartStop.setText("Stop");
				bStep.setEnabled(false);
				runner.setShouldRun(true);
			}
			else if(bStartStop.getText().equals("Stop")){
				runner.setShouldRun(false);
				bStartStop.setText("Start");
				bStep.setEnabled(true);
			}
		}
		else if(evt.getSource() == bStep){
			engine.step();
		}
	}
		
		
	class Runner extends Thread{
		
		private Object waiter = new Object();
		private boolean shouldRun = false;
		private boolean terminate = false;
		
		public void wakeUp(){
			synchronized(waiter){
				waiter.notifyAll();
			}
		}
		
		public void setShouldRun(boolean shouldRun){
			this.shouldRun = shouldRun;
			wakeUp();
		}
		
		public void terminate(){
			terminate = true;
			wakeUp();
		}
		
		public void run(){
			while(true){
				if(terminate) return;
				if(!shouldRun){
					try{
						synchronized(waiter){
							waiter.wait();
						}
						if(terminate) return;
					}
					catch(InterruptedException e){
						continue;
					}
				}
				if(terminate) return;
				if(cbSmallDelay.isSelected()){
					try{
						Thread.sleep(50);
						Thread.yield();
					}
					catch(InterruptedException e){
					}
				}
				engine.step();
			}
		}
	}
}

