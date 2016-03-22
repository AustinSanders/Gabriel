package c2demo.byrne;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

import edu.uci.ics.widgets.WidgetUtils;

public class TalkingHead extends JFrame {

	boolean stopCapture = false;
	ByteArrayOutputStream byteArrayOutputStream;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	JProgressBar pb;
	JImageComponent imageComponent;

	class JImageComponent extends JComponent{
		Image[] images;

		public JImageComponent(String name, int num) {
			images = new Image[num];
			for (int i = 0; i < num; i++) {
				String imageName =
					"c2demo/byrne/res/" + name + i + ".png";
				System.out.println(imageName);
				ImageIcon ii = WidgetUtils.getImageIcon(imageName);
				images[i] = ii.getImage();
			}
		}

		private int imageNum = 0;
		
		public int getImageNum(){
			return imageNum;
		}
		
		public Dimension getPreferredSize(){
			return new Dimension(400, 400);
		}

		public void setImageNum(int imageNum) {
			this.imageNum = imageNum;
			repaint();
		}

		public void paintComponent(Graphics g) {
			g.drawImage(images[imageNum], 0, 0, Color.WHITE, null);
		}
	}

	public static void main(String args[]) {
		new TalkingHead();
	} //end main

	public TalkingHead() { //constructor
		final JButton stopBtn = new JButton("Stop");
		final JButton playBtn = new JButton("Playback");

		pb = new JProgressBar();
		//imageComponent = new JImageComponent("strongbad", 5);
		imageComponent = new JImageComponent("dick", 5);

		stopBtn.setEnabled(false);
		playBtn.setEnabled(true);

		try {
			File soundFile = new File("sound.wav");
			FileInputStream fis = new FileInputStream(soundFile);
			byte[] buf = new byte[4096];
			byteArrayOutputStream = new ByteArrayOutputStream();
			while (true) {
				long bytesRead = fis.read(buf);
				if (bytesRead == -1) {
					fis.close();
					break;
				}
				byteArrayOutputStream.write(buf, 0, (int) bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopBtn.setEnabled(false);
				playBtn.setEnabled(true);
				//Terminate the capturing of
				// input data from the
				// microphone.
				stopCapture = true;
			} //end actionPerformed
		} //end ActionListener
		); //end addActionListener()
		getContentPane().add(stopBtn);

		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Play back all of the data
				// that was saved during
				// capture.
				playAudio();
			} //end actionPerformed
		} //end ActionListener
		); //end addActionListener()
		getContentPane().add(playBtn);
		//getContentPane().add(pb);
		getContentPane().add(imageComponent);

		getContentPane().setLayout(new FlowLayout());
		setTitle("Capture/Playback Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(250, 250);
		setVisible(true);
	} //end constructor

	//This method captures audio input
	// from a microphone and saves it in
	// a ByteArrayOutputStream object.
	private void captureAudio() {
		try {
			//Get everything set up for
			// capture
			audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo =
				new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();

			//Create a thread to capture the
			// microphone data and start it
			// running.  It will run until
			// the Stop button is clicked.
			Thread captureThread = new Thread(new CaptureThread());
			captureThread.start();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		} //end catch
	} //end captureAudio method

	//This method plays back the audio
	// data that has been saved in the
	// ByteArrayOutputStream
	private void playAudio() {
		try {
			//Get everything set up for
			// playback.
			//Get the previously-saved data
			// into a byte array object.
			byte audioData[] = byteArrayOutputStream.toByteArray();
			//Get an input stream on the
			// byte array containing the data
			InputStream byteArrayInputStream =
				new ByteArrayInputStream(audioData);
			AudioFormat audioFormat = getAudioFormat();
			audioInputStream =
				new AudioInputStream(
					byteArrayInputStream,
					audioFormat,
					audioData.length / audioFormat.getFrameSize());
			DataLine.Info dataLineInfo =
				new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();

			//Create a thread to play back
			// the data and start it
			// running.  It will run until
			// all the data has been played
			// back.
			//Thread playThread =
			//    new Thread(new PlayThread());
			PlayThread playThread = new PlayThread();
			MonitorThread mt = new MonitorThread(playThread);
			mt.start();
			playThread.start();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		} //end catch
	} //end playAudio

	//This method creates and returns an
	// AudioFormat object =for a given set
	// of format parameters.  If these
	// parameters don't work well for
	// you, try some of the other
	// allowable parameter values, which
	// are shown in comments following
	// the declarations.
	private AudioFormat getAudioFormat() {
		float sampleRate = 44100.0F;
		//8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		//8,16
		int channels = 1;
		//1,2
		boolean signed = true;
		//true,false
		boolean bigEndian = false;
		//true,false
		return new AudioFormat(
			sampleRate,
			sampleSizeInBits,
			channels,
			signed,
			bigEndian);
	} //end getAudioFormat
	//===================================//

	//Inner class to capture data from
	// microphone
	class CaptureThread extends Thread {
		//An arbitrary-size temporary holding
		// buffer
		byte tempBuffer[] = new byte[10000];
		public void run() {
			byteArrayOutputStream = new ByteArrayOutputStream();
			stopCapture = false;
			try { //Loop until stopCapture is set
				// by another thread that
				// services the Stop button.
				while (!stopCapture) {
					//Read data from the internal
					// buffer of the data line.
					int cnt =
						targetDataLine.read(tempBuffer, 0, tempBuffer.length);
					if (cnt > 0) {
						//Save data in output stream
						// object.
						byteArrayOutputStream.write(tempBuffer, 0, cnt);
					} //end if
				} //end while
				byteArrayOutputStream.close();
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			} //end catch
		} //end run
	} //end inner class CaptureThread
	//===================================//
	//Inner class to play back the data
	// that was saved.
	class PlayThread extends Thread {
		int size = 10000;
		byte tempBuffer[] = new byte[size];

		public void run() {
			try {
				int cnt;
				//Keep looping until the input
				// read method returns -1 for
				// empty stream.
				while ((cnt =
					audioInputStream.read(tempBuffer, 0, tempBuffer.length))
					!= -1) {

					if (cnt > 0) {
						//Write data to the internal
						// buffer of the data line
						// where it will be delivered
						// to the speaker.
						sourceDataLine.write(tempBuffer, 0, cnt);
					} //end if
				} //end while
				//Block and wait for internal
				// buffer of the data line to
				// empty.
				sourceDataLine.drain();
				sourceDataLine.close();
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			} //end catch
		} //end run
	} //end inner class PlayThread
	//===================================//

	public class MonitorThread extends Thread {
		private PlayThread pt;

		final int QUANTA = 5;

		public MonitorThread(PlayThread pt) {
			this.pt = pt;
		}

		public void run() {
			try {
				AudioFormat fmt = sourceDataLine.getFormat();
				int frameSize = fmt.getFrameSize();
				byte[] sample = byteArrayOutputStream.toByteArray();

				double minLevel = Double.MAX_VALUE;
				double maxLevel = Double.MIN_VALUE;
				
				double sumOfLevels = 0;
				int numAdders = 0;
				
				for (int s = 0; s < sample.length; s += 5000){
					long level = 0;
					int itCnt = 0;
					for (int i = s; i < s + 5000; i++) {
						if (i >= sample.length)
							break;
						itCnt++;
						level += sample[i] * sample[i];
					}
					level /= itCnt;
					double dLevel = Math.sqrt(level);
					sumOfLevels += dLevel;
					numAdders++;
					if (dLevel < minLevel)
						minLevel = dLevel;
					if (dLevel > maxLevel)
						maxLevel = dLevel;
				}
				double averageLevel = sumOfLevels / numAdders;
				
				System.out.println("minLevel = " + minLevel);
				System.out.println("maxLevel = " + maxLevel);
				System.out.println("averageLevel = " + averageLevel);
				int oldl = 0;

				int lastq = 0;
				
				int lastFrameCnt = -1;
				
				while (true) {
					int frameCnt = sourceDataLine.getFramePosition();
					if(frameCnt == lastFrameCnt){
						lastFrameCnt = frameCnt;
						Thread.sleep(20);
						continue;
					}
					lastFrameCnt = frameCnt;
					int arrayIndex = frameCnt * frameSize;
					long level = 0;
					int itCnt = 0;
					for (int i = arrayIndex; i < arrayIndex + 256; i++) {
						if (i >= sample.length)
							return;
						itCnt++;
						level += (sample[i] * sample[i]);
					}
					if(itCnt != 0){
						level /= itCnt;
					}
					else{
						level = 0;
					}
					double dLevel = Math.sqrt(level);
					//System.out.println(level + "   :   " + itCnt);

					double scaledLevel = dLevel - minLevel;
					if (scaledLevel < 0.0d)
						scaledLevel = 0.0d;
					scaledLevel = scaledLevel / (maxLevel - minLevel);
					scaledLevel *= 100.0d;
					
					//scaledLevel = dLevel;

					System.out.println(scaledLevel);

					//imageComponent.setImageNum((int)scaledLevel / 20);
					/*
					int l = (int)(scaledLevel / 1.8);
					System.out.println("l = " + l);
					System.out.println("oldl = " + oldl);
					int dl = l - oldl;
					if(dl < 0) dl = -dl;
					if(l > oldl){
						for(int k = 0; k < dl; k++){ 
							int currentImageNum = imageComponent.getImageNum();
							if((currentImageNum + 1) < 5){
								imageComponent.setImageNum(currentImageNum + 1);
							}
							else break;
							Thread.sleep(30);
						}
					}
					else if(l < oldl){
						for(int k = 0; k < dl; k++){ 
							int currentImageNum = imageComponent.getImageNum();
							if((currentImageNum - 1) > 0){
								imageComponent.setImageNum(currentImageNum - 1);
							}
							else break;
							Thread.sleep(30);
						}
					}
					else{
						Thread.sleep(50);
					}
					oldl = l;
					*/
					
					int lastSameCount = 0;
					
					int accum = 0;
					double q = 100;
					boolean shown = false;
					for (int i = 0; i < QUANTA; i++) {
						q = q / 2.0d;
						//for my sb sound:
						//q = q / 2.2d;
						int lev = 100 - (int) q;
						//System.out.println("lev" + lev);
						if (((int) scaledLevel) < lev) {
							System.out.println("i" + i);
							int oldi = imageComponent.getImageNum();
							if(oldi != i){
								int diff = i - oldi;
								int dir = diff > 0 ? 1 : -1;
								for(int j = oldi + dir; j != i; j += dir){
									imageComponent.setImageNum(j);
									Thread.sleep(15);
								}
								imageComponent.setImageNum(i);
							}
							shown = true;
							//imageComponent.setImageNum(i);
							break;
						}
					}
					if(!shown){
						imageComponent.setImageNum(QUANTA - 1);
					}
					
					//pb.setValue((int)scaledLevel);
					//System.out.println("ScaledLevel: " + scaledLevel);
					//System.out.println("Level: " + dLevel);
					//System.out.println("Pow: " + Math.pow(dLevel, 2));
					Thread.sleep(30);
				}
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}
		}
	}
}

