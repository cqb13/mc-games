package dev.cqb13.McGames.modules;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Wordle extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Boolean> hideChatMessages = sgGeneral.add(new BoolSetting.Builder()
      .name("hide-chat-messages")
      .description("Hide chat messages from players while the game is running.")
      .defaultValue(true)
      .build());

  public Wordle() {
    super(McGames.CATEGORY, "wordle", "Play Wordle forever.");
  }

  private String[] wordList;
  private String hiddenWord;
  private int lives;
  private int wins;
  private int losses;

  @Override
  public void onActivate() {
    McGamesChatUtils.sendGameMsg(title, "Your chat messages will not send while Wordle is active.");
  }

  @EventHandler
  private void onSendMessage(SendMessageEvent event) {
    mc.inGameHud.getChatHud().clear(false);
    event.cancel();
  }

  @EventHandler
  private void onMessageReceive(ReceiveMessageEvent event) {
    if (!hideChatMessages.get())
      return;
    event.cancel();
  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    toggle();
  }
}
