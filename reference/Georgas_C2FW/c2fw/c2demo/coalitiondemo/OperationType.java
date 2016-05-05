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
 * An enumeration implementation of the possible depicted military
 * operations. 
 * The operation names are taken from MIL-STD 2525B Appendix B.
 *
 * @author Kari Nies
 */
public final class OperationType implements java.io.Serializable {

    public static final OperationType FLIGHTPATH = 
        new OperationType ("Flight Path");
    public static final OperationType PREDIMPACT =
        new OperationType("Predicted Impact Point");
    public static final OperationType IMPACT =
        new OperationType("Impact Point");
    public static final OperationType ROM =
        new OperationType ("Refule on the Move");

    
    private String operationName;
    
    //** ensure no one can call the default constructor
    private OperationType() {};
    
    //** constructor
    private OperationType (String operationName) {
        this.operationName = operationName;
    }

    public boolean equals(Object o){
        if(!(o instanceof OperationType)){
            return false;
        }
        OperationType other = (OperationType)o;
        return other.operationName.equals(this.operationName);
    }
    
    public int hashCode(){
        return operationName.hashCode();
    }

    public String toString () {
        return operationName;
    }
}
