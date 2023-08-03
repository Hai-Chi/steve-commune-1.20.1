package top.haichi;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.haichi.command.ModCommands;
import top.haichi.event.ModEvents;
import top.haichi.storage.Positions;
import top.haichi.storage.Storage;

import java.nio.file.Path;

public class SteveCommune implements ModInitializer {
	public static final String MOD_ID = "stevecommune";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



	@Override
	public void onInitialize() {
		LOGGER.info("初始化steve commune");
		//加载文件
		Storage.loadStorages();
		//注册事件
		ModEvents.registerEvents();
		//注册命令
		ModCommands.registerCommands();

	}
}