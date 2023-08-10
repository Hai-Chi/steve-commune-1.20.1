package top.haichi.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register(new PositionCommand());
        CommandRegistrationCallback.EVENT.register(new TestCommand());
    }
}
