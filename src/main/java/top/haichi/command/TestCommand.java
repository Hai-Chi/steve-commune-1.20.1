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
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class TestCommand implements CommandRegistrationCallback {


    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("sc")
                .then(literal("test1")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            EnderChestInventory enderChestInventory = player.getEnderChestInventory();

                            SimpleGui simpleGui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
                            for (int i = 0; i < enderChestInventory.size(); i++) {
                                simpleGui.setSlotRedirect(i,new Slot(enderChestInventory,i,0,0));
                            }
                            simpleGui.setTitle(Text.literal(player.getName().getString() + "的末影箱"));
                            simpleGui.open();


                            return 1;
                        })));
    }
}
