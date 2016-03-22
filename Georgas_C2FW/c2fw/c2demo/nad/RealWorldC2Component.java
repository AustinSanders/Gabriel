package c2demo.nad;

import c2.fw.*;
import c2.legacy.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;

import edu.uci.ics.widgets.*;

public class RealWorldC2Component extends AbstractC2DelegateBrick{

	private int uid = 100;

	private RealWorldFrame rwf = null;
	
	private Map entityMap = new HashMap();
	private Vector entityTypes;
	private Vector entityPaths;
	
	public RealWorldC2Component(Identifier id){
		super(id);
		this.addLifecycleProcessor(new RealWorldLifecycleProcessor());
		this.addMessageProcessor(new RealWorldMessageProcessor());
	}
	
	class RealWorldLifecycleProcessor extends LifecycleAdapter{
		public void init(){
			entityTypes = new Vector();
			entityTypes.addElement(new EntityTypeSpec(
				"F/A-18 Hornet", Constants.ENTITY_TYPE_AIR_FIXEDWING, 100));
			entityTypes.addElement(new EntityTypeSpec(
				"Cessna Skyhawk", Constants.ENTITY_TYPE_AIR_FIXEDWING, 20));
			entityTypes.addElement(new EntityTypeSpec(
				"Sikorsky Comanche", Constants.ENTITY_TYPE_AIR_ROTORCRAFT, 20));
			entityTypes.addElement(new EntityTypeSpec(
				"Passenger Helicopter", Constants.ENTITY_TYPE_AIR_ROTORCRAFT, 15));
			entityTypes.addElement(new EntityTypeSpec(
				"Stealth Aircraft", Constants.ENTITY_TYPE_AIR_STEALTH, 70));
			entityTypes.addElement(new EntityTypeSpec(
				"A-10 Thunderbolt", Constants.ENTITY_TYPE_AIR_FIXEDWINGBOMBER, 40));
			entityTypes.addElement(new EntityTypeSpec(
				"Troops", Constants.ENTITY_TYPE_LAND_TROUPS, 1));
			entityTypes.addElement(new EntityTypeSpec(
				"M1 Abrams Tank", Constants.ENTITY_TYPE_LAND_ARMOR, 2));
			entityTypes.addElement(new EntityTypeSpec(
				"Navy Destroyer", Constants.ENTITY_TYPE_SEA_DESTROYER, 2));
			entityTypes.addElement(new EntityTypeSpec(
				"Navy Battleship", Constants.ENTITY_TYPE_SEA_BATTLESHIP, 2));
			entityTypes.addElement(new EntityTypeSpec(
				"Aircraft Carrier", Constants.ENTITY_TYPE_SEA_CARRIER, 2));
			entityTypes.addElement(new EntityTypeSpec(
				"Navy Hovercraft", Constants.ENTITY_TYPE_SEA_HOVERCRAFT, 2));
			entityTypes.addElement(new EntityTypeSpec(
				"Mk109 Stingray Ship-to-Air Missile", Constants.ENTITY_TYPE_AIR_SAM, 180));
			 
			entityPaths = new Vector();
			entityPaths.addElement(new EntityPathSpec(
				"Triangular Patrol", new int[]{100, 9900, 100}, new int[]{100, 5000, 9900}, false));
			entityPaths.addElement(new EntityPathSpec(
				"Orbit AWACS", new int[]{5000, 2000, 5000, 7000}, new int[]{2000, 5000, 7000, 5000}, false));
			entityPaths.addElement(new EntityPathSpec(
				"N-S Flybys", new int[]{3000, 3500}, new int[]{-500, 10500}, false));
			entityPaths.addElement(new EntityPathSpec(
				"E-W Flybys", new int[]{-500, 10500}, new int[]{3000, 3500}, false));
			entityPaths.addElement(new EntityPathSpec(
				"Approach AWACS from South", new int[]{5000, 5000}, new int[]{7500, 5000}, true));
			entityPaths.addElement(new EntityPathSpec(
				"Sea Patrol", new int[]{7500, 7600}, new int[]{2500, 2600}, false));
			entityPaths.addElement(new EntityPathSpec(
				"Carrier At Sea", new int[]{7000, 7000}, new int[]{7001, 7001}, false));
			entityPaths.addElement(new EntityPathSpec(
				"Carrier Air Support", new int[]{7000, 4000, 4000}, new int[]{7000, 2000, 7000}, false));
			entityPaths.addElement(new EntityPathSpec(
				"Missile Destroyer At Sea", new int[]{2000, 2000}, new int[]{7001, 7001}, false));
			entityPaths.addElement(new EntityPathSpec(
				"Missile Destroyer SAM", new int[]{2000, 5000}, new int[]{7001, 5000}, true));
			
			rwf = new RealWorldFrame();
		}
	}
	
	class RealWorldMessageProcessor implements MessageProcessor{
		public void handle(Message m){
			if(m instanceof MissileExplosionMessage){
				doEMPZap();
			}
		}
	}
	
	static class EntityTypeSpec{
		private String desc;
		private int entityType;
		private int speed;
		
		public EntityTypeSpec(String desc, int entityType, int speed){
			this.desc = desc;
			this.entityType = entityType;
			this.speed = speed;
		}
		
		public String getDescription(){
			return desc;
		}
		
		public int getEntityType(){
			return entityType;
		}
		
		public int getSpeed(){
			return speed;
		}
		
		public String toString(){
			return desc;
		}
	}

