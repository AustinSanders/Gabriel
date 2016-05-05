package c2demo.coalition;

import c2.fw.ArchitectureController;
import c2.fw.NamedPropertyMessage;
import c2.fw.Message;
import c2.fw.Timer;
import c2.fw.TimerListener;
import c2.legacy.AbstractC2Brick;
import c2.fw.Identifier;

public class TimeClockEventGenerator extends AbstractC2Brick
	implements TimerListener {

	private int currentTickCount;

	public TimeClockEventGenerator(Identifier id,
		ArchitectureController controller)
	{
		super(id);
		//((Timer)controller).addTimerListener(this);  // redundant
	}

	public void init()
	{
	}

	public void destroy()
	{
	}

	public void begin()
	{
		new ClockThread();
	}

	public void end()
	{
	}

	public void handle(Message m)
	{
	}

	class ClockThread extends Thread{
		int tickCount = 0;

		public ClockThread(){
			this.setDaemon(true);
			this.start();
		}

		public void run(){
			while(true){
				try{
					Thread.sleep(200);
				}
				catch(InterruptedException e){}
				tickCount += 100;
				doTick(tickCount);
			}
		}
	}

	public void doTick(int tickCount){
		NamedPropertyMessage npm;

		currentTickCount = tickCount;

		npm = new NamedPropertyMessage("clock");
		npm.addParameter("tick", tickCount);
		sendNotification(npm);
	}

	public void tick(int tickCount){/*
	NamedPropertyMessage npm;

	currentTickCount = tickCount;

	if (currentTickCount != 0 && currentTickCount % 100 == 0) {
	npm = new NamedPropertyMessage("clock");
	npm.addParameter("tick", tickCount);
	sendNotification(npm);
	}*/
	}
}
