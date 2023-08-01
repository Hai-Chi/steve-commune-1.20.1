package top.haichi.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;

public class Storage {

    public static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    public static Path gamePath = FabricLoader.getInstance().getGameDir();
    public static final File DATA_DIR = new File(gamePath+"/mods/steve_commune");

    private static void init(){
        if(!DATA_DIR.exists() && !DATA_DIR.mkdirs()){
            throw new RuntimeException("无法创建文件夹");
        }
    }

    public static void loadStorages(){
        init();
    }
}
