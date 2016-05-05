package c2demo.chatsys;

import c2.fw.*;
import c2.legacy.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientC2Component extends AbstractC2DelegateBrick{
	
	protected ChatComponentGUI gui;
	
	public ClientC2Component(Identifier id){
		super(id);
		this.addLifecycleProcessor(new ClientC2ComponentLifecycleProcessor());
		this.addMessageProcessor(new ClientC2ComponentMessageProcessor());
	}
	
	class ClientC2ComponentLifecycleProcessor extends LifecycleAdapter{
		public void begin(){
			gui = new ChatComponentGUI();
		}
		
		public void end() {
			gui.setVisible(false);
			gui.dispose();
		}
	}
	
	class ClientC2ComponentMessageProcessor implements MessageProcessor{
		public void handle(Message m){
      if(m.getDestination().getInterfaceIdentifier().equals(topIface.getIdentifier())){
				if(m instanceof NamedPropertyMessage){
					NamedPropertyMessage npm = (NamedPropertyMessage)m;
					if(npm.getName().equals("ChatMessage")){
						String text = (String)npm.getParameter("text");
						gui.addMessageToTranscript(text);
					}
				}
			}
		}
	}

	class ChatComponentGUI extends JFrame implements ActionListener{
		JTextArea transcript;
		JTextField entryField;
		JButton sendButton;
		StringBuffer transcriptBuf;
		
		public ChatComponentGUI(){
			super(getIdentifier().toString());
			transcriptBuf = new StringBuffer();
			transcript = new JTextArea();
			entryField = new JTextField(20);
			sendButton = new JButton("Send");
			sendButton.addActionListener(this);
			
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add("Center", transcript);
			
			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			bottomPanel.add(entryField);
			bottomPanel.add(sendButton);
			this.getContentPane().add("South", bottomPanel);
			
			this.setSize(500, 400);
			this.setVisible(true);
			validate();
			repaint();
		}
		
		public void addMessageToTranscript(String text){
			transcriptBuf.append(text);
			transcriptBuf.append(System.getProperty("line.separator"));
			transcript.setText(transcriptBuf.toString());
		}
		
		public void actionPerformed(ActionEvent evt){
			String text = entryField.getText();
			if(!text.equals("")){
				NamedPropertyMessage chatMessage = new NamedPropertyMessage("ChatMessage");
				chatMessage.addParameter("text", getIdentifier() + ": " + text);
				sendToAll(chatMessage, topIface);
				entryField.setText("");
			}
		}
		
	}
} 
