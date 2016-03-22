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
package c2demo.coalition;

/**
 * An enumeration implementation of the known affiliation types.
 * Affiliation refers to the threat posed by the warfighting object
 * being represented.  The basic affiliation categories are unknown,
 * friend, neutral, and hostile as defined by MIL-STD2525B Common
 * Warfighting Symbology.
 *
 * @author Kari Nies
 */
public final class Affiliation implements java.io.Serializable {

    public static final Affiliation UNKNOWN =
        new Affiliation("Unknown");
    public static final Affiliation FRIEND =
        new Affiliation("Friend");
    public static final Affiliation NEUTRAL =
        new Affiliation("Neutral");
    public static final Affiliation HOSTILE =
        new Affiliation("Hostile");

    private String affiliationString;

    //** ensure no one can call the default constructor
    private Affiliation () {};

    //** constructor
    private Affiliation (String affiliationString) {
        this.affiliationString = affiliationString;
    }

    public boolean equals(Object o){
        if(!(o instanceof Affiliation)){
            return false;
        }
        Affiliation other = (Affiliation)o;
        return other.affiliationString.equals(this.affiliationString);
    }

    public int hashCode(){
        return affiliationString.hashCode();
    }

    public String toString() {
        return affiliationString;
    }
}
