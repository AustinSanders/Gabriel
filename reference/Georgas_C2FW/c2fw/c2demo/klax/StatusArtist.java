/*
 * Copyright (c) 2000 Regents of the University of California.
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

package c2demo.klax;

import c2.fw.*;
import c2.legacy.*;
import java.util.*;


public class StatusArtist extends c2.legacy.AbstractC2Brick
{
   protected KlaxSelectorTable kst;
   protected String status_viewport_name;
   protected int status_viewport_xpos;
   protected int status_viewport_ypos;
   protected int status_viewport_width = 275;
   protected int status_viewport_height = 130;
   protected String status_viewport_fg = "white";
   protected String status_viewport_bg = "darkGray";
   protected String start_button, pause_button, quit_button;
   Hashtable score_label,
             num_lives_label,
             score_text,
             num_lives_text;
   int initial_score;
   int initial_num_lives;
   int current_num_lives;


   // category: constructor
    public StatusArtist( String _name, int xpos, int ypos )
    {
        super( new SimpleIdentifier(_name) );
        create(_name, xpos, ypos);
    }


    public StatusArtist( Identifier id )
    {
        super(id);
        create( id.toString(), 10, 530 );
    }


    private void create(String _name, int xpos, int ypos)
    {
        //super.create(_name, FIFOPort.classType());
        initial_score = 0;
        initial_num_lives = 5;
        current_num_lives = 5;
        status_viewport_xpos = xpos;
        status_viewport_ypos = ypos;
    }


    public void destroy()
    {
    }


    public void init()
    {
    }


    public void begin()
    {
        create_viewport();
        draw_text_labels();
        draw_buttons ();
    }


    public void end()
    {
    }


    // category: c2 message processing
    private void sendDefault( Message m )
    {
        if( Utils.isC2Notification(m) )
        {
            sendToAll( m, bottomIface );
        }
        else if( Utils.isC2Request(m) )
        {
            sendToAll( m, topIface );
        }
    }


    public synchronized void handle( Message m )
    {
        if( Utils.isC2Notification(m) )
        {
            handleNotification( (NamedPropertyMessage)m );
        }
        else
        {
            handleRequest( (NamedPropertyMessage)m );
        }
    }


    public synchronized void handleRequest( NamedPropertyMessage r )
    {
        String label;

        if (r.getName().equalsIgnoreCase("AcceptEvent"))
        {
            String parent_id = (String)r.getParameter("parent_id");
            if (parent_id.equalsIgnoreCase(status_viewport_name))
            {
                label = (String)r.getParameter("button");

                if (label != null) // this is a button event
                {
                    if (label.equalsIgnoreCase(start_button))
                    {
                        // generate a request to start the clock
                        NamedPropertyMessage new_r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.ResumeClock );
                        sendDefault(new_r);
                    }
                    if (label.equalsIgnoreCase(pause_button))
                    {
                        // generate a request to pause the clock
                        NamedPropertyMessage new_r = c2.legacy.Utils.createC2Request( KlaxSelectorTable.SuspendClock );
                        sendDefault(new_r);
                    }
                    if (label.equalsIgnoreCase(quit_button))
                    {
                        // generate a notification to terminate the application
                        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification("ApplicationTerminated");
                        sendDefault(n);
                    }
                }
            }
        }
    }


    public synchronized void handleNotification( NamedPropertyMessage n )
    {
        String message_name = n.getName();

        if (message_name.equals(KlaxSelectorTable.CurrentScoreSelector))
        {
            int score = ((Integer)n.getParameter("score")).intValue();
            update_score(score);
        }
        else if (message_name.equals(KlaxSelectorTable.NumberOfPlayersSelector))
        {
            // do nothing, not displaying number of players
        }
        else if (message_name.equals(KlaxSelectorTable.NumberOfLivesSelector))
        {
            current_num_lives =
             ((Integer)n.getParameter("num_lives")).intValue();
            update_number_of_lives(current_num_lives);
        }
        else if (message_name.equals(KlaxSelectorTable.ClockSuspended) &&
              (current_num_lives <= 0))
            game_over();
    }


    protected void draw_buttons()
    {
        start_button = "Start";
        NamedPropertyMessage n1 = c2.legacy.Utils.createC2Notification("ButtonAdded");
        n1.addParameter ("x", new Integer (0));
        n1.addParameter ("y", new Integer (status_viewport_height - 60));
        n1.addParameter ("width", new Integer (80));
        n1.addParameter ("height", new Integer (50));
        n1.addParameter ("foreground", "white");
        n1.addParameter ("background", "blue");
        n1.addParameter ("label", start_button);
        n1.addParameter ("parent_id", status_viewport_name);
        sendDefault(n1);

        pause_button = "Pause";
        NamedPropertyMessage n2 = c2.legacy.Utils.createC2Notification("ButtonAdded");
        n2.addParameter ("x", new Integer (87));
        n2.addParameter ("y", new Integer (status_viewport_height - 60));
        n2.addParameter ("width", new Integer (80));
        n2.addParameter ("height", new Integer (50));
        n2.addParameter ("foreground", "white");
        n2.addParameter ("background", "blue");
        n2.addParameter ("label", pause_button);
        n2.addParameter ("parent_id", status_viewport_name);
        sendDefault(n2);

        quit_button = "Quit";
        NamedPropertyMessage n3 = c2.legacy.Utils.createC2Notification("ButtonAdded");
        n3.addParameter ("x", new Integer (174));
        n3.addParameter ("y", new Integer (status_viewport_height - 60));
        n3.addParameter ("width", new Integer (80));
        n3.addParameter ("height", new Integer (50));
        n3.addParameter ("foreground", "white");
        n3.addParameter ("background", "blue");
        n3.addParameter ("label", quit_button);
        n3.addParameter ("parent_id", status_viewport_name);
        sendDefault(n3);
    }


    protected Hashtable draw_text (int xpos, int ypos,
                                  String color, String str,
                                  String font, String style,
                                  int size, String text_id)
    {
        // build new instance of a text object

        Hashtable text = new Hashtable();
        text.put("parent_id", status_viewport_name);
        text.put("name", text_id);
        text.put("type", "text");
        text.put("x", new Integer (xpos));
        text.put("y", new Integer (ypos));
        text.put("value", str);
        text.put("font", font);
        text.put("color", color);
        text.put("style", style);
        text.put("size", new Integer (size));

        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectCreatedSelector );
        Enumeration e = text.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            n.addParameter( key, text.get(key) );
        }
        sendDefault(n);

        return text;
    }


    protected void change_text( Hashtable text_obj, String new_text )
    {
        text_obj.put ("value", new_text);
        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ObjectModifiedSelector );
        Enumeration e = text_obj.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            n.addParameter( key, text_obj.get(key) );
        }
        sendDefault(n);
    }

    void update_score (int score)
    {
        Integer integer_value = new Integer (score);
        change_text (score_text, integer_value.toString());
    }


    void update_number_of_lives (int num_lives)
    {
        Integer integer_value = new Integer (num_lives);
        change_text (num_lives_text, integer_value.toString());
    }

    void draw_text_labels()
    {
        Integer integer_value;

        score_label = draw_text (0, 20, "White", "Score: ",
                               "Helvetica", "Bold", 18,
                               "Status_score_label");
        integer_value = new Integer (initial_score);
        score_text = draw_text (75, 20, "White",
                              integer_value.toString(),
                              "Helvetica", "Plain", 18,
                              "Status_score_text");
        num_lives_label = draw_text (0, 50, "White",
                                   "Number of Lives: ",
                                   "Helvetica", "Bold", 18,
                                   "Status_num_lives_label");
        integer_value = new Integer (initial_num_lives);
        num_lives_text = draw_text (185, 50, "White",
                                  integer_value.toString(),
                                  "Helvetica", "Plain", 18,
                                  "Status_num_lives_text");
    }


    void game_over()
    {
        change_text (num_lives_text, "");
        num_lives_label.put ("color", "red");
        change_text (num_lives_label, "Game Over");
    }


    void create_viewport ()
    {
        status_viewport_name = getClass().getName() + "." +
                             getIdentifier().toString() + "." + "vport1";
        NamedPropertyMessage n = c2.legacy.Utils.createC2Notification( KlaxSelectorTable.ViewportCreatedSelector );
        n.addParameter ("x", new Integer(status_viewport_xpos));
        n.addParameter ("y", new Integer (status_viewport_ypos));
        n.addParameter ("width", new Integer (status_viewport_width));
        n.addParameter ("height", new Integer (status_viewport_height));
        n.addParameter ("foreground", status_viewport_fg);
        n.addParameter ("background", status_viewport_bg);
        n.addParameter ("title", status_viewport_name);
        n.addParameter ("id", status_viewport_name);
        sendDefault(n);
    }
}