	static class EntityPathSpec{
		private String desc;
		private int[] wx;
		private int[] wy;
		private boolean terminate;
		
		public EntityPathSpec(String desc, int[] wx, int[] wy, boolean terminate){
			this.desc = desc;
			this.wx = wx;
			this.wy = wy;
			this.terminate = terminate;
		}
		
		public String getDescription(){
			return desc;
		}

		public int[] getWaypointXs(){
			return wx;
		}
		
		public int[] getWaypointYs(){
			return wy;
		}
		
		public boolean getTerminate(){
			return terminate;
		}
		
		public String toString(){
			return desc;
		}
	}
	
	public void doEMPZap(){
		if(rwf != null){
			rwf.disableEMPButton();
		}
		EMPZapMessage zapMessage = new EMPZapMessage();
		sendToAll(zapMessage, bottomIface);
	}
		
	class RealWorldFrame extends JFrame implements ActionListener{
		protected JComboBox cbEntityType;
		protected JComboBox cbEntityPath;
		
		protected JRadioButton rbFriendly;
		protected JRadioButton rbUnknown;
		protected JRadioButton rbHostile;
		
		protected JTextField tfAnnotation1;
		protected JTextField tfAnnotation2;
		
		protected JButton empZapButton;
		
		protected JButton createButton;
		
		public RealWorldFrame(){
			super("World Populator");
			
			Toolkit tk = getToolkit();
			Dimension screenSize = tk.getScreenSize();
			double xSize = (400);
			double ySize = (260);
			double xPos = (screenSize.getWidth() * 0.70);
			double yPos = (screenSize.getHeight() * 0.05);
			
			cbEntityType = new JComboBox(entityTypes);
			cbEntityPath = new JComboBox(entityPaths);

			rbFriendly = new JRadioButton("Friendly", true);
			rbUnknown = new JRadioButton("Unknown/Neutral", false);
			rbHostile = new JRadioButton("Hostile", false);
			
			ButtonGroup bg = new ButtonGroup();
			bg.add(rbFriendly);
			bg.add(rbUnknown);
			bg.add(rbHostile);
			
			tfAnnotation1 = new JTextField(15);
			tfAnnotation2 = new JTextField(15);
			
			empZapButton = new JButton("EMP Zap");
			empZapButton.addActionListener(this);
			
			createButton = new JButton("Create Entity");
			createButton.addActionListener(this);
			
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
			
			leftPanel.add(new JPanelUL(new JLabel("Entity Type:")));
			leftPanel.add(new JPanelUL(cbEntityType));
			leftPanel.add(new JPanelUL(new JLabel("Mission Profile:")));
			leftPanel.add(new JPanelUL(cbEntityPath));
			leftPanel.add(Box.createVerticalStrut(10));
			leftPanel.add(new JPanelUL(empZapButton));
			leftPanel.add(Box.createGlue());
			
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
			
			rightPanel.add(new JPanelUL(new JLabel("Affiliation:")));
			JPanel bp = new JPanel();
			bp.setLayout(new BoxLayout(bp, BoxLayout.Y_AXIS));
			bp.add(rbFriendly);
			bp.add(rbUnknown);
			bp.add(rbHostile);
			rightPanel.add(new JPanelUL(bp));
			
			rightPanel.add(new JPanelUL(new JLabel("Annotation Data:")));
			rightPanel.add(new JPanelUL(tfAnnotation1));
			rightPanel.add(new JPanelUL(tfAnnotation2));
			
			rightPanel.add(Box.createGlue());
			
			JPanel contentPanel = new JPanel();
			//contentPanel.setLayout(new GridLayout(1,2));
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
			contentPanel.add(new JPanelUL(leftPanel));
			contentPanel.add(new JPanelUL(rightPanel));

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.add(createButton);
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			
			mainPanel.add("Center", contentPanel);
			
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add("Center", new JPanelIS(mainPanel, 5));
			this.getContentPane().add("South", new JPanelIS(buttonPanel, 2));
		
			setVisible(true);
			setSize((int)xSize, (int)ySize);
			setLocation((int)xPos, (int)yPos);
			setVisible(true);
			paint(getGraphics());
		}
		
		public void actionPerformed(ActionEvent evt){
			if(evt.getSource().equals(createButton)){
				String annotation1 = tfAnnotation1.getText();
				String annotation2 = tfAnnotation2.getText();
				
				tfAnnotation1.setText("");
				tfAnnotation2.setText("");
				
				EntityTypeSpec et = (EntityTypeSpec)cbEntityType.getSelectedItem();
				EntityPathSpec ep = (EntityPathSpec)cbEntityPath.getSelectedItem();
				
				int affiliation = Constants.UNKNOWN;
				if(rbFriendly.isSelected()){
					affiliation = Constants.FRIENDLY;
				}
				else if(rbUnknown.isSelected()){
					affiliation = Constants.UNKNOWN;
				}
				else if(rbHostile.isSelected()){
					affiliation = Constants.HOSTILE;
				}
				
				CreateEntityMessage cem = new CreateEntityMessage(
					et.toString() + " " + uid, et.getEntityType(),
					ep.getWaypointXs(), ep.getWaypointYs(), et.getSpeed(), ep.getTerminate(),
					affiliation, new String[]{et.toString() + " " + uid, annotation1, annotation2});
	
				uid++;
				
				sendToAll(cem, bottomIface);
			}
			else if(evt.getSource().equals(empZapButton)){
				doEMPZap();
			}
		}
		
		public void disableEMPButton(){
			empZapButton.setEnabled(false);
		}
	}
	
}
