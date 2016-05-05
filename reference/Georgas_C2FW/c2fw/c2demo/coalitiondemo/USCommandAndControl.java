/*
 * Copyright (c) 2001-2002 Regents of the University of California.
 * All rights reserved.
 *
 * This software was developed at the University of California, Irvine.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the University of California, Irvine.  The name of the
 * University may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package c2demo.coalitiondemo;

import c2.legacy.AbstractC2Brick;
import c2.fw.Identifier;
import c2.fw.Message;
import c2.fw.NamedPropertyMessage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Vector;

//Swing Stuff
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * Command and Control component.  Listens for entity events and
 * determines the affiliation based on the country of origin, and 
 * also determines the battle dimension based on the entity type.
 *
 * @author Kari Nies
 */
public class USCommandAndControl extends AbstractC2Brick{
    
    private boolean firstTimeCarrier = true;
    private boolean firstTimeAirDefense = true;
    private boolean preImpact = true;
    private Vector coordVector = new Vector(5);
    private int flightPathSeg = 0;
    private int impactPathSeg;
    
    
    public USCommandAndControl(Identifier id)
    {
        super(id);
    }
    
    class USCommandAndControlFrame{
        private JFrame frame;
        private JButton usccButton; 
        private JTextArea usccText;
        
        public USCommandAndControlFrame(String alert){
            frame = new JFrame("US Command and Control");
            frame.getContentPane().setLayout(new BorderLayout());
            usccButton = new JButton("Reconfigure Flight Path");
            usccText = new JTextArea(alert);
            usccText.setLineWrap(true);
            usccText.setForeground(Color.RED);
            usccButton.addActionListener 
                (new ActionListener() {
                        public void actionPerformed(ActionEvent evt){
                            reconfigureFlightPath();
                        }
                    }
                 );
            frame.getContentPane().add(usccText,BorderLayout.CENTER);
            frame.getContentPane().add(usccButton,BorderLayout.SOUTH);
            frame.setSize(300, 100);
            frame.setLocation(0, 600);
            frame.setVisible(true);                   
        }
        
        private void reconfigureFlightPath(){
            if (flightPathSeg <= 2) {
                usccText.setForeground(Color.BLACK);
                usccText.setText("Flight path reconfigured.");
                usccButton.setEnabled(false);
                coordVector.add(3, new Point(562,304));
                impactPathSeg = 5;
                NamedPropertyMessage pathNPM;   
                pathNPM = new NamedPropertyMessage("operation");
                //pathNPM.addParameter("tick", tick);
                pathNPM.addParameter("type", OperationType.FLIGHTPATH);
                pathNPM.addParameter("operationId", new String("tac1"));
                pathNPM.addParameter("country", Country.USA);
                pathNPM.addParameter("affiliation", Affiliation.FRIEND);
                pathNPM.addParameter("taccoords", coordVector);
                sendNotification(pathNPM); //draws flight path
                sendRequest(pathNPM); // notify entity (radar) of flight path
            }
        }
    }
    

    public void init()
    {
    }

    public void destroy()
    {
    }

    public void begin()
    {        
    }

    public void end()
    {
    }

