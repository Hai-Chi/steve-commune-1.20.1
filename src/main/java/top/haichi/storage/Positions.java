package top.haichi.storage;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import static top.haichi.storage.Dimension.Types.*;

public class Positions {
    public final ArrayList<Position> positionArrayList = new ArrayList<>();
    private static final File POSITION_FILE = new File(Storage.DATA_DIR + "/positions.json");

    public Positions() {
        if (!POSITION_FILE.exists()) {
            try {
                if (POSITION_FILE.createNewFile()) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(POSITION_FILE, StandardCharsets.UTF_8))) {
                        Storage.GSON.toJson(this, writer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Stream<Position> Search(String keyword) {
        return positionArrayList.stream().filter(position -> position.name.contains(keyword));
    }

    public void showList(ServerCommandSource source) {
        Text startText = Text.literal("地址列表为：\n").setStyle(
                Style.EMPTY.withColor(TextColor.fromRgb(0xffffff))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("此列表为公用列表")))
        );
        source.sendMessage(startText);

        NbtList pages = new NbtList();
        NbtCompound bookTag = new NbtCompound();

        for (int i = 0; i < positionArrayList.size(); i += 2) {
            MutableText pageText = positionArrayList.get(i).toText(-1);
            if (i + 1 < positionArrayList.size()) {
                pageText.append(positionArrayList.get(i + 1).toText(-1));
            }
            source.sendMessage(pageText);
            pages.add(NbtString.of(Text.Serializer.toJson(pageText)));
        }
        bookTag.put("pages", pages);
        bookTag.putString("author", "CharlesHsu & HaiChi");
        bookTag.putString("title", "地址の书");

        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.setNbt(bookTag);

        Objects.requireNonNull(source.getPlayer()).giveItemStack(book);
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POSITION_FILE, StandardCharsets.UTF_8))) {
            Storage.GSON.toJson(this, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一个新坐标
     *
     * @param position
     */
    public void add(Position position) {
        this.positionArrayList.add(position);
        save();
    }

    public void remove(String name) {
        this.positionArrayList.removeIf(position -> position.name.equals(name));
        save();
    }

    public static class Position {
        private Dimension.Types dimension = OVER_WORLD;
        private String name = "";
        private String description = "";
        private Coordinate coordinate = new Coordinate(0, 0, 0);

        public Position(Dimension.Types dimension, String name, String description, BlockPos blockPos) {
            this.dimension = dimension;
            this.name = name;
            this.description = description;
            this.coordinate.x = blockPos.getX();
            this.coordinate.y = blockPos.getY();
            this.coordinate.z = blockPos.getZ();
        }

        public void show(ServerCommandSource source) {
            show(source, -1);
        }

        public MutableText toText(int index) {
            MutableText message = Text.literal(String.format("---%s---", name)).append("\n");

            if (index != -1) message.append(Text.literal(index + ". \n"));

            Text dimensionName;

            switch (dimension) {
                case OVER_WORLD -> {
                    dimensionName = Text.literal("主世界").setStyle(
                            Style.EMPTY.withColor(0x155724)
                    );
                    message.append(Text.literal("主世界坐标：").setStyle(
                                    Style.EMPTY.withColor(0x81ff9f)
                            )).append(coordinate.toText(name, ":0:false:0:Internal_overworld_waypoints")).append("\n")
                            .append(Text.literal("地狱坐标：").setStyle(
                                    Style.EMPTY.withColor(0x721c24)
                            )).append(coordinate.toNether().toText(name, ":0:false:0:Internal_the_nether_waypoints"));
                }
                case THE_NETHER -> {
                    dimensionName = Text.literal("地狱").setStyle(
                            Style.EMPTY.withColor(0xf8d7da)
                    );
                    message.append(Text.literal("地狱坐标：").setStyle(
                            Style.EMPTY.withColor(0xf8d7da)
                    )).append(coordinate.toNether().toText(name, ":0:false:0:Internal_the_nether_waypoints"));
                }
                case THE_END -> {
                    dimensionName = Text.literal("末地").setStyle(
                            Style.EMPTY.withColor(0xa37acc)
                    );
                    message.append(Text.literal("末地坐标：").setStyle(
                            Style.EMPTY.withColor(0xa37acc)
                    )).append(coordinate.toText(name, ":0:false:0:Internal_the_end_waypoints"));
                }
                default -> {
                    dimensionName = Text.literal("未知").setStyle(
                            Style.EMPTY.withColor(0x999999)
                    );
                    message.append(Text.literal("坐标：").setStyle(
                            Style.EMPTY.withColor(0x999999)
                    )).append(coordinate.toText());
                }
            }

            message.append("\n维度：").append(dimensionName)
                    .append("\n简介：").append(description).append("\n");

            return message;
        }

        public void show(ServerCommandSource source, int index) {
            source.sendMessage(toText(index));
        }


        public static class Coordinate {
            public int x = 0;
            public int y = 0;
            public int z = 0;

            public Coordinate(int x, int y, int z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            public Coordinate toNether() {
                return new Coordinate(x / 8, y / 8, z);
            }

            @Override
            public String toString() {
                return String.format("§2[%d,%d,%d]§r", x, y, z);
            }

            public String getCommand(String name) {
                return String.format("/xaero_waypoint_add:%s:S:%d:%d:%d", name, x, y, z);
            }

            public MutableText toText() {
                return Text.literal(String.format("[%d,%d,%d]", x, y, z)).setStyle(
                        Style.EMPTY.withColor(TextColor.fromRgb(0x81ff9f))
                );
            }

            public MutableText toText(String name, String dimension) {
                return Text.literal(String.format("[%d,%d,%d]", x, y, z)).setStyle(
                        Style.EMPTY.withColor(TextColor.fromRgb(0x81ff9f)).withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(name) + dimension)
                        ).withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("添加航点，需要XMW或XMM"))
                        )
                ).append(Text.literal(" [Highlight]").setStyle(
                        Style.EMPTY.withColor(TextColor.fromRgb(0x0c5460)).withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/highlightWaypoint %d %d %d", x, y, z))
                        ).withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("高亮显示，需要OMMC"))
                        )
                ));
            }
        }

    }
}

