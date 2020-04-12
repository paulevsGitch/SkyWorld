package paulevs.skyworld.structures;

import java.util.Locale;

import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.registry.Registry;
import paulevs.skyworld.structures.piece.FlatSphereIslandPiece;

public class StructureTypes
{
	public static final StructurePieceType FLAT_SPHERE_ISLAND = register(FlatSphereIslandPiece::new, "flat_sphere_island");
	
	public static void init()
	{
		
	}
	
	protected static StructurePieceType register(StructurePieceType pieceType, String id)
	{
		return (StructurePieceType)Registry.register(Registry.STRUCTURE_PIECE, (String)id.toLowerCase(Locale.ROOT), pieceType);
	}
}
