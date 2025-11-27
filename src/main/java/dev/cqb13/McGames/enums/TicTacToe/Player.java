package dev.cqb13.McGames.enums.TicTacToe;

public enum Player {
    McPlayer,
    Computer;

    public Player opposite() {
        if (this == Player.McPlayer) {
            return Player.Computer;
        } else {
            return Player.McPlayer;
        }
    }
}
