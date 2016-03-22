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


/**
 * This component listens for an "action" event and, in
 * response, generates a "map" event containing a url that 
 * can be used to query the Microsoft Terraserver for a 
 * single topological map tile.
 *
 * @author Justin Erenkrantz
 */
public class LocalLandRetrieval extends AbstractC2Brick {

    public LocalLandRetrieval(Identifier id)
    {
        super(id);
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

    private String getResource(double x, double y, double size)
    {
        return "resources/maps/" + (int)x + "-" + (int)y + ".jpg";
    }

    public void handle(Message m)
    {
        //System.out.println("Land Artist Retrieval received a message!");
        if (m instanceof NamedPropertyMessage)
        {
            NamedPropertyMessage npm = (NamedPropertyMessage)m;

            if (npm.getParameter("action") != null)
            {
                double longitude, latitude, size;
                String imagePath;

                longitude = npm.getDoubleParameter("longitude");
                latitude = npm.getDoubleParameter("latitude");
                size = npm.getDoubleParameter("size");

                imagePath = this.getResource(longitude, latitude, size);

                npm = new NamedPropertyMessage("map");
                npm.addParameter("longitude", longitude);
                npm.addParameter("latitude", latitude);
                npm.addParameter("height", 200.0);
                npm.addParameter("width", 200.0);
                npm.addParameter("imageResource", imagePath);

                sendNotification(npm);
            }
            else
                throw new IllegalArgumentException();
        }
    }

}
