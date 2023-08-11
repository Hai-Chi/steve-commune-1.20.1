package top.haichi.storage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import top.haichi.utils.Dimension;

public class Positions {
    public final ArrayList<Position> positions = new ArrayList<>();
    private static final File POSITION_FILE = new File(Storage.DATA_DIR + "/positions.json");

    public static Positions load() {
        if (!POSITION_FILE.exists()) {
            Positions instance = new Positions();
            instance.save();
            return instance;
        } else {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(POSITION_FILE), StandardCharsets.UTF_8))) {
                return Storage.GSON.fromJson(reader, Positions.class);
            } catch (Exception e) {
                throw new RuntimeException("无法加载positions.json文件");
            }
        }
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POSITION_FILE,StandardCharsets.UTF_8))) {
            Storage.GSON.toJson(this, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一个新坐标
     * @param position
     */
    public void add(Position position) {
        this.positions.add(position);
        save();
    }

    public void remove(String name) {
        this.positions.removeIf(position -> position.name.equals(name));
        save();
    }

    public static class Position {
        public String dimension;
        public String overworldPos;
        public String netherPos;
        public String name;
        public String endPos;
        public String description;

        @Override
        public String toString() {
            return toString(-1);
        }

        public String toString(int index) {
            StringBuilder builder = new StringBuilder();

            if (index != -1) builder.append("§f").append(index).append(". ");

            switch (dimension) {
                case Dimension.THE_END -> builder.append("§b").append(name).append("-------------------")
                        .append("\n 维度：").append(dimension)
                        .append("\n 末地坐标：").append(endPos)
                        .append("\n 简介：").append(description);
                case Dimension.OVER_WORLD -> builder.append("§6").append(name).append("-------------------")
                        .append("\n 维度：").append(dimension)
                        .append("\n 主世界坐标：").append(overworldPos)
                        .append("\n 地狱坐标：").append(netherPos)
                        .append("\n 简介：").append(description);
                case Dimension.THE_NETHER -> builder.append("§4").append(name).append("-------------------")
                        .append("\n 维度：").append(dimension)
                        .append("\n 地狱坐标：").append(netherPos)
                        .append("\n 简介：").append(description);
                default -> builder.append("§f").append(name).append("-------------------")
                        .append("\n 维度：").append(dimension)
                        .append("\n 主世界坐标：").append(overworldPos)
                        .append("\n 地狱坐标：").append(netherPos)
                        .append("\n 末地坐标：").append(endPos)
                        .append("\n 简介：").append(description);
            }
            return builder.toString();
        }
    }
}

