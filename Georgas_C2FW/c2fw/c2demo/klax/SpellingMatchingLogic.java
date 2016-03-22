/*
 * Copyright (c) 2000 Regents of the University of California.
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

package c2demo.klax;

import c2.fw.*;
import c2.legacy.*;
import java.io.*;
import java.util.*;


public class SpellingMatchingLogic extends MatchingLogic
{
   protected Hashtable dictionary = null;

   // category: constructor
   public SpellingMatchingLogic(String _name)
   {
       super(_name);
      create(_name);
   }

    public SpellingMatchingLogic( Identifier id )
    {
        super(id);
        create( id.toString() );
    }

   public void create(String _name)
   {
      buildDictionary();
   }
   public void create(String name, Class portClass) {
     create(name);
   }

   public int longestMatchingSequence(int row, int col, int dir)
   {
      int len = 0;
      int curRow = row;
      int curCol = col;
      int deltarow = deltaRowTable[dir];
      int deltacol = deltaColTable[dir];
      Tile curTile = null;
      String buffer = "";


      Tile tile = (Tile)tiles.get_matrix_element(row, col);

      if (tile.isSpace() || tile.isEmpty())
         return 0;

      while (curRow >= 0 && curCol >= 0 &&
             curRow < nrows && curCol < ncols)
      {
         curTile = ((Tile)tiles.get_matrix_element(curRow, curCol));
         if (curTile.isSpace() || curTile.isEmpty())
            return len;
         buffer += curTile.toString();

         if (findWord(buffer))
            len = buffer.length();

         curRow += deltarow;
         curCol += deltacol;

      }
      return len;
   }


   protected boolean findWord (String word)
   {
      if (dictionary.containsKey(word))
         return true;
      else
         return false;
   }

   protected void buildDictionary()
   {
      dictionary = new Hashtable();
      InputStream in;
      StreamTokenizer st;

      //try
      //{
         //in = new FileInputStream("dict.txt");
      	in = ClassLoader.getSystemResourceAsStream("c2demo/klax/dict.txt");
      	InputStreamReader isr = new InputStreamReader(in);
         st = new StreamTokenizer (isr);

         try
         {
            st.nextToken();
            while (st.ttype != StreamTokenizer.TT_EOF)
            {
               if (st.sval != null)
                  dictionary.put(st.sval.toUpperCase(),"");
               st.nextToken();
            }
            isr.close();
         }
         catch (java.io.IOException e)
         {
            System.err.println(e.toString());
         }
      //}
      //catch (java.io.FileNotFoundException e)
      //{
      //   System.err.println(e.toString());
      //}
   }
}
