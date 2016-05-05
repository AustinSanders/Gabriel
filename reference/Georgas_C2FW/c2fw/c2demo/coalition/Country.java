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
 * An enumeration implementation of the possible countries or origin
 * of the warfighting objects being represented.
 *
 * @author Kari Nies
 */
public final class Country implements java.io.Serializable {

    public static final Country UNKNOWN =
        new Country ("Unknown");
    public static final Country USA =
        new Country ("United States of America");
    public static final Country FRANCE =
        new Country ("France");
    public static final Country MEXICO =
        new Country ("Mexico");
    // ... more later

    private String countryName;

    //** ensure no one can call the default constructor
    private Country () {};

    //** constructor
    private Country (String countryName) {
        this.countryName = countryName;
    }

    public boolean equals(Object o){
        if(!(o instanceof Country)){
            return false;
        }
        Country other = (Country)o;
        return other.countryName.equals(this.countryName);
    }

    public int hashCode(){
        return countryName.hashCode();
    }

    public String toString () {
        return countryName;
    }
}
