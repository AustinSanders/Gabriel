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

import java.util.*;

public class Palette extends Object
{
   Stack PaletteStack = new Stack ();
   int Size_Of_Stack;
   int Tiles_In_Stack;
   int position;
   int leftBound, rightBound;

   public Palette ()
   {
      Size_Of_Stack = 5;
      Tiles_In_Stack = 0;
      leftBound = 0;
      rightBound = 4;
   }


   public String toString ()
   {
      // a temporary stack, which is a clone of the PaletteStack, is
      // used for printing.  The reason is that Stack's toString
      // method doesn't know how to print tiles, so this is done
      // tile-by-tile, first by popping them off the stack and then
      // invoking their toString method to display them.
      Stack tempStack = (Stack)PaletteStack.clone();
      String return_string = "Palette: \n";
      Tile t;

      return_string += "   Size:            " + Size_Of_Stack + "\n";
      return_string += "   Number of Tiles: " + Tiles_In_Stack + "\n";
      return_string += "   Contents: \n";

      while (!tempStack.empty())
      {
         t = (Tile)tempStack.pop();
         return_string += "             " + t.toString() + "\n";
      }

      return return_string;
   }

   public boolean AddTileToPalette(Tile NewTile)
   {
      if (Tiles_In_Stack < Size_Of_Stack)
      {
         PaletteStack.push(NewTile);
         Tiles_In_Stack++;
         return true;
      }
      else
         return false;
   }


   public Tile DropTileIntoWell()
   {
      Tile t = null;

      try {
         t = (Tile)PaletteStack.pop();
      } catch (java.util.EmptyStackException e) {
         System.err.println ("Empty Stack");
         t = null;
      }
      Tiles_In_Stack--;
      return t;
   }


   public int HowManyTiles()
   {
      return Tiles_In_Stack;
   }


   public int GetPaletteCapacity()
   {
      return Size_Of_Stack;
   }


   // SetPaletteCapacity assumes that it will not be called if there
   // are tiles on the palette. If it is called with tiles on the
   // Palette and the new capacity is less than the current number
   // of tiles on the Palette then it is the caller's responsibility
   // to pop the extra tiles off of the palette BEFORE attempting to
   // place any new tiles.
   public void SetPaletteCapacity(int New_Capacity)
   {
      Size_Of_Stack = New_Capacity;
      if (Size_Of_Stack < 0)
         Size_Of_Stack = 0;
   }

   public int MovePalette(int direction)
   {
      if (direction < -1)
         direction = -1;
      if (direction > 1)
         direction = 1;
      position += direction; // use +1 for right, -1 for left

      if (position < leftBound)
         position = leftBound;
      else if (position > rightBound)
         position = rightBound;

      return position;
   }


   public int GetPalettePosition()
   {
      return position;
   }

   public void SetPalettePosition (int new_position)
   {
      if ((new_position >= leftBound) && (new_position <= rightBound))
         position = new_position;
   }


   public void ResetPalette()
   {
      position = leftBound;
      while (!PaletteStack.empty())
          PaletteStack.pop();
      Tiles_In_Stack = 0;
   }
}
