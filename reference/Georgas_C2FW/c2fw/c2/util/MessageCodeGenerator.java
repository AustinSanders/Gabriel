package c2.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.uci.ics.widgets.*;

public class MessageCodeGenerator extends JFrame{

	DefaultTableModel tableModel;
	JTable propertiesTable;
	
	JTextField classNameField;
	JTextField propertyNameField;
	JTextField propertyClassField;

	public static void main(String[] args){
		new MessageCodeGenerator();
	}
	
	public MessageCodeGenerator(){
		super("Message Code Generator Tool");
		
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent evt){
					System.exit(0);
				}
			}
		);
		
		classNameField = new JTextField(30);
		propertyNameField = new JTextField(30);
		propertyClassField = new JTextField(30);
		
		tableModel = new DefaultTableModel();
		tableModel.addColumn("Property Name");
		tableModel.addColumn("Property Class");
		propertiesTable = new JTable(tableModel);
		propertiesTable.setColumnSelectionAllowed(false);
		//propertiesTable.setCellSelectionAllowed(false);
		propertiesTable.setRowSelectionAllowed(true);
		propertiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		mainPanel.add(new JPanelUL(new JLabel("Fully Qualified Class Name")));
		mainPanel.add(new JPanelUL(classNameField));
		
		mainPanel.add(new JSeparator());
		mainPanel.add(Box.createHorizontalStrut(15));
		
		mainPanel.add(new JPanelUL(new JLabel("Message Properties")));
		mainPanel.add(new JScrollPane(propertiesTable));
		
		JPanel tbp = new JPanel();
		tbp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JButton removeButton = new JButton("Remove Selected");
		removeButton.addActionListener(new RemovePropertyActionListener());
		tbp.add(removeButton);
		
		JButton moveUpButton = new JButton("Move Up");
		moveUpButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					int selectedRow = propertiesTable.getSelectedRow();
					if(selectedRow == -1){
						return;
					}
					if(selectedRow == 0){
						return;
					}
					tableModel.moveRow(selectedRow, selectedRow, selectedRow - 1);
					propertiesTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
				}
			}
		);
		tbp.add(moveUpButton);
		
		JButton moveDownButton = new JButton("Move Down");
		moveDownButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					int selectedRow = propertiesTable.getSelectedRow();
					if(selectedRow == -1){
						return;
					}
					if(selectedRow == (tableModel.getRowCount() - 1)){
						return;
					}
					tableModel.moveRow(selectedRow, selectedRow, selectedRow + 1);
					propertiesTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
				}
			}
		);
		tbp.add(moveDownButton);
		
		mainPanel.add(new JPanelUL(tbp));
		
		mainPanel.add(new JSeparator());
		mainPanel.add(Box.createHorizontalStrut(15));
		
		mainPanel.add(new JPanelUL(new JLabel("Property Class")));
		mainPanel.add(new JPanelUL(propertyClassField));

		mainPanel.add(new JPanelUL(new JLabel("Property Name")));
		mainPanel.add(new JPanelUL(propertyNameField));
		
		JButton addPropertyButton = new JButton("Add this property");
		addPropertyButton.addActionListener(new AddPropertyActionListener());
		mainPanel.add(new JPanelUL(addPropertyButton));
		
		mainPanel.add(new JSeparator());
		mainPanel.add(Box.createHorizontalStrut(15));

		mainPanel.add(Box.createGlue());
		
		JButton generateButton = new JButton("Generate Code!");
		generateButton.addActionListener(new GenerateCodeActionListener());
		
		JPanel bp = new JPanel();
		bp.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bp.add(generateButton);
		mainPanel.add(bp);
		
		this.getContentPane().add(mainPanel);
		
		this.setSize(500, 400);
		this.setLocation(100, 100);
		this.setVisible(true);
		this.validate();
		this.repaint();
	}

	class RemovePropertyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent evt){
			int[] selectedRows = propertiesTable.getSelectedRows();
			for(int i = (selectedRows.length - 1); i >= 0; i--){
				tableModel.removeRow(selectedRows[i]);
			}
		}
	}			
			
	class AddPropertyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent evt){
			String name = propertyNameField.getText().trim();
			String val = propertyClassField.getText().trim();
			if(name.equals("")){
				JOptionPane.showMessageDialog(MessageCodeGenerator.this, "Enter a name.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(val.equals("")){
				JOptionPane.showMessageDialog(MessageCodeGenerator.this, "Enter a class.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			propertyNameField.setText("");
			propertyClassField.setText("");
			tableModel.addRow(new Object[]{name, val});
		}
	}
	
	class GenerateCodeActionListener implements ActionListener{
		public void actionPerformed(ActionEvent evt){
			String fqClassName = classNameField.getText();
			if(fqClassName.equals("")){
				JOptionPane.showMessageDialog(MessageCodeGenerator.this, "Enter a class name.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			int rowCount = tableModel.getRowCount();
			String[] propertyNames = new String[rowCount];
			String[] propertyClasses = new String[rowCount];
			
			for(int i = 0; i < rowCount; i++){
				propertyNames[i] = (String)tableModel.getValueAt(i, 0);
				propertyClasses[i] = (String)tableModel.getValueAt(i, 1);
			}
			generateCode(fqClassName, propertyNames, propertyClasses);
			
		}
	}
	
	private static String capFirstLetter(String s){
		if(s == null) return null;
		if(s.equals("")) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toUpperCase(s.charAt(0)));
		if(s.length() > 0){
			sb.append(s.substring(1));
		}
		return sb.toString();
	}
	
	public static boolean isSimpleTypeName(String s){
		return
			s.equals("byte") ||
			s.equals("short") ||
			s.equals("boolean") ||
			s.equals("int") ||
			s.equals("long") ||
			s.equals("float") ||
			s.equals("double") ||
			s.equals("char");
	}
	
	public void generateCode(String fqClassName, String[] propertyNames, String[] propertyClasses){
		String eol = "\n"; //System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		
		String packageName;
		String className;
		int lastDotIndex = fqClassName.lastIndexOf(".");
		if(lastDotIndex == -1){
			packageName = null;
			className = fqClassName;
		}
		else{
			packageName = fqClassName.substring(0, lastDotIndex);
			className = fqClassName.substring(lastDotIndex + 1);
		}

		if(packageName != null){
			sb.append("package " + packageName + ";" + eol);
			sb.append(eol);
		}
		
		sb.append("import c2.fw.*;" + eol);
		sb.append(eol);
		
		sb.append("public class " + className + " extends NamedPropertyMessage{" + eol);
		sb.append("\tpublic " + className + "(");
		for(int i = 0; i < propertyNames.length; i++){
			sb.append(propertyClasses[i] + " " + propertyNames[i]);
			if(i != (propertyNames.length - 1)){
				sb.append(", ");
			}
		}
		sb.append("){" + eol);
		sb.append("\t\tsuper(\"" + className + "\");" + eol);
		for(int i = 0; i < propertyNames.length; i++){
			sb.append("\t\tsuper.addParameter(\"");
			sb.append(propertyNames[i]);
			sb.append("\", " + propertyNames[i] + ");" + eol);
		}
		sb.append("\t}" + eol);
		sb.append(eol);
		
		sb.append("\tprotected " + className + "(" + className + " copyMe){" + eol);
		sb.append("\t\tsuper(copyMe);" + eol);
		sb.append("\t}" + eol);
		sb.append(eol);
		
		sb.append("\tpublic Message duplicate(){" + eol);
		sb.append("\t\treturn new " + className + "(this);" + eol);
		sb.append("\t}" + eol);
		sb.append(eol);
		
		for(int i = 0; i < propertyNames.length; i++){
			sb.append("\tpublic void set" + capFirstLetter(propertyNames[i]) + "("  + propertyClasses[i] + " " + propertyNames[i] + "){" + eol);
			sb.append("\t\taddParameter(\"" + propertyNames[i] + "\", " + propertyNames[i] + ");" + eol);
			sb.append("\t}" + eol);
			sb.append(eol);
			
			sb.append("\tpublic " + propertyClasses[i] + " get" + capFirstLetter(propertyNames[i]) + "(){" + eol);
			if(isSimpleTypeName(propertyClasses[i])){
				sb.append("\t\treturn get" + capFirstLetter(propertyClasses[i]) + "Parameter(\"" + propertyNames[i] + "\");" + eol);
			}
			else{
				sb.append("\t\treturn (" + propertyClasses[i] + ")getParameter(\"" + propertyNames[i] + "\");" + eol);
			}
			sb.append("\t}" + eol);
			sb.append(eol);
		}
		
		sb.append("}" + eol);
		sb.append(eol);
		
		JFrame f = new JFrame();
		f.setSize(500, 400);
		f.setLocation(150, 150);
		JTextArea ta = new JTextArea();
		ta.setText(sb.toString());
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add("Center", new JScrollPane(ta));
		ta.setEditable(false);
		f.setVisible(true);
	}
}
