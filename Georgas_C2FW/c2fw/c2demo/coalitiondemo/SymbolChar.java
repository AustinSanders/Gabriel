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

/**
 * An enumeration implementation of the possible symbol characters
 * assuming the use of MAPSYMB fonts.
 *
 * @author Kari Nies
 */
public final class SymbolChar implements java.io.Serializable {

    // the fill char is the background character for a symbol.
    // it provides the color fill and is set to be a consistent
    // character across all MAPSYMB fonts.
    public static final SymbolChar FILLCHAR = 
        new SymbolChar("Fill Char", new Character('\uF040'));

    // entity characters
    public static final SymbolChar ADMCHAR =
        new SymbolChar("ADM Char", new Character('\uF087'));
    public static final SymbolChar AWACSCHAR =
        new SymbolChar("AWACS Char", new Character('\uF057'));
    public static final SymbolChar BATTLESHIPCHAR =
        new SymbolChar("Battleship Char", new Character('\uF021'));
    public static final SymbolChar CARRIERCHAR =
        new SymbolChar("Carrier Char", new Character('\uF022'));
    public static final SymbolChar FIXEDWINGCHAR =
        new SymbolChar("Fixed Wing Char", new Character('\uF023'));
    public static final SymbolChar FIXEDWINGBOMBERCHAR =
        new SymbolChar("Fixed Wing Bomber Char", new Character('\uF042'));
    public static final SymbolChar TROUPSCHAR =
        new SymbolChar("Troups Char", new Character('\uF04D'));


    private String charName;
    private Character ch;
    
    //** ensure no one can call the default constructor
    private SymbolChar() {};
    
    //** constructor
    private SymbolChar(String charName, Character ch) {
        this.charName = charName;
        this.ch = ch;
    }

    public boolean equals(Object o){
        if(!(o instanceof SymbolChar)){
            return false;
        }
        SymbolChar other = (SymbolChar)o;
        return ((other.ch.equals(this.ch)) &&
                (other.charName.equals(this.charName)));
    }
    
    public int hashCode(){
        return charName.hashCode();
    }

    public String toString () {
        return charName;
    }
                       
    public Character getChar () {
        return ch;
    }
                   
}
