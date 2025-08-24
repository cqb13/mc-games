package dev.cqb13.McGames.modules;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.Difficulty;
import dev.cqb13.McGames.enums.GameMode;
import dev.cqb13.McGames.enums.TicTacToe.Piece;
import dev.cqb13.McGames.utils.GameUtils;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TicTacToe extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Difficulty> difficulty = sgGeneral.add(new EnumSetting.Builder<Difficulty>()
      .name("difficulty")
      .description("Affects word length and guess attempts.")
      .defaultValue(Difficulty.Normal)
      .onChanged(d -> optionSwitch())
      .build());

  private final Setting<GameMode> mode = sgGeneral.add(new EnumSetting.Builder<GameMode>()
      .name("mode")
      .description("Single Round: one game, Survival: play until you die, Infinite: play forever.")
      .defaultValue(GameMode.SingleRound)
      .onChanged(m -> optionSwitch())
      .build());

  private final Setting<Boolean> hideChatMessages = sgGeneral.add(new BoolSetting.Builder()
      .name("hide-chat-messages")
      .description("Hide chat messages from players while the game is running.")
      .defaultValue(true)
      .build());

  public TicTacToe() {
    super(McGames.CATEGORY, "tic-tac-toe", "Play Tic-Tac-Toe");
  }

  private Piece[][] board = new Piece[3][3];

  @Override
  public void onActivate() {
    setup();
    McGamesChatUtils.sendGameMsg(title, "Your chat messages will not send while Tic-Tac-Toe is active.");
    sendStartGameMsg();
  }

  public void optionSwitch() {
    setup();
    sendStartGameMsg();
  }

  private void setup() {
  }

  @EventHandler
  private void onSendMessage(SendMessageEvent event) {
    mc.inGameHud.getChatHud().clear(false);
    event.cancel();

    sendCurrentStateMsg();
  }

  @EventHandler
  private void onMessageReceive(ReceiveMessageEvent event) {
    if (!hideChatMessages.get() || GameUtils.mcGamesMessage(event, title)) {
      return;
    }
    event.cancel();
  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    toggle();
  }

  private void sendStartGameMsg() {
    MutableText message = Text.empty();
    message.append("\n\n");
    message.append("Difficulty: ");
    message.append(difficulty.get().getStyledDifficulty() + "\n");
    McGamesChatUtils.sendGameMsg(title, message);
  }

  private void sendCurrentStateMsg() {
    MutableText message = Text.empty();
    McGamesChatUtils.sendGameMsg(title, message);
  }
}
