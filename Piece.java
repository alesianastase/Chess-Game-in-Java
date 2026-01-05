import java.util.List;
public abstract class Piece implements ChessPiece {
    protected Colors color;
    protected Position position;

    public Piece(Colors color, Position position) {
        this.color = color;
        this.position = position;
    }

    public Colors getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
