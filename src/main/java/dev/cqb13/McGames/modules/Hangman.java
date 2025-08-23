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

public class Hangman extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Difficulty> difficulty = sgGeneral.add(new EnumSetting.Builder<Difficulty>()
      .name("difficulty")
      .description("Affects word length and guess attempts.")
      .defaultValue(Difficulty.Normal)
      .onChanged(d -> difficultySwitch())
      .build());

  private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
      .name("mode")
      .description("Single Round: one game, Survival: play until you die, Infinite: play forever.")
      .defaultValue(Mode.SingleRound)
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
  private int lives;
  private ArrayList<String> guessedLetters;
  private int wordsGuessed;

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
    sendStartGameMsg();
  }

  public void difficultySwitch() {
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

  }

  private void sendCurrentState() {
  }

  enum Mode {
    SingleRound,
    Survival,
    Infinite,
  }
}
