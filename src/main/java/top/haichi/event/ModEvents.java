package top.haichi.event;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import top.haichi.event.listeners.UseItemListener;
import top.haichi.event.listeners.AttackEntityListener;

public class ModEvents {

    public static void registerEvents(){
        UseItemCallback.EVENT.register(new UseItemListener());
        AttackEntityCallback.EVENT.register(new AttackEntityListener());
    }
}
