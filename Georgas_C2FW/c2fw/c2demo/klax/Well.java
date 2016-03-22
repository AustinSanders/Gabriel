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

public class Well extends Object {

   Matrix well_matrix;
   int num_rows;
   int num_cols;

   public Well(int rows, int cols)
   {
      well_matrix = new Matrix (rows, cols);
      num_rows = rows;
      num_cols = cols;

      // initialize well to all spaces
      for (int i = 0; i < num_rows; ++i) {
         for (int j = 0; j < num_cols; ++j) {
            well_matrix.set_matrix_element(i, j, new Tile(Tile.SpaceTile));
         }
      }
   }


   public Matrix get_well ()
   {
      return well_matrix;
   }

   public boolean add_tile(int location, Tile new_tile)
   {
      // check for full well
      if (is_space_at_loc(0, location)) {
         // insert new tile at top of its column
         well_matrix.set_matrix_element(0, location, new_tile);
         return true;
      } else {
         return false;
      }
   }


   public int advance_tiles()
   {
      int num_moved = 0;

      // bottom row cannot advance
      for (int i = num_rows - 2; i >= 0; i--)
         for (int j = 0; j < num_cols; j++)
            // only bother advancing non-space tiles
            if (!is_space_at_loc(i, j) && is_space_at_loc(i+1, j))
            {
               swap_tiles(i+1, j, i, j);
               num_moved++;
            }

      return num_moved;
   }


   // removes to the right
   public void remove_horizontal (int row, int col, int num_tiles)
   {
      for (int i = 0; i < num_tiles; i++)
         well_matrix.set_matrix_element(row, col+i, new Tile(Tile.EmptyTile));
   }

   // removes downward
   public void remove_vertical (int row, int col, int num_tiles)
   {
      for (int i = 0; i < num_tiles; i++)
         well_matrix.set_matrix_element(row+i, col, new Tile(Tile.EmptyTile));
   }

   // removes up and to the right
   public void remove_diagonal_up (int row, int col, int num_tiles)
   {
      for (int i = 0; i < num_tiles; i++)
         well_matrix.set_matrix_element(row-i, col+i, new Tile(Tile.EmptyTile));
   }

   // removes down and to the right
   public void remove_diagonal_down (int row, int col, int num_tiles)
   {
      for (int i = 0; i < num_tiles; i++)
         well_matrix.set_matrix_element(row+i, col+i, new Tile(Tile.EmptyTile));
   }


   public void collapse_tiles()
   {
      int drop_rows;

      // scans left-to-right and bottom-to-top
      for (int c = 0; c < num_cols; c++) {
         drop_rows = 0;
         for (int r = num_rows-1; r >= 0; r--) {
            if (is_deleted_at_loc(r, c)) {
               well_matrix.set_matrix_element (r, c, new Tile(Tile.SpaceTile));
               drop_rows++; // removed tiles seen so far in this col
            } else {
               // does tile need to drop?
               if (drop_rows > 0)
                  swap_tiles(r, c, r+drop_rows, c);
            }
         }
      }
   }


   public Tile get_tile(int row, int col)
   {
      return (Tile)well_matrix.get_matrix_element(row, col);
   }



   public String toString ()
   {
      return well_matrix.toString();
   }


   //===================//
   // protected methods //
   //===================//

   protected void swap_tiles(int row1, int col1, int row2, int col2)
   {
      Tile tmp = (Tile)well_matrix.get_matrix_element(row1, col1);
      well_matrix.set_matrix_element(row1, col1,
         well_matrix.get_matrix_element(row2, col2));
      well_matrix.set_matrix_element(row2, col2, tmp);
   }


   protected boolean is_space_at_loc(int row, int col)
   {
      Tile tmp = (Tile)well_matrix.get_matrix_element(row, col);
      return (tmp.isSpace());
   }


   protected boolean is_deleted_at_loc(int row, int col)
   {
      Tile tmp = (Tile)well_matrix.get_matrix_element(row, col);
      return (tmp.isEmpty());
   }
}
