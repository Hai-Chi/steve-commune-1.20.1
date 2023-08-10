package top.haichi.storage;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Positions {
    public ArrayList<Position> positions = new ArrayList<>();
    public static final File positionFile = new File(Storage.DATA_DIR + "/positions.json");

    /**
     * 从文件中读取
     */
    public static Positions load() {
        Positions instance;
        if (!positionFile.exists()) {
            instance = new Positions();
            instance.save();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(positionFile), StandardCharsets.UTF_8))) {
            instance = Storage.GSON.fromJson(reader, Positions.class);
        } catch (Exception e) {
            throw new RuntimeException("无法加载positions.json文件");
        }

        return instance;
    }

    /**
     * 保存到文件
     */
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(positionFile,StandardCharsets.UTF_8))) {
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

    /**
     * 根据名字删除一个坐标
     *
     * @param name
     */
    public void remove(String name) {
        this.positions.removeIf(position -> {
            return position.name.equals(name);
        });
        save();
    }


    /**
     * 单个坐标类
     */
    public static class Position {
        public String dimension;
        public String mainPos;
        public String netherPos;
        public String endPos;
        public String name;
        public String description;

        public Position() {
        }

        public Position(String dimension, String mainPos, String netherPos, String endPos, String name, String description) {
            this.dimension = dimension;
            this.mainPos = mainPos;
            this.netherPos = netherPos;
            this.endPos = endPos;
            this.name = name;
            this.description = description;
        }

        public String toString(int index) {
            if (dimension.equals("末地")) {
                return "§b" + index + ". " +
                        name + "-------------------" +
                        "\n 维度：" + dimension +
                        "\n 末地坐标：" + endPos +
                        "\n 简介：" + description +
                        "\n\n";
            } else if (dimension.equals("主世界")) {
                return "§6" + index + ". " +
                        name + "-------------------" +
                        "\n 维度：" + dimension +
                        "\n 主世界坐标：" + mainPos +
                        "\n 地狱坐标：" + netherPos +
                        "\n 简介：" + description +
                        "\n\n";
            } else if (dimension.equals("地狱")) {
                return "§4" + index + ". " +
                        name + "-------------------" +
                        "\n 维度：" + dimension +
                        "\n 地狱坐标：" + netherPos +
                        "\n 简介：" + description +
                        "\n\n";
            } else return "§f" + index + ". " +
                    name + "-------------------" +
                    "\n 维度：" + dimension +
                    "\n 主世界坐标：" + mainPos +
                    "\n 地狱坐标：" + netherPos +
                    "\n 末地坐标：" + endPos +
                    "\n 简介：" + description +
                    "\n\n";
        }

        @Override
        public String toString() {
            if (dimension.equals("末地")) {
                return "§b" +
                        name + "-------------------" +
                        "\n 维度：" + dimension +
                        "\n 末地坐标：" + endPos +
                        "\n 简介：" + description +
                        "\n\n";
            } else if (dimension.equals("主世界")) {
                return "§6" +
                        name + "-------------------" +
                        "\n 维度：" + dimension +
                        "\n 主世界坐标：" + mainPos +
                        "\n 地狱坐标：" + netherPos +
                        "\n 简介：" + description +
                        "\n\n";
            } else if (dimension.equals("地狱")) {
                return "§4" +
                        name + "-------------------" +
                        "\n 维度：" + dimension +
                        "\n 地狱坐标：" + netherPos +
                        "\n 简介：" + description +
                        "\n\n";
            } else return "§f" +
                    name + "-------------------" +
                    "\n 维度：" + dimension +
                    "\n 主世界坐标：" + mainPos +
                    "\n 地狱坐标：" + netherPos +
                    "\n 末地坐标：" + endPos +
                    "\n 简介：" + description +
                    "\n\n";
        }
    }
}
