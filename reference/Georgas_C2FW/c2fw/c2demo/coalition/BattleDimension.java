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
 * An enumeration implementation of the possible battle dimensions.
 * on which a warfighting object can reside. The dimensions do not
 * directly correspond with those found in MIL-STD 2525B and are
 * instead choosed to map to the MAPSYMBS fonts.
 *
 * @author Kari Nies
 */
public final class BattleDimension implements java.io.Serializable {

    public static final BattleDimension AIR =
        new BattleDimension("Air");
    public static final BattleDimension GROUND =
        new BattleDimension("Ground");
    public static final BattleDimension SEA =
        new BattleDimension("Sea");
    public static final BattleDimension SUBSURFACE =
        new BattleDimension("Subsurface");

    private String dimensionString;

    //** ensure no one can call the default constructor
    private BattleDimension () {};

    //** constructor
    private BattleDimension (String dimensionString) {
        this.dimensionString = dimensionString;
    }

    public boolean equals(Object o){
        if(!(o instanceof BattleDimension)){
            return false;
        }
        BattleDimension other = (BattleDimension)o;
        return other.dimensionString.equals(this.dimensionString);
    }

    public int hashCode(){
        return dimensionString.hashCode();
    }

    public String toString() {
        return dimensionString;
    }
}
