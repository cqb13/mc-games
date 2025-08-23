package dev.cqb13.McGames.modules;

import java.util.ArrayList;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.Difficulty;
import dev.cqb13.McGames.utils.HangmanUtils;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
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

public class Hangman extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Difficulty> difficulty = sgGeneral.add(new EnumSetting.Builder<Difficulty>()
      .name("difficulty")
      .description("Affects word length and guess attempts.")
      .defaultValue(Difficulty.Normal)
      .onChanged(d -> optionSwitch())
      .build());

  private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
      .name("mode")
      .description("Single Round: one game, Survival: play until you die, Infinite: play forever.")
      .defaultValue(Mode.SingleRound)
      .onChanged(m -> optionSwitch())
      .build());

  private final Setting<Boolean> hideChatMessages = sgGeneral.add(new BoolSetting.Builder()
      .name("hide-chat-messages")
      .description("Hide chat messages from players while the game is running.")
      .defaultValue(true)
      .build());

  public Hangman() {
    super(McGames.CATEGORY, "hangman", "Guess letters to reveal the word.");
  }

  private String[] wordList;
  private int minWordLen;
  private int maxWordLen;
  private String hiddenWord;
  private String guessState;
  private int lives;
  private ArrayList<String> guessedLetters;
  private int wordsGuessed;
  private int round;

  @Override
  public void onActivate() {
    try {
      wordList = HangmanUtils.fetchWordList();
    } catch (Exception e) {
      error(e.getMessage());
      toggle();
      return;
    }

    setup();
    McGamesChatUtils.sendGameMsg(title, "Your chat messages will not send while Hangman is active.");
    McGamesChatUtils.sendGameMsg(title, "Enter `!help` for more details.");
    sendStartGameMsg();
    sendCurrentState();
  }

  public void optionSwitch() {
    if (wordList == null) {
      return;
    }
    setup();
    sendStartGameMsg();
  }

  private void setup() {
    switch (difficulty.get()) {
      case Difficulty.Easy:
        lives = 10;
        minWordLen = 3;
        maxWordLen = 6;
        break;
      case Difficulty.Normal:
        lives = 8;
        minWordLen = 4;
        maxWordLen = 10;
        break;
      case Difficulty.Hard:
        lives = 5;
        minWordLen = 6;
        maxWordLen = 18;
        break;
    }

    hiddenWord = HangmanUtils.pickWord(wordList, minWordLen, maxWordLen);
    guessState = "_".repeat(hiddenWord.length());
    info(hiddenWord);
  }

  @EventHandler
  private void onSendMessage(SendMessageEvent event) {
    info(event.message);
    event.cancel();
  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    toggle();
  }

  private void sendStartGameMsg() {
    MutableText message = Text.empty();
    message.append("\nDifficulty: ");
    MutableText difficultyText = Text.empty();
    switch (difficulty.get()) {
      case Difficulty.Easy:
        difficultyText.setStyle(difficultyText.getStyle().withBold(true).withFormatting(Formatting.BLUE));
        difficultyText.append("EASY\n");
        break;
      case Difficulty.Normal:
        difficultyText.setStyle(difficultyText.getStyle().withBold(true).withFormatting(Formatting.GREEN));
        difficultyText.append("NORMAL\n");
        break;
      case Difficulty.Hard:
        difficultyText.setStyle(difficultyText.getStyle().withBold(true).withFormatting(Formatting.RED));
        difficultyText.append("HARD\n");
        break;

    }
    message.append(difficultyText);
    message.append("Lives: " + lives + "\n");
    if (mode.get() == Mode.Survival || mode.get() == Mode.Infinite) {
      message.append("Round: " + round + "\n");
    }
    if (mode.get() == Mode.Infinite) {
      message.append("Correct Words: " + wordsGuessed + "\n");
    }
    message.append("\n" + guessState + " (" + hiddenWord.length() + ")" + "\n");

    McGamesChatUtils.sendGameMsg(false, title, message);
  }

  private void sendCurrentStateMsg() {
    MutableText message = Text.empty();
    message.append("Lives: " + lives + "\n");
    message.append("\n" + guessState + " (" + hiddenWord.length() + ")" + "\n");
    McGamesChatUtils.sendGameMsg(false, title, message);
  }

  enum Mode {
    SingleRound,
    Survival,
    Infinite,
  }
}
