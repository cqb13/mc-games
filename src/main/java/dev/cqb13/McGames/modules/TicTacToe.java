package dev.cqb13.McGames.modules;

import java.util.ArrayList;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.Difficulty;
import dev.cqb13.McGames.enums.GameMode;
import dev.cqb13.McGames.enums.TicTacToe.Piece;
import dev.cqb13.McGames.enums.TicTacToe.Player;
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

public class TicTacToe extends Module {
  public class MiniMaxResult {
    private int location;
    private int score;
    private int winningMove;

    public MiniMaxResult(int location, int score) {
      this.location = location;
      this.score = score;
      this.winningMove = 0;
    }

    public int getLocation() {
      return this.location;
    }

    public int getScore() {
      return this.score;
    }

    public int getWinningMove() {
      return this.winningMove;
    }

    public void setScore(int score) {
      this.score = score;
    }

    public void setLocation(int location) {
      this.location = location;
    }

    public void setWinningMove(int winningMoves) {
      this.winningMove = winningMoves;
    }
  }

  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

  private final Setting<Difficulty> difficulty = sgGeneral.add(new EnumSetting.Builder<Difficulty>()
      .name("difficulty")
      .description("Affects word length and guess attempts.")
      .defaultValue(Difficulty.Normal)
      .onChanged(d -> optionSwitch())
      .build());

