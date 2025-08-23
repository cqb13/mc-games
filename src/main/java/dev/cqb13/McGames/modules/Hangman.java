package dev.cqb13.McGames.modules;

import java.util.ArrayList;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.Difficulty;
import dev.cqb13.McGames.utils.HangmanUtils;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import joptsimple.internal.Strings;
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
  private ArrayList<String> guessedLetters = new ArrayList<>();
  private int wordsGuessed = 0;
  private int round = 0;

  @Override
  public void onActivate() {
    try {
      wordList = HangmanUtils.fetchWordList();
    } catch (Exception e) {
      error(e.getMessage());
      toggle();
      return;
    }

    setup(true);
    McGamesChatUtils.sendGameMsg(title, "Your chat messages will not send while Hangman is active.");
    McGamesChatUtils.sendGameMsg(title, "Enter `!used` to see used letters.");
    sendStartGameMsg();
  }

  public void optionSwitch() {
    if (wordList == null) {
      return;
    }
    setup(true);
    sendStartGameMsg();
  }

  private void setup(boolean resetLives) {
    guessedLetters = new ArrayList<>();
    if (resetLives) {
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
    }

    hiddenWord = HangmanUtils.pickWord(wordList, minWordLen, maxWordLen);
    guessState = "_".repeat(hiddenWord.length());
  }

  @EventHandler
  private void onSendMessage(SendMessageEvent event) {
    mc.inGameHud.getChatHud().clear(false);
    event.cancel();
    if (event.message.equals("!used")) {
      sendUsedLettersMsg();
      sendCurrentStateMsg();
      return;
    }

    if (event.message.length() > 1) {
      McGamesChatUtils.sendGameMsg(title, "Please guess 1 letter at a time.");
      sendCurrentStateMsg();
      return;
    }

    if (guessedLetters.contains(event.message.toLowerCase())) {
      McGamesChatUtils.sendGameMsg(title, "You have already guessed this letter.");
      sendUsedLettersMsg();
      sendCurrentStateMsg();
      return;
    }

    if (hiddenWord.contains(event.message.toLowerCase())) {
      StringBuilder temp = new StringBuilder(guessState);
      for (int i = 0; i < hiddenWord.length(); i++) {
        if (hiddenWord.charAt(i) == event.message.charAt(0)) {
          temp.setCharAt(i, event.message.charAt(0));
        }
      }
      guessState = temp.toString().toLowerCase();

      if (guessState.equals(hiddenWord)) {
        round += 1;
        wordsGuessed += 1;

        McGamesChatUtils.sendGameMsg(title, "You Guessed the Word, " + hiddenWord + "!");

        if (mode.get() == Mode.SingleRound) {
          toggle();
          return;
        }

        setup(mode.get() == Mode.Infinite);
        sendStartGameMsg();
        return;
      }
    } else {
      lives -= 1;
      if (lives == 0) {
        McGamesChatUtils.sendGameMsg(title, "You Lost! the word was " + hiddenWord + ".");
        if (mode.get() == Mode.Survival && round > 0) {
          McGamesChatUtils.sendGameMsg(title, "You survived for " + round + "rounds");
        }
        if (mode.get() == Mode.SingleRound || mode.get() == Mode.Survival) {
          toggle();
          return;
        }
        setup(true);
        sendStartGameMsg();
        return;
      }
    }

    guessedLetters.addLast(event.message.toLowerCase());

    sendCurrentStateMsg();
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

  public void sendUsedLettersMsg() {
    String usedLetters = "Used Letters: ";
    usedLetters += Strings.join(guessedLetters, ", ");
    McGamesChatUtils.sendGameMsg(false, title, Text.of(usedLetters));
  }

  enum Mode {
    SingleRound,
    Survival,
    Infinite,
  }
}
