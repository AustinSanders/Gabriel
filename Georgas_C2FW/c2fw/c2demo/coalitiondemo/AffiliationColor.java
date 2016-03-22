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
import java.awt.Color;

/**
 * An enumeration implementation of the possible symbol colors
 * based on entity affiliation.
 * The affiliation colors are taken from MIL-STD 2525B.
 *
 * @author Kari Nies
 */
public final class AffiliationColor implements java.io.Serializable {

    public static final AffiliationColor FRIENDCOLOR = 
        new AffiliationColor ("Friend Color", new Color(128, 224, 255));

    public static final AffiliationColor HOSTILECOLOR = 
        new AffiliationColor ("Hostile Color", new Color(255, 128, 128));

    public static final AffiliationColor NEUTRALCOLOR = 
        new AffiliationColor ("Neutral Color", new Color(170, 225, 170));

    public static final AffiliationColor UNKNOWNCOLOR = 
        new AffiliationColor ("Unknown Color", new Color(255, 255, 128));
  
    
    private String colorName;
    private Color  color;
    
    //** ensure no one can call the default constructor
    private AffiliationColor() {};
    
    //** constructor
    private AffiliationColor (String colorName, Color color) {
        this.colorName = colorName;
        this.color = color;
    }

    public boolean equals(Object o){
        if(!(o instanceof AffiliationColor)){
            return false;
        }
        AffiliationColor other = (AffiliationColor)o;
        return ((other.color.equals(this.color)) &&
                (other.colorName.equals(this.colorName)));
    }
    
    public int hashCode(){
        return colorName.hashCode();
    }

    public String toString () {
        return colorName;
    }

    public Color getColor() {
        return color;
    }
}
