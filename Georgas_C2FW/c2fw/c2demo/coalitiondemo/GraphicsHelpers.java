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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Font;
import java.awt.FontMetrics;

public class GraphicsHelpers{

    /**
     * Given to points on a line, returns the angle of the line
     */
    public static int calcHeading (double fromX,
                                   double fromY,
                                   double toX,
                                   double toY) {
        double angle = Math.toDegrees(Math.atan2(-(toY-fromY), toX-fromX));
        return (int)angle;
    } 
    
    /**
     * Returns the x,y coordinates of the end point of line starting
     * at startx, starty in the direction of the given heading and of 
     * the given length.
     */
    public static Point getHeadingEndPoint (int startx, int starty, 
                                            int heading, int length) {
        int x, y;
        x = startx + (int)(Math.cos(Math.toRadians(heading))* length);   
        y = starty - (int)(Math.sin(Math.toRadians(heading))* length);
        return new Point(x,y);
    }
    
    
    /**
     * Given a character and its font, calculate the center point offset
     * for that character.  In otherwords, if the character were placed
     * at point (0,0), the result would be the center point of the
     * character.
     */
    public static Point getCenterOffset (Graphics g, Font f, char ch) {
        FontMetrics fm = g.getFontMetrics(f);
        int xOffset = (int)(fm.charWidth(ch)/2);
        int yOffset = (int)(fm.getAscent()/2);
        return new Point(xOffset, yOffset);
    }

    /**
     * Given a line formed by points Point1 and Point2, returns
     * the point at a given distance from the Point1 along the
     * same line.
     */
    public static Point calcPointOnLineAtDist (Point Point1, 
                                               Point Point2,
                                               int Dist) {
        double A = Point2.getX()-Point1.getX();
        double B = -(Point2.getY()-Point1.getY()); //negate for graphic coords
        if (A == 0) {
            return new Point(Point1.x, Point2.y+Dist);
        }
        double angle = Math.atan(B/A);
        double a = Dist * Math.cos(angle);
        double b = Dist * Math.sin(angle);
        if (A > 0) {
            return new Point((int)(Point1.x+a), (int)(Point1.y-b));
        } else {
            return new Point((int)(Point1.x-a), (int)(Point1.y+b));
        }
    }

    /**
     * Calculates a square root
     */
    public static double sq(double val) {
        return val * val;
    }


    /**
     * Given a line formed by points Point1 and Point2, returns
     * the length of the line.
     */
    public static double lineLen (Point Point1, Point Point2) {
        return Math.sqrt(sq((Point2.getX()-Point1.getX())) +
                         sq((Point2.getY()-Point1.getY())));
                       
    }

    public static double lineLen (double x1, double y1,
                                  double x2, double y2) {
        return Math.sqrt(sq(x2-x1)+sq(y2-y1));
                       
    }

   
}