  private final Setting<Player> firstMove = sgGeneral.add(new EnumSetting.Builder<Player>()
      .name("first-move")
      .description("The player who will take the first move.")
      .defaultValue(Player.McPlayer)
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

  private Piece[] board = new Piece[9];
  private int turn;

  @Override
  public void onActivate() {
    setup();
    McGamesChatUtils.sendGameMsg(title, "Your chat messages will not send while Tic-Tac-Toe is active.");
    sendStartGameMsg();

    if (firstMove.get() == Player.Computer) {
      int pos = makeComputerMove();

      board[pos] = Piece.O;

      turn += 1;
    }

    sendCurrentStateMsg();
  }

  public void optionSwitch() {
    setup();
    sendStartGameMsg();

    if (firstMove.get() == Player.Computer) {
      int pos = makeComputerMove();

      board[pos] = Piece.O;

      turn += 1;
    }

    sendCurrentStateMsg();
  }

  private void setup() {
    turn = 0;
    for (int i = 0; i < 9; i++) {
      board[i] = Piece.Empty;
    }
  }

  @EventHandler
  private void onSendMessage(SendMessageEvent event) {
    mc.inGameHud.getChatHud().clear(false);
    event.cancel();

    // Process Player Move
    if (event.message.length() > 1) {
      McGamesChatUtils.sendGameMsg(title, "Please only enter 1 digit.");
      sendCurrentStateMsg();
      return;
    }

    int numValue = Integer.parseInt(event.message);

    if (numValue < 1 || numValue > 9) {
      McGamesChatUtils.sendGameMsg(title, "Digit must be between 1 and 9.");
      sendCurrentStateMsg();
      return;
    }

    if (board[numValue - 1] != Piece.Empty) {
      McGamesChatUtils.sendGameMsg(title, "A piece is already placed in this square.");
      sendCurrentStateMsg();
      return;
    }

    board[numValue - 1] = Piece.X;

    if (playerWon(board)) {
      McGamesChatUtils.sendGameMsg(title, "You Won!");
      sendCurrentStateMsg();

      if (mode.get() != GameMode.Infinite) {
        toggle();
        return;
      }

      setup();
      sendCurrentStateMsg();
      return;
    }

    turn += 1;

    if (turn == 9) {
      McGamesChatUtils.sendGameMsg(title, "It's a Draw.");
      sendCurrentStateMsg();

      if (mode.get() == GameMode.SingleRound) {
        toggle();
        return;
      }

      setup();
      sendCurrentStateMsg();
      return;
    }

    // Process Computer Move
    int pos = makeComputerMove();

    board[pos] = Piece.O;

    if (playerWon(board)) {
      McGamesChatUtils.sendGameMsg(title, "You Lost :( !");
      sendCurrentStateMsg();

      if (mode.get() != GameMode.Infinite) {
        toggle();
        return;
      }

      setup();
      sendCurrentStateMsg();
      return;

    }

    turn += 1;

    if (turn == 9) {
      McGamesChatUtils.sendGameMsg(title, "It's a Draw.");
      sendCurrentStateMsg();

      if (mode.get() == GameMode.SingleRound) {
        toggle();
        return;
      }

      setup();
      sendCurrentStateMsg();
      return;
    }

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

  private int makeComputerMove() {
    double chance = Math.random();

    if (difficulty.get() == Difficulty.Easy || (difficulty.get() == Difficulty.Normal && chance > 0.5)) {
      int pos = (int) Math.random() * 9;
      while (board[pos] != Piece.Empty) {
        pos = (int) (Math.random() * 9);
      }

      return pos;
    }

    MiniMaxResult bestMove = miniMax(Player.Computer, turn, board);

    return bestMove.getLocation();
  }

  private MiniMaxResult miniMax(Player player, int turn, Piece[] state) {
    Player computerPlayer = Player.Computer;
    Player otherPlayer = player == Player.McPlayer ? Player.Computer : Player.McPlayer;

    ArrayList<Integer> openSquares = new ArrayList<>();
    for (int i = 0; i < state.length; i++) {
      if (state[i] == Piece.Empty) {
        openSquares.add(i);
      }
    }
    MiniMaxResult result = new MiniMaxResult(-1, 0);

    // The player won on the last turn
    if (playerWon(state)) {
      if (player == computerPlayer) {
        result.setScore(-15 + turn);
      } else {
        result.setScore(15 - turn);
      }

      return result;
    }

    if (openSquares.isEmpty()) {
      result.setScore(0);
      return result;
    }

    MiniMaxResult bestMove = new MiniMaxResult(-1,
        player == computerPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);

    int wins = 0;
    for (int location : openSquares) {
      if (player == Player.McPlayer) {
        state[location] = Piece.X;
      } else {
        state[location] = Piece.O;
      }
      MiniMaxResult miniResult = miniMax(otherPlayer, turn + 1, state);

      if (miniResult.getScore() < 0) {
        wins += 1;
      }

      state[location] = Piece.Empty;

      if (player == computerPlayer) {
        if (miniResult.getScore() > bestMove.getScore()) {
          bestMove.setScore(miniResult.getScore());
          bestMove.setLocation(location);
          bestMove.setWinningMove(miniResult.getWinningMove());
        }
      } else {
        if (miniResult.getScore() < bestMove.getScore()) {
          bestMove.setScore(miniResult.getScore());
          bestMove.setLocation(location);
          bestMove.setWinningMove(miniResult.getWinningMove());
        }
      }

      if (miniResult.getScore() == bestMove.getScore() && miniResult.getWinningMove() < bestMove.getWinningMove()) {
        bestMove.setWinningMove(miniResult.getWinningMove());
        bestMove.setLocation(miniResult.getLocation());
      }
    }
    bestMove.setWinningMove(wins);
    return bestMove;
  }

  private boolean playerWon(Piece[] state) {
    // Check verticals
    for (int i = 0; i < 3; i++) {
      if (state[i] == state[i + 3] && state[i] == state[i + 6] && state[i] != Piece.Empty) {
        return true;
      }
    }

    // Check horizontal
    for (int i = 0; i < 9; i += 3) {
      if (state[i] == state[i + 1] && state[i] == state[i + 2] && state[i] != Piece.Empty) {
        return true;
      }
    }

    if (state[4] == Piece.Empty) {
      return false;
    }

    // Check top left to bottom right and top right to bottom left
    return (state[0] == state[4] && state[4] == state[8])
        || (state[2] == state[4] && state[4] == state[6]);
  }

  private void sendStartGameMsg() {
    MutableText message = Text.empty();
    message.append("\n\n");
    message.append("Difficulty: ");
    message.append(difficulty.get().getStyledDifficulty());
    message.append("\n 1 | 2 | 3 \n--.--.--\n 4 | 5 | 6 \n--.--.--\n 7 | 8 | 9\n");
    message.append("\n");
    McGamesChatUtils.sendGameMsg(title, message);
  }

  private void sendCurrentStateMsg() {
    MutableText message = Text.empty();
    message.append("\n\n");
    for (int i = 0; i < 9; i++) {
      message.append(" " + board[i].getValue() + " ");
      if (i % 3 == 0 || i % 3 == 1) {
        message.append("|");
      }
      if (i % 3 == 2 && i != 8) {
        message.append("\n");
        message.append("--.--.--\n");
      }
    }
    McGamesChatUtils.sendGameMsg(title, message);
  }
}
