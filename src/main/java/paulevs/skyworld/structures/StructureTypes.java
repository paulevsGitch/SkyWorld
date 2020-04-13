package paulevs.skyworld.structures;

import java.util.Locale;

import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.registry.Registry;
import paulevs.skyworld.structures.piece.IslandPiece;

public class StructureTypes
{
	public static final StructurePieceType SKY_ISLAND = register(IslandPiece::new, "sky_island");
	
	public static void init()
	{
		
	}
	
	protected static StructurePieceType register(StructurePieceType pieceType, String id)
	{
		return (StructurePieceType)Registry.register(Registry.STRUCTURE_PIECE, (String)id.toLowerCase(Locale.ROOT), pieceType);
	}
}
