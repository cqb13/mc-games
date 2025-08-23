package dev.cqb13.McGames.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.utils.Utils;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class McGamesChatUtils {
  public static void sendGameMsg(String gameName, String message) {
    sendMsg(true, gameName, Text.of(message));
  }

  public static void sendGameMsg(String gameName, Text message) {
    sendMsg(true, gameName, message);
  }

  public static void sendGameMsg(boolean includePrefix, String gameName, Text message) {
    sendMsg(includePrefix, gameName, message);
  }

  private static void sendMsg(boolean includePrefix, String gameName, Text message) {
    if (mc.world == null)
      return;
    MutableText text = Text.empty();

    if (includePrefix) {
      MutableText prefix = Text.empty();
      prefix.append(createPrefix("MC Games", Formatting.GOLD));
      prefix.append(" ");
      prefix.append(createPrefix(Utils.nameToTitle(gameName), Formatting.GOLD));
      prefix.append(" ");
      text.append(prefix);
    }

    text.append(message);

    ((IChatHud) mc.inGameHud.getChatHud()).meteor$add(text, 0);
  }

  private static MutableText createPrefix(String content, Formatting formatting) {
    MutableText prefix = Text.empty();
    prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY));
    prefix.append("[");
    MutableText text = Text.literal(content);
    text.setStyle(text.getStyle().withFormatting(formatting));
    prefix.append(text);
    prefix.append("]");

    return prefix;
  }
}
