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

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.InputStream;


/**
 * An enumeration implementation of the possible graphical symbol fonts
 * based on entity affiliation and battle dimension.  A single
 * separate font is used for operation symbols.
 *
 * NATO APP-6a military map marking symbols available at:
 * http://www.mapsymbs.com/app-6a.html
 *
 * @author Kari Nies
 */
public final class SymbolFont implements java.io.Serializable {

    /* Package location used for locating the TrueType files.
     * If it has /, then it'll look at a root.
     * Otherwise, it'll prepend this package name.
     */
    private static final String FONT_PREFIX = "resources/fonts/";

    // friend affiliation fonts
    public static final SymbolFont FRIENDAIRFONT =
        new SymbolFont ("Friend Air Font", "MapSym-FR-Air-APP6a",
                        FONT_PREFIX + "APP6A08.TTF");
    public static final SymbolFont FRIENDLANDFONT =
        new SymbolFont ("Friend Land Font", "MapSym-FR-Land-APP6a",
                        FONT_PREFIX + "APP6A04.TTF");
    public static final SymbolFont FRIENDSEAFONT =
        new SymbolFont ("Friend Sea Font", "MapSym-FR-Sea-APP6a",
                        FONT_PREFIX + "APP6A12.TTF");

    // hostile affiliation fonts
    public static final SymbolFont HOSTILEAIRFONT =
        new SymbolFont ("Hostile Air Font", "MapSym-EN-Air-APP6a",
                        FONT_PREFIX + "APP6A05.TTF");
    public static final SymbolFont HOSTILELANDFONT =
        new SymbolFont ("Hostile Air Font", "MapSym-EN-Land-APP6a",
                        FONT_PREFIX + "APP6A01.TTF");
    public static final SymbolFont HOSTILESEAFONT =
        new SymbolFont ("Hostile Air Font", "MapSym-EN-Sea-APP6a",
                        FONT_PREFIX + "APP6A09.TTF");

    // neutral affiliation fonts
    public static final SymbolFont NEUTRALAIRFONT =
        new SymbolFont ("Neutral Air Font", "MapSym-NU-Air-APP6a",
                        FONT_PREFIX + "APP6A07.TTF");
    public static final SymbolFont NEUTRALLANDFONT =
        new SymbolFont ("Neutral Air Font", "MapSym-NU-Land-APP6a",
                        FONT_PREFIX + "APP6A03.TTF");
    public static final SymbolFont NEUTRALSEAFONT =
        new SymbolFont ("Neutral Air Font", "MapSym-NU-Sea-APP6a",
                        FONT_PREFIX + "APP6A11.TTF");

    // unknown affiliation fonts
    public static final SymbolFont UNKNOWNAIRFONT =
        new SymbolFont ("Unknown Air Font", "MapSym-NK-Air-APP6a",
                        FONT_PREFIX + "APP6A06.TTF");
    public static final SymbolFont UNKNOWNLANDFONT =
        new SymbolFont ("Unknown Air Font", "MapSym-NK-Land-APP6a",
                        FONT_PREFIX + "APP6A02.TTF");
    public static final SymbolFont UNKNOWNSEAFONT =
        new SymbolFont ("Unknown Air Font", "MapSym-NK-Sea-APP6a",
                        FONT_PREFIX + "APP6A10.TTF");

    // operation font
    public static final SymbolFont OPERATIONFONT =
        new SymbolFont ("Operation Font", "demoFont",
                        FONT_PREFIX + "DemoFont.ttf");

    /* Additional fonts in the APP6a package:
     * fonts/APP6A14.TTF MapSymNAOOTW APP6a
     * fonts/APP6A13.TTF MapSymNAEqpt APP6a
     * fonts/APP6A15.TTF MapSymENLand1
     * fonts/APP6A16.TTF MapSymNKLand1
     * fonts/APP6A17.TTF MapSymNULand1
     * fonts/APP6A18.TTF MapSymFRLand1
     */

    private String fontDesc;
    private String fontName;
    private String fontFile;

    //** ensure no one can call the default constructor
    private SymbolFont() {};

    //** constructor
    private SymbolFont (String fontDesc, String fontName, String fontFile) {
        this.fontDesc = fontDesc;
        this.fontName = fontName;
        this.fontFile = fontFile;
    }

    public boolean equals(Object o){
        if(!(o instanceof SymbolFont)){
            return false;
        }
        SymbolFont other = (SymbolFont)o;
        return ((other.fontDesc.equals(this.fontDesc)) &&
                (other.fontName.equals(this.fontName)));
    }

    public int hashCode(){
        return fontName.hashCode();
    }

    public String toString () {
        return fontDesc + " " + fontName + " " + fontFile;
    }

    public String getFontName() {
        return fontName;
    }

    public String getFontFile() {
        return fontFile;
    }

    public Font getFont(int fontSize)
    {
        Font font = null;

        InputStream in =
            VirtualScreen.class.getResourceAsStream(getFontFile());

        /* Technique #2 to finding the resource */
        if (in == null) {
            in = VirtualScreen.class.getClassLoader().
                getSystemResourceAsStream(getFontFile());
        }

        if (in != null) {
            /* Aha!  We found the font file! */
            try {
                Font origFont = Font.createFont(Font.TRUETYPE_FONT, in);
                /* Java orignates the font at 1pt.  Grow to our size.
                 *
                 * This cast is required since Font constructor takes an
                 * integer.  deriveFont(int) is for style NOT size.
                 */
                font = origFont.deriveFont((float)fontSize);
                in.close();
            }
            catch (java.awt.FontFormatException ffe) {
                ffe.printStackTrace();
            }
            catch (java.io.IOException ie) {
                ie.printStackTrace();
            }
        }

        /* If we can't find the file ourselves, fail to the system. */
        if (font == null) {
            System.out.println("Couldn't find: " + toString());
            font = new Font(getFontName(), Font.PLAIN, fontSize);
        }

        return font;
    }
}
