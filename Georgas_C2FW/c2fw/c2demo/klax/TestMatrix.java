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
import java.io.*;

public class TestMatrix extends Object
{
   static public void main(String argv[])
   {
      Matrix m = new Matrix (3,2);
      System.out.println (m.toString());
      System.out.println ("Matrix Size: " +
                          m.get_matrix_row_size() + " rows and " +
                          m.get_matrix_col_size() + " columns." + "\n");

      m.set_matrix_element (1,0,"2,1");
      m.set_matrix_element (2,1,"3,2");
      System.out.println (m.toString());

      m.set_matrix_element (0,0,"1,1");
      m.set_matrix_element (0,1,"1,2");
      m.set_matrix_element (1,1,"2,2");
      m.set_matrix_element (2,0,"3,1");
      m.set_matrix_element (2,1,"red tile");
      System.out.println (m.toString());

      System.out.println ("0,0 matrix element: " +
                          (m.get_matrix_element(0,0)).toString());
      System.out.println ("0,1 matrix element: " +
                          (m.get_matrix_element(0,1)).toString());
      System.out.println ("1,0 matrix element: " +
                          (m.get_matrix_element(1,0)).toString());
      System.out.println ("1,1 matrix element: " +
                          (m.get_matrix_element(1,1)).toString());
      System.out.println ("2,0 matrix element: " +
                          (m.get_matrix_element(2,0)).toString());
      System.out.println ("2,1 matrix element: " +
                          (m.get_matrix_element(2,1)).toString() + "\n");

      m.remove_matrix_element (1,1);
      System.out.println (m.toString());

      Matrix m2 = (Matrix)m.clone();
      System.out.println (m.toString());
      System.out.println (m2.toString());

      Matrix m3 = m;
      m = new Matrix (3,4);
      System.out.println (m.toString());
      System.out.println (m2.toString());
      System.out.println (m3.toString());
   }
}
