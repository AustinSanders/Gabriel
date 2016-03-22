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

// This file contains a table of all selector numbers used in c2
// Message objects to invoke services on c2 Component objects.
// Messages are given non-mnemonic names to improve performance.

package c2demo.klax;

public final class KlaxSelectorTable {
	
	// for the clock
	public static final String DbgTick = "DbgTick";
	public static final String Tick = "Tick";
	public static final String MinorTick = "MinorTick";
	public static final String SetClockRate = "SetClockRate";
	public static final String GetClockRate = "GetClockRate";
	public static final String SuspendClock = "SuspendClock";
	public static final String ClockSuspended = "ClockSuspended";
	public static final String ResumeClock = "ResumeClock";
	
	// for the status adt requests
	public static final String IncrementScoreSelector = "IncrementScore";
	public static final String GetNumberOfLivesSelector = "GetNumberOfLives";
	public static final String DecrementNumberOfLivesSelector = "DecrementNumberOfLives";
	// notifications
	public static final String CurrentScoreSelector = "CurrentScore";
	public static final String NumberOfPlayersSelector = "NumberOfPlayers";
	public static final String NumberOfLivesSelector = "NumberOfLives";
	
	// for the well, and chute
	public static final String AddTileSelector = "AddTile";
	public static final String CollapseSelector = "Collapse";
	public static final String RemoveHorizontalSelector = "RemoveHorizontal";
	public static final String RemoveVerticalSelector = "RemoveVertical";
	public static final String RemoveDiagUpSelector = "RemoveDiagUp";
	public static final String RemoveDiagDownSelector = "RemoveDiagDown";
	public static final String AdvanceTilesSelector = "AdvanceTiles";
	public static final String AdvanceWellTilesSelector = "AdvanceWellTiles";
	public static final String AdvanceChuteTilesSelector = "AdvanceChuteTiles";
	public static final String WellFullSelector = "WellFull";
	public static final String TilesRemovedSelector = "TilesRemoved";
	public static final String PlaceTileSelector = "PlaceTile";
	public static final String DropChuteTileSelector = "DropChuteTile";
	public static final String ClearAllChutesSelector = "ClearAllChutes";
	
	// for the palette
	public static final String SetPaletteLocationSelector = "SetPaletteLocation";
	public static final String PaletteLocationSelector = "PaletteLocation";
	public static final String ResetPaletteLocationSelector = "ResetPaletteLocation";
	public static final String MovePaletteSelector = "MovePalette";
	public static final String ResetPaletteSelector = "ResetPalette";
	public static final String AddTileToPaletteSelector = "AddTileToPalette";
	public static final String TileAddedToPaletteSelector = "TileAddedToPalette";
	public static final String DeferTileSelector = "DeferTile";
	public static final String DropPaletteTileSelector = "DropPaletteTile";
	public static final String PaletteTileDroppedSelector = "PaletteTileDropped";
	public static final String InitializePaletteCapacitySelector = "InitializePaletteCapacity";
	public static final String GetPaletteCapacitySelector = "GetPaletteCapacity";
	public static final String SetPaletteCapacitySelector = "SetPaletteCapacity";
	public static final String PaletteCapacitySelector = "PaletteCapacity";
	public static final String MissedTileSelector = "MissedTile";
	public static final String PaletteFullSelector = "PaletteFull";
	public static final String PaletteEmptySelector = "PaletteEmpty";
	public static final String GetPaletteLocationSelector = "GetPaletteLocation";
	
	// for rel pos
	public static final String CatchTileSelector = "CatchTile";
	public static final String LoseALifeSelector = "LoseALife";
	
	// for tile artist
	public static final String GetTileShapeSelector = "GetTileShape";
	public static final String SetTileShapeSelector = "SetTileShape";
	
	// for the graphics component -- these will have to change
	public static final String ViewportCreatedSelector = "ViewportCreated";
	public static final String ViewportDestroyedSelector = "ViewportDestroyed";
	public static final String PanelAddedSelector = "PanelAdded";
	public static final String ObjectCreatedSelector = "ObjectCreated";
	public static final String ObjectModifiedSelector = "ObjectModified";
	public static final String ObjectDestroyedSelector = "ObjectDestroyed";
	
	public static final String AcceptEventSelector = "AcceptEvent";
	
	public static final String StateCastSelector = "StateCast";
}
