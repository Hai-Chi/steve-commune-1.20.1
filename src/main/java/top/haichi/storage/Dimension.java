package top.haichi.storage;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.HashMap;
import java.util.Map;

import static top.haichi.storage.Dimension.Types.UNKNOWN;

public class Dimension {
    public static Map<RegistryKey<DimensionType>, Types> dimensionMap = Map.of(
            DimensionTypes.OVERWORLD, Types.OVER_WORLD,
            DimensionTypes.THE_END, Types.THE_END,
            DimensionTypes.THE_NETHER, Types.THE_NETHER
    );

    public enum Types {
        OVER_WORLD,
        THE_NETHER,
        THE_END,
        UNKNOWN
    }

    public static Types getType(RegistryKey<DimensionType> dimensionKey) {
        return dimensionMap.getOrDefault(dimensionKey, UNKNOWN);
    }
}