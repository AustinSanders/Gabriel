package c2demo.nad;

public interface Constants{

	public static final int ENTITY_TYPE_AIR_FIXEDWINGBOMBER = 1000;
	public static final int ENTITY_TYPE_AIR_FIXEDWING = 1010;
	public static final int ENTITY_TYPE_AIR_ROTORCRAFT = 1020;
	public static final int ENTITY_TYPE_AIR_STEALTH = 1030;
	public static final int ENTITY_TYPE_AIR_SAM = 1040;
	
	public static final int ENTITY_TYPE_LAND_TROUPS = 2000;
	public static final int ENTITY_TYPE_LAND_ARMOR = 2010;
	
	public static final int ENTITY_TYPE_SEA_CARRIER = 3000;
	public static final int ENTITY_TYPE_SEA_BATTLESHIP = 3010;
	public static final int ENTITY_TYPE_SEA_HOVERCRAFT = 3020;
	public static final int ENTITY_TYPE_SEA_MINESWEEPER = 3030;
	public static final int ENTITY_TYPE_SEA_DESTROYER = 3040;

	public static final int FRIENDLY = 100;
	public static final int UNKNOWN = 201;
	public static final int HOSTILE = 302;
	
	public static final int TICK_INTERVAL_IN_MS = 450;
	
	public static final int IMINDEX_AIR_FILL = 0x40;
	public static final int IMINDEX_AIR_ROTORCRAFT = 0x61;
	public static final int IMINDEX_AIR_FIXEDWING = 0x23;
	public static final int IMINDEX_AIR_FIXEDWINGBOMBER = 0x42;
	public static final int IMINDEX_AIR_SAM = 0xc3;
	public static final int IMINDEX_AIR_ASM = 0xc5;
	public static final int IMINDEX_AIR_AAM = 0xc6;
	public static final int IMINDEX_AIR_STEALTH = 0xbd;

	public static final int IMINDEX_LAND_FILL = 0x40;
	public static final int IMINDEX_LAND_ARMOR = 0x41;
	public static final int IMINDEX_LAND_TROUPS = 0x49;
	
	public static final int IMINDEX_SEA_FILL = 0x40;
	public static final int IMINDEX_SEA_BATTLESHIP = 0x42;
	public static final int IMINDEX_SEA_DESTROYER = 0x44;
	public static final int IMINDEX_SEA_AMPHIBIOUS = 0x45;
	public static final int IMINDEX_SEA_CARRIER = 0x22;
	public static final int IMINDEX_SEA_MINESWEEPER = 0x4b;
	public static final int IMINDEX_SEA_HOVERCRAFT = 0x26;
}
