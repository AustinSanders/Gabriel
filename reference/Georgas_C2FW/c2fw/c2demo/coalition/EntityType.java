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
 * An enumeration implementation of the possible warfighting entities.
 * The set of available enties are limited to those supported in the
 * MAPSYMBS fonts available at:
 * http://ourworld.compuserve.com/homepages/TomMouat/Maphome.htm
 * The entity names are taken from MIL-STD 2525B Appendix A.
 *
 * @author Kari Nies
 */
public final class EntityType implements java.io.Serializable {

    public static final EntityType FIXEDWING =
        new EntityType ("Military Fixed Wing");
    public static final EntityType FIXEDWINGBOMBER =
        new EntityType ("Fixed Wing Bomber");
    public static final EntityType BATTLESHIP =
        new EntityType("Battleship");
    public static final EntityType CARRIER =
        new EntityType ("Aircraft Carrier");
    public static final EntityType TROUPS =
        new EntityType ("Troups");  // get valid name later
    // ... more later

    private String entityName;

    //** ensure no one can call the default constructor
    private EntityType() {};

    //** constructor
    private EntityType (String entityName) {
        this.entityName = entityName;
    }

    public boolean equals(Object o){
        if(!(o instanceof EntityType)){
            return false;
        }
        EntityType other = (EntityType)o;
        return other.entityName.equals(this.entityName);
    }

    public int hashCode(){
        return entityName.hashCode();
    }

    public String toString () {
        return entityName;
    }
}
