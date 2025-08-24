package dev.cqb13.McGames.modules;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.WordleBoxState;
import dev.cqb13.McGames.utils.GameUtils;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Wordle extends Module {
  record WordleBox(char letter, WordleBoxState state) {
  }

  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Boolean> hideChatMessages = sgGeneral.add(new BoolSetting.Builder()
      .name("hide-chat-messages")
      .description("Hide chat messages from players while the game is running.")
      .defaultValue(true)
      .build());

  public Wordle() {
    super(McGames.CATEGORY, "wordle", "Play Wordle forever.");
  }

  private static final String WORD_LIST_URL = "https://raw.githubusercontent.com/cqb13/mc-games/refs/heads/data/wordle-words.txt";
  private String[] wordList;
  private WordleBox[][] gameState = new WordleBox[6][5];
  private String hiddenWord;
  private int currentRow;

  @Override
  public void onActivate() {
    try {
      wordList = GameUtils.fetchWordList(WORD_LIST_URL);
    } catch (Exception e) {
      error(e.getMessage());
      toggle();
      return;
    }

    setup();
    McGamesChatUtils.sendGameMsg(title, "Your chat messages will not send while Wordle is active.");
    sendGameStateMsg();
  }

  private void setup() {
    hiddenWord = GameUtils.pickWordFromList(wordList, 5, 5);

    currentRow = 0;
    for (int y = 0; y < gameState.length; y++) {
      for (int x = 0; x < gameState[y].length; x++) {
        gameState[y][x] = new WordleBox(' ', WordleBoxState.Empty);
      }
    }
  }

  @EventHandler
  private void onSendMessage(SendMessageEvent event) {
    mc.inGameHud.getChatHud().clear(false);
    event.cancel();
    if (event.message.length() != 5) {
      McGamesChatUtils.sendGameMsg(title, "Please enter a 5 letter word.");
      sendGameStateMsg();
      return;
    }

    boolean found = false;
    for (String word : wordList) {
      if (word.equals(event.message.toLowerCase())) {
        found = true;
        break;
      }
    }

    if (!found) {
      McGamesChatUtils.sendGameMsg(title, "Word not in word list.");
      sendGameStateMsg();
      return;
    }

    updateGameState(event.message.toLowerCase());

    if (hiddenWord.equals(event.message.toLowerCase())) {
      sendGameStateMsg();
      McGamesChatUtils.sendGameMsg(title, "You got it!");
      setup();
      sendGameStateMsg();
      return;
    }

    sendGameStateMsg();

    currentRow += 1;
    if (currentRow > 5) {
      McGamesChatUtils.sendGameMsg(title, "Not quite, the word was " + hiddenWord + ".");
      setup();
      sendGameStateMsg();
      return;
    }
  }

  private void updateGameState(String guess) {
    String[] guessLetters = guess.split("");
    String[] hiddenWordLettersCopy = hiddenWord.split("");

    for (int i = 0; i < 5; i++) {
      if (guessLetters[i].equals(hiddenWordLettersCopy[i])) {
        gameState[currentRow][i] = new WordleBox(guess.charAt(i), WordleBoxState.Correct);
        guessLetters[i] = "-";
        hiddenWordLettersCopy[i] = "*";
      }
    }

    for (int i = 0; i < 5; i++) {
      if (gameState[currentRow][i].state() == WordleBoxState.Correct) {
        continue;
      }

      String letter = guessLetters[i];
      boolean found = false;

      for (int j = 0; j < 5; j++) {
        if (letter.equals(hiddenWordLettersCopy[j])) {
          found = true;
          hiddenWordLettersCopy[j] = "*";
          break;
        }
      }

      if (found) {
        gameState[currentRow][i] = new WordleBox(guess.charAt(i), WordleBoxState.Included);
      } else {
        gameState[currentRow][i] = new WordleBox(guess.charAt(i), WordleBoxState.Incorrect);
      }
    }
  }

  private void sendGameStateMsg() {
    MutableText message = Text.empty();
    message.append("\n\n");
    for (WordleBox[] row : gameState) {
      MutableText rowText = Text.empty();
      rowText.append("|");
      for (WordleBox box : row) {
        Text styledLetter = styleLetter(box);
        rowText.append(styledLetter);
      }
      rowText.append("|\n");
      message.append(rowText);
    }
    McGamesChatUtils.sendGameMsg(title, message);
  }

  private Text styleLetter(WordleBox box) {
    MutableText letter = Text.empty();
    switch (box.state()) {
      case WordleBoxState.Correct:
        letter.setStyle(letter.getStyle().withFormatting(Formatting.GREEN));
        break;
      case WordleBoxState.Included:
        letter.setStyle(letter.getStyle().withFormatting(Formatting.YELLOW));
        break;
      case WordleBoxState.Incorrect:
        letter.setStyle(letter.getStyle().withFormatting(Formatting.GRAY));
        break;
      case WordleBoxState.Empty:
        letter.setStyle(letter.getStyle().withFormatting(Formatting.WHITE));
        break;
    }
    letter.append(" " + box.letter() + " ");

    return letter;
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
}
