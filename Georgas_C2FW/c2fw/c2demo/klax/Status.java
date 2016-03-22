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


public class Status extends Object
{
      static int MIN_SCORE = 0;
      static int MIN_PLAYERS = 0;
      static int MAX_PLAYERS = 1;
      static int MIN_LIVES = 0;
      static int MIN_LEVEL = 1;
      static int MAX_LEVEL = 1;
      static int MIN_DIFF = 1 ;
      static int MAX_DIFF = 1;
      static int INITIAL_SCORE = 0;
      static int INITIAL_PLAYERS = 1;
      static int INITIAL_LIVES = 5;
      static int INITIAL_LEVEL = 1;
      static int INITIAL_DIFF = 1;
      int Score;
      int Number_Of_Players;
      int Number_Of_Lives;
      int Game_Level;
      int Difficulty_Level;

   public Status()
   {
      Score             =   INITIAL_SCORE;
      Number_Of_Players =   INITIAL_PLAYERS;
      Number_Of_Lives   =   INITIAL_LIVES;
      Game_Level        =   INITIAL_LEVEL;
      Difficulty_Level  =   INITIAL_DIFF;
   }

   public int  OK()
   {
      if (Score < MIN_SCORE)
         return -1;
      if ((Number_Of_Players < MIN_PLAYERS) ||
          (Number_Of_Players > MAX_PLAYERS))
         return -2;
      if (Number_Of_Lives < MIN_LIVES)
         return -3;
      if ((Game_Level < MIN_LEVEL) ||
          (Game_Level > MAX_LEVEL))
         return -4;
      if ((Difficulty_Level < MIN_DIFF) ||
          (Difficulty_Level > MAX_DIFF))
         return -5;
      return 1;
   }

   public String toString ()
   {
      String return_string = "Klax_Status_ADT: \n" +
         "   Score             = " + Score + "\n" +
         "   Number of Players = " + Number_Of_Players + "\n" +
         "   Number of Lives   = " + Number_Of_Lives + "\n" +
         "   Game Level        = " + Game_Level + "\n" +
         "   Difficulty Level  = " + Difficulty_Level + "\n";

      return return_string;
   }


   int  GetCurrentScore()
   {
      return Score;
   }


   void IncrementScore(int delta)
   {
      Score += delta;
   }


   void DecrementScore(int delta)
   {
      Score -= delta;
      if (Score < MIN_SCORE)
         Score = MIN_SCORE;
   }


   void ResetScore()
   {
      Score = INITIAL_SCORE;
   }

   int  GetNumberOfPlayers()
   {
      return Number_Of_Players;
   }


   void SetNumberOfPlayers(int specific_number)
   {
      Number_Of_Players = specific_number;
      if (Number_Of_Players > MAX_PLAYERS)
         Number_Of_Players = MAX_PLAYERS;
      if (Number_Of_Players < MIN_PLAYERS)
         Number_Of_Players = MIN_PLAYERS;
   }

   void IncrementNumberOfPlayers()
   {
      if (Number_Of_Players < MAX_PLAYERS)
         Number_Of_Players += 1;
   }


   void DecrementNumberOfPlayers()
   {
      if (Number_Of_Players > MIN_PLAYERS)
         Number_Of_Players -= 1;
   }

   int  GetNumberOfLives()
   {
      return Number_Of_Lives;
   }


   void SetNumberOfLives(int specific_number)
   {
      Number_Of_Lives = specific_number;
      if (Number_Of_Lives < MIN_LIVES)
         Number_Of_Lives = MIN_LIVES;
   }

   void IncrementNumberOfLives()
   {
      Number_Of_Lives += 1;
   }

   void DecrementNumberOfLives()
   {
      if (Number_Of_Lives > MIN_LIVES)
         Number_Of_Lives -= 1;
   }

   void ResetNumberOfLives()
   {
      Number_Of_Lives = INITIAL_LIVES;
   }


   int  GetGameLevel()
   {
      return Game_Level;
   }


   void SetGameLevel(int specific_number)
   {
      Game_Level = specific_number;
      if (Game_Level > MAX_LEVEL)
         Game_Level = MAX_LEVEL;
      if (Game_Level < MIN_LEVEL)
         Game_Level = MIN_LEVEL;
   }


   void IncrementGameLevel()
   {
      if (Game_Level < MAX_LEVEL)
         Game_Level += 1;
   }


   void DecrementGameLevel()
   {
      if (Game_Level > MIN_LEVEL)
         Game_Level -= 1;
   }


   void ResetGameLevel()
   {
      Game_Level = INITIAL_LEVEL;
   }


   int  GetGameDifficulty()
   {
      return Difficulty_Level;
   }


   void SetGameDifficulty(int specific_level)
   {
      Difficulty_Level = specific_level;
      if (Difficulty_Level > MAX_DIFF)
         Difficulty_Level = MAX_DIFF;
      if (Difficulty_Level < MIN_DIFF)
         Difficulty_Level = MIN_DIFF;
   }


   void IncrementGameDifficulty()
   {
      if (Difficulty_Level < MAX_DIFF)
         Difficulty_Level += 1;
   }


   void DecrementGameDifficulty()
   {
      if (Difficulty_Level > MIN_DIFF)
         Difficulty_Level -= 1;
   }


   void ResetGameDifficulty()
   {
      Difficulty_Level = INITIAL_DIFF;
   }
}

