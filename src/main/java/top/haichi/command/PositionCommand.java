package top.haichi.command;

import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import top.haichi.storage.Positions;
import top.haichi.utils.Dimension;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PositionCommand implements CommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("sc")
                .requires(serverCommandSource -> true)
                .then(literal("pos")
                        .then(literal("search")
                                .then(argument("关键词", StringArgumentType.string())
                                        .executes(context -> {
                                            searchPositions(context.getArgument("关键词", String.class),context.getSource());
                                            return 1;
                                        })))
                        .then(literal("here")
                                .then(argument("地名", StringArgumentType.string())
                                        .then(argument("简介", StringArgumentType.string())
                                                .executes(context -> {
                                                    addPosition(context);
                                                    return 1;
                                                })))
                        )
                        .then(literal("list")
                                .executes(PositionCommand::sendPositionMessages))));
    }

    /**
     * 获取所有坐标信息
     *
     * @return 坐标信息
     */
    @Deprecated
    private static String getPositions() {
        Positions positions = Positions.load();
        StringBuilder stringBuilder = new StringBuilder("获取到以下坐标信息：\n");
        positions.positions.forEach(position -> stringBuilder.append(position.toString()));
        return stringBuilder.toString();
    }

    /**
     * 发送带有小地图坐标的所有坐标信息
     *
     * @param context
     */
    private static int sendPositionMessages(CommandContext<ServerCommandSource> context) {
        Positions positions = Positions.load();
        ServerCommandSource source = context.getSource();
        source.sendMessage(Text.literal("获取到以下坐标信息："));
        positions.positions.forEach(position -> {
            source.sendMessage(Text.literal(position.toString()));
            source.sendMessage(Text.literal(generateWaypointSession(position)));
            source.sendMessage(Text.literal(""));
        });
        return 1;
    }

    /**
     * 生成小地图session
     *
     * @param position
     * @return
     */
    private static String generateWaypointSession(Positions.Position position) {
        switch (position.dimension) {
            case Dimension.THE_END -> {
                String pos = position.endPos.replaceAll(",", ":");
                return "xaero-waypoint:小地图:S:" + pos + ":0:false:0:Internal-the-end-waypoints";
            }
            case Dimension.OVER_WORLD -> {
                String pos = position.overworldPos.replaceAll(",", ":");
                return "xaero-waypoint:小地图:S:" + pos + ":0:false:0:Internal-overworld-waypoints";
            }
            case Dimension.THE_NETHER -> {
                String pos = position.netherPos.replaceAll(",", ":");
                return "xaero-waypoint:小地图:S:" + pos + ":0:false:0:Internal-the-nether-waypoints";
            }
            default -> {
                return "";
            }

        }
    }

    /**
     * 根据关键词搜索坐标信息
     */
    private static void searchPositions(String keyWord,ServerCommandSource source) {
        Positions positions = Positions.load();
        positions.positions.stream()
                .filter(position -> position.name.contains(keyWord))
                .forEach(position -> {
                    source.sendMessage(Text.literal(position.toString()));
                    source.sendMessage(Text.literal(generateWaypointSession(position)));
                    source.sendMessage(Text.literal(""));
                });
    }

    /**
     * 在玩家所在位置新建坐标信息
     *
     * @param context 参数信息
     */
    private static void addPosition(CommandContext<ServerCommandSource> context) {
        String name = context.getArgument("地名", String.class);
        String description = context.getArgument("简介", String.class);

        BlockPos blockPos = context.getSource().getPlayer().getBlockPos();
        String pos = String.format("%d,%d,%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());

        RegistryKey<DimensionType> dimensionKey = context.getSource().getWorld().getDimensionKey();
        String dimension;
        Positions.Position position = new Positions.Position();

        if (dimensionKey.equals(DimensionTypes.OVERWORLD)) {
            dimension = Dimension.OVER_WORLD;
            position.overworldPos = pos;
            position.netherPos = String.format("%d,%d", blockPos.getX() / 8, blockPos.getZ() / 8);
        } else if (dimensionKey.equals(DimensionTypes.THE_END)) {
            dimension = Dimension.THE_END;
            position.endPos = pos;
        } else if (dimensionKey.equals(DimensionTypes.THE_NETHER)) {
            dimension = Dimension.THE_NETHER;
            position.netherPos = pos;
        } else dimension = Dimension.UNKNOWN;

        position.dimension = dimension;
        position.name = name;
        position.description = description;

        Positions.load().add(position);
        context.getSource().getPlayer().sendMessage(Text.literal(position.toString() + "\n§f添加成功"));
    }
}
