package dev.cqb13.McGames.enums.TicTacToe;

public enum Piece {
  X("X"),
  O("O"),
  Empty(" ");

  private final String value;

  private Piece(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
