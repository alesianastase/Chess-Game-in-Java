import java.util.*;

public class King extends Piece {
    public King (Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'K';
    }

    private void tryMove(Board board, List<Position> moves, char newX, int newY) {
        // checks if inside board
        if (newX < 'A' || newX > 'H' || newY < 1 || newY > 8) {
            return;
        }

        Position next = new Position(newX, newY);
        Piece p = board.getPieceAt(next);

        if (p == null) {
            moves.add(next);
            //empty square, can move
        } else if (p.getColor() != this.getColor()) {
            moves.add(next);
            //can capture enemy piece
        }
        //else, cannot move
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        Position pos = getPosition();
        char x = pos.getX();
        int y = pos.getY();

        tryMove(board, moves, (char)(x), y + 1); // up
        tryMove(board, moves, (char)(x), y - 1); // down
        tryMove(board, moves, (char)(x + 1), y); // right
        tryMove(board, moves, (char)(x - 1), y); // left

        tryMove(board, moves, (char)(x + 1), y + 1); // up-right
        tryMove(board, moves, (char)(x - 1), y + 1); // up-left
        tryMove(board, moves, (char)(x + 1), y - 1); // down-right
        tryMove(board, moves, (char)(x - 1), y - 1); // down-left

        return moves;
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        List<Position> moves = getPossibleMoves(board);
        return moves.contains(kingPosition);
    }
}
