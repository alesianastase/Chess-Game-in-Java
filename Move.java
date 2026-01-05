public class Move {
    private Colors playerColor;
    private Position from;
    private Position to;
    private Piece captured = null;

    public Move(Colors playerColor, Position from, Position to, Piece captured) {
        this.playerColor = playerColor;
        this.from = from;
        this.to = to;
        this.captured = captured;
    }

    public Colors getPlayerColor() {
        return playerColor;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getCaptured() {
        return captured;
    }
}
