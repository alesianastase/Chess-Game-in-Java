import java.util.*;

public class Knight extends Piece {
    public Knight (Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'N';
    }

    private void tryMove(Board board, List<Position> moves, char newX, int newY) {
        //checks if is inside board
        if (newX < 'A' || newX > 'H' || newY < 1 || newY > 8) {
            return; // outside board
        }

        Position next = new Position(newX, newY);
        Piece p = board.getPieceAt(next);

        if (p == null) {
            moves.add(next);
            //the square is empty, piece can be moved
        } else if (p.getColor() != this.getColor()) {
            moves.add(next);
            //square has diff color piece, can be captured
        }
        //else, cannot move
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        Position pos = getPosition();
        char x = pos.getX();
        int y = pos.getY();

        tryMove(board, moves, (char)(x + 1), y + 2);
        tryMove(board, moves, (char)(x - 1), y + 2);
        tryMove(board, moves, (char)(x + 1), y - 2);
        tryMove(board, moves, (char)(x - 1), y - 2);

        tryMove(board, moves, (char)(x + 2), y + 1);
        tryMove(board, moves, (char)(x + 2), y - 1);
        tryMove(board, moves, (char)(x - 2), y + 1);
        tryMove(board, moves, (char)(x - 2), y - 1);

        return moves;

    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        List<Position> moves = getPossibleMoves(board);
        return moves.contains(kingPosition);
    }
}
