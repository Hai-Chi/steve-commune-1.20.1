package top.krahsu.utils;

import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

public class ChatUtils {
    public static void highlightPosition(Text message) {
        if (message.getSiblings().size() > 0) {
            for (Text text : message.getSiblings()) {
                highlightPosition(message);
            }
        }
        TextContent content = message.getContent();
        System.out.println("message:" + message);
    }
}