    public void handle(Message m)
    {
        if (m instanceof NamedPropertyMessage) {
            NamedPropertyMessage npm, received;

            received = (NamedPropertyMessage)m;
            npm = (NamedPropertyMessage)received.duplicate();

            // determine affiliation from country
            Country country = (Country)received.getParameter("country");

            if (country.equals(Country.USA))
                npm.addParameter("affiliation", Affiliation.FRIEND);
            else if (country.equals(Country.MEXICO))
                npm.addParameter("affiliation", Affiliation.NEUTRAL);
            else if (country.equals(Country.FRANCE))
                npm.addParameter("affiliation", Affiliation.FRIEND);
            else if (country.equals(Country.LEBANON))
                npm.addParameter("affiliation", Affiliation.HOSTILE);
            else if (country.equals(Country.UNKNOWN))
                npm.addParameter("affiliation", Affiliation.UNKNOWN);
            else
                throw new RuntimeException("C&C:Unknown country.");

            if ((received.getName()).equals("entity")) { // not applicable for rem-entity

                // determine battle dimension from entity type
                EntityType entity = (EntityType)received.getParameter("type");
                
                if (entity.equals(EntityType.FIXEDWING)) {
                    int tick = received.getIntParameter("tick");        
                    npm.addParameter("dimension", BattleDimension.AIR);
                    
                    flightPathSeg = received.getIntParameter("pathIdx");   
                    if ((flightPathSeg == impactPathSeg)&&(preImpact)) {
                        // remove predicted impact point                    
                        NamedPropertyMessage removeNPM;
                        removeNPM = new NamedPropertyMessage("rem-operation");
                        removeNPM.addParameter("operationId", new String("tac2"));
                        removeNPM.addParameter("affiliation", Affiliation.FRIEND);
                        sendNotification(removeNPM);
                        
                        // add impact point
                        NamedPropertyMessage impactNPM;
                        Vector impactVector;
                        impactNPM = new NamedPropertyMessage("operation");
                        impactNPM.addParameter("tick", tick);
                        impactNPM.addParameter("type", OperationType.IMPACT);
                        impactNPM.addParameter("operationId", new String("tac3"));
                        impactNPM.addParameter("country", Country.USA);
                        impactNPM.addParameter("affiliation", Affiliation.FRIEND);
                        
                        impactVector = new Vector(1);
                        impactVector.add(new Point(550, 350));
                        impactNPM.addParameter("taccoords", impactVector);
                        sendNotification(impactNPM);
                        preImpact = false;
                    }
                }
                else if (entity.equals(EntityType.FIXEDWINGBOMBER))
                    npm.addParameter("dimension", BattleDimension.AIR);
                else if (entity.equals(EntityType.BATTLESHIP)){
                    npm.addParameter("dimension", BattleDimension.SEA);
                }
                else if (entity.equals(EntityType.CARRIER)) {
                    int tick = received.getIntParameter("tick");        
                    npm.addParameter("dimension", BattleDimension.SEA);
                    // if first, create a flight path and impact point
                    if (firstTimeCarrier) {
                        // create flight path
                        NamedPropertyMessage pathNPM;   
                        pathNPM = new NamedPropertyMessage("operation");
                        pathNPM.addParameter("tick", tick);
                        pathNPM.addParameter("type", OperationType.FLIGHTPATH);
                        pathNPM.addParameter("operationId", new String("tac1"));
                        pathNPM.addParameter("country", Country.USA);
                        pathNPM.addParameter("affiliation", Affiliation.FRIEND);
                        int x = (int)received.getDoubleParameter("locX"); 
                        int y = (int)received.getDoubleParameter("locY");
                        coordVector.add(new Point(x, y));
                        coordVector.add(new Point(336, 273));
                        coordVector.add(new Point(473, 258));
                        coordVector.add(new Point(550, 350));
                        coordVector.add(new Point(369, 391));
                        coordVector.add(new Point(x, y));
                        impactPathSeg = 4;
                        pathNPM.addParameter("taccoords", coordVector);
                        sendNotification(pathNPM); //draws flight path
                        sendRequest(pathNPM); // notify entity (radar) of flight path
             
                        // create impact point 
                        NamedPropertyMessage predimpactNPM;
                        Vector predimpactVector;
                        predimpactNPM = new NamedPropertyMessage("operation");
                        predimpactNPM.addParameter("tick", tick);
                        predimpactNPM.addParameter("type", OperationType.PREDIMPACT);
                        predimpactNPM.addParameter("operationId", new String("tac2"));
                        predimpactNPM.addParameter("country", Country.USA);
                        predimpactNPM.addParameter("affiliation", Affiliation.FRIEND);
                        predimpactVector = new Vector(1);
                        predimpactVector.add(new Point(550, 350));
                        predimpactNPM.addParameter("taccoords", predimpactVector);
                        sendNotification(predimpactNPM);
                        firstTimeCarrier = false;
                    }
                } 
                else if (entity.equals(EntityType.TROUPS))
                    npm.addParameter("dimension", BattleDimension.GROUND);
                else if (entity.equals(EntityType.AIRDEFENSEMISSILE)) {
                    npm.addParameter("dimension", BattleDimension.GROUND);
                    // air defense missile detected. reconfigure flight path
                    if (firstTimeAirDefense) {
                        new USCommandAndControlFrame
                            ("WARNING: Air Defense Missile detected between aviation control points 2 and 3.");
                        firstTimeAirDefense = false;
                    }
                }
                else if (entity.equals(EntityType.AWACS))
                    npm.addParameter("dimension", BattleDimension.AIR);
                else
                    throw new RuntimeException("C&C:Unknown entity type.");
            }
            sendNotification(npm);
        }
    }
}
