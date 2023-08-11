package top.haichi.command;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class TestCommand implements CommandRegistrationCallback {


    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("sc")
                        .then(literal("test1").executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    EnderChestInventory enderChestInventory = player.getEnderChestInventory();

                                    SimpleGui simpleGui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
                                    for (int i = 0; i < enderChestInventory.size(); i++) {
                                        simpleGui.setSlotRedirect(i, new Slot(enderChestInventory, i, 0, 0));
                                    }
                                    simpleGui.setTitle(Text.literal(player.getName().getString() + "的末影箱"));
                                    simpleGui.open();


                                    return 1;
                                })
                        ).then(literal("test2").executes(context -> {
                                    MutableText click = Text.literal("asdf")
                                            .setStyle(Style.EMPTY.withClickEvent(
                                                            new ClickEvent(ClickEvent.Action.OPEN_URL, "http://example.com")
                                                    )
                                            );
                                    Objects.requireNonNull(context.getSource().getPlayer()).sendMessage(click);
//[style={color=gray,clickEvent=ClickEvent{action=RUN_COMMAND, value='/xaero_waypoint_add:Shared Location:S:-9:101:-4:1:false:0:Internal_overworld_waypoints'},hoverEvent=HoverEvent{action=<action show_text>, value='literal{-9, 101, -4}'}}]
                                    Objects.requireNonNull(context.getSource().getPlayer()).sendMessage(
                                            Text.literal("test").setStyle(
                                                    Style.EMPTY.withClickEvent(
                                                            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/xaero_waypoint_add:Shared Location:S:-9:101:-4:1:false:0:Internal_overworld_waypoints")
                                                    ).withHoverEvent(
                                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("add"))
                                                    )
                                            )
                                    );
                                    return 1;
                                })
                        )
        );
    }
}
