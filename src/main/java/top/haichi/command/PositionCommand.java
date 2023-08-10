package top.haichi.command;

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
                                            context.getSource().sendMessage(Text.literal(searchPositions(context.getArgument("关键词", String.class))));
                                            return 1;
                                        })))
                        .then(literal("here")
                                .then(argument("地名", StringArgumentType.string())
                                        .then(argument("简介", StringArgumentType.string())
                                                .executes(context -> {addPosition(context);
                                                    return 1;
                                                })))
                        )
                        .then(literal("list")
                                .executes(context -> {
                                    context.getSource().sendMessage(Text.literal(getPositions()));
                                    return 1;
                                }))));
    }

    /**
     * 获取所有坐标信息
     * @return
     */
    private static String getPositions() {
        Positions positions = Positions.load();
        StringBuilder stringBuilder = new StringBuilder("获取到以下坐标信息：\n");
        for (Positions.Position position : positions.positions) {
            int index = positions.positions.indexOf(position);
            stringBuilder.append(
                    position.toString(index + 1));
        }
        return stringBuilder.toString();
    }

    /**
     * 根据关键词搜索坐标信息
     * @param keyWord
     * @return
     */
    private static String searchPositions(String keyWord) {
        Positions positions = Positions.load();
        StringBuilder stringBuilder = new StringBuilder("搜索到以下坐标信息：\n");
        positions.positions.stream().filter(position -> position.name.contains(keyWord)).forEach(position ->
                stringBuilder.append(position.toString()));
        if(stringBuilder.isEmpty())return "未搜索到相关信息";
        return stringBuilder.toString();
    }

    /**
     * 在玩家所在位置新建坐标信息
     * @param context
     */
    private static void addPosition(CommandContext<ServerCommandSource> context) {
        String name = context.getArgument("地名", String.class);
        String description = context.getArgument("简介", String.class);
        //获取坐标
        BlockPos blockPos = context.getSource().getPlayer().getBlockPos();
        String pos = blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
        //维度
        RegistryKey<DimensionType> dimensionKey = context.getSource().getWorld().getDimensionKey();
        String dimension;

        Positions.Position position = new Positions.Position();

        if (dimensionKey.equals(DimensionTypes.OVERWORLD)) {
            dimension = "主世界";
            position.mainPos = pos;
            position.netherPos = blockPos.getX() / 8 + "," + blockPos.getZ() / 8;
        } else if (dimensionKey.equals(DimensionTypes.THE_END)) {
            dimension = "末地";
            position.endPos = pos;
        } else if (dimensionKey.equals(DimensionTypes.THE_NETHER)) {
            dimension = "地狱";
            position.netherPos = pos;
        } else dimension = "未知";
        position.dimension = dimension;
        position.name = name;
        position.description = description;
        Positions.load().add(position);
        context.getSource().getPlayer().sendMessage(Text.literal(position.toString() + "§f添加成功"));

    }

}
