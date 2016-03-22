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

import java.lang.*;
import java.util.*;
import java.io.*;

public class Matrix extends Object implements java.io.Serializable
{

   protected int num_rows, num_cols;
   protected Object matrix[];


   //-----------------------------------------------------------
   //  maps a 2 dimensional array onto a one dimensional array
   //-----------------------------------------------------------
   protected int calc_index (int row, int col)
   {
      return (row*num_cols + col);
   }

   //-----------------------------------------------------------
   //  returns the minimum of two integers
   //-----------------------------------------------------------
   int min (int i1, int i2)
   {
      return (i1 < i2) ? i1 : i2;
   }


   // constructor
   public Matrix () {}
   public Matrix (int rows, int cols)
   {
      num_rows = rows;
      num_cols = cols;
      // allocate matrix and initialize values
      matrix = new Object [num_rows*num_cols];
      for (int i = 0; i < num_rows*num_cols; ++i)
         matrix[i] = null;
   }

   public Object clone ()
   {
      Matrix clomat = new Matrix (num_rows, num_cols);
      try
      {
         // artificial throw for compatibility to java/1.0.2
         if (false) throw new CloneNotSupportedException();

         clomat.matrix = (Object[])matrix.clone();
      }
      catch (java.lang.CloneNotSupportedException e)
      {
         System.err.println ("Cannot clone an array");
      }

      return clomat;
   }

   public Object get_matrix_element (int row, int col)
   {
      return matrix[calc_index(row,col)];
   }


   public void remove_matrix_element (int row, int col)
   {
      matrix[calc_index(row,col)] = null;
   }


   public void set_matrix_element(int row, int col, Object element)
   {
      matrix[calc_index(row,col)] = element;
   }


   public void reset_matrix ()
   {
      for (int i = 0; i < num_rows*num_cols; ++i)
         matrix[i] = null;
   }


   public int get_matrix_row_size ()
   {
      return num_rows;
   }


   public int get_matrix_col_size ()
   {
      return num_cols;
   }


   public void set_matrix_size (int rows, int cols)
   {
      Object new_matrix[] = new Object [rows*cols];

      for (int i = 0; i < rows*cols; ++i)
	 new_matrix[i] = null;

      for (int i = 0;  i < min(rows,num_rows); ++i) {
         for (int j = 0;  j < min(cols,num_cols); ++j) {
            new_matrix[i*cols+j] = get_matrix_element(i,j);
	 }
      }

      matrix = new_matrix;
      num_rows = rows;
      num_cols = cols;
   }


   public String toString ()
   {
      String return_string;

      return_string = "Matrix: \n";
      for (int i = 0; i < num_rows; ++i) {
         for (int j = 0; j < num_cols; ++j) {
            if (matrix[calc_index(i,j)] == null)
               return_string += "null   ";
            else
               return_string = return_string +
                               matrix[calc_index(i,j)].toString() +
                               "  ";
         }
         return_string += "\n";
      }

      return return_string;
   }

}

