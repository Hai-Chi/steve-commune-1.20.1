package top.haichi.command;

import java.util.Objects;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import top.haichi.storage.Positions;
import top.haichi.storage.Dimension;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PositionCommand implements CommandRegistrationCallback {

    public static Positions positions = new Positions();

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("sc")
                .requires(serverCommandSource -> true)
                .then(literal("pos")
                        .then(literal("search")
                                .then(argument("关键词", StringArgumentType.string())
                                        .executes(context -> {
                                            searchPositions(context.getArgument("关键词", String.class), context.getSource());
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
     * 发送带有小地图坐标的所有坐标信息
     *
     * @param context
     */
    private static int sendPositionMessages(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        removeBooks(Objects.requireNonNull(source.getPlayer()));
        positions.showList(source);
        return 1;
    }

    /**
     * 根据关键词搜索坐标信息
     */
    private static void searchPositions(String keyWord, ServerCommandSource source) {
        positions.Search(keyWord).forEach(position -> {
            position.show(source);
        });
    }

    /**
     * 在玩家所在位置新建坐标信息
     *
     * @param context 参数信息
     */
    private static void addPosition(CommandContext<ServerCommandSource> context) {
        Dimension.Types dimension = Dimension.getType(context.getSource().getWorld().getDimensionKey());
        String name = context.getArgument("地名", String.class);
        String description = context.getArgument("简介", String.class);
        BlockPos blockPos = Objects.requireNonNull(context.getSource().getPlayer(), "无法获取玩家信息").getBlockPos();

        Positions.Position position = new Positions.Position(dimension, name, description, blockPos);

        ServerCommandSource source = context.getSource();
        positions.add(position);
        position.show(source);
        source.sendMessage(Text.literal("添加成功"));
    }

    public static void removeBooks(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack item = player.getInventory().getStack(i);
            if (item.getItem() == Items.WRITTEN_BOOK) {
                NbtCompound tag = item.getNbt();
                if (tag != null) {
                    String title = tag.getString("title");
                    String author = tag.getString("author");
                    if ("地址の书" .equals(title) && "CharlesHsu & HaiChi" .equals(author)) {
                        player.getInventory().setStack(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
