import java.util.*;

public class Queen extends Piece {

    public Queen(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'Q';
    }

    protected boolean addMoveOrStop (char x, int nextY, Board b, List <Position> moves) {
        Position next = new Position(x, nextY);
        Piece p = b.getPieceAt(next);

        if (p == null) {
            moves.add(next);
            return true;
            //continue in this direction
        } else {
            if (p.getColor() != this.getColor()) {
                moves.add(next);
            }
            return false;
        }
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        Position current = getPosition();
        char startX = current.getX();
        int startY = current.getY();

        //straight lines
        // up
        for (int nextY = startY + 1; nextY <= 8; nextY++) {
            if (!addMoveOrStop(startX, nextY, board, moves)) {
                break;
            }
        }

        // down
        for (int nextY = startY - 1; nextY >= 1; nextY--) {
            if (!addMoveOrStop(startX, nextY, board, moves)) {
                break;
            }
        }

        // right
        for (char nextX = (char)(startX + 1); nextX <= 'H'; nextX++) {
            if (!addMoveOrStop(nextX, startY, board, moves)) {
                break;
            }
        }

        // left
        for (char nextX = (char)(startX - 1); nextX >= 'A'; nextX--) {
            if (!addMoveOrStop(nextX, startY, board, moves)) {
                break;
            }
        }

        //diagonals
        char nextX;
        int nextY;

        // up-right
        nextX = (char)(startX + 1);
        nextY = startY + 1;
        while (nextX <= 'H' && nextY <= 8) {
            if (!addMoveOrStop(nextX, nextY, board, moves)) {
                break;
            }
            nextX++;
            nextY++;
        }

        // up-left
        nextX = (char)(startX - 1);
        nextY = startY + 1;
        while (nextX >= 'A' && nextY <= 8) {
            if (!addMoveOrStop(nextX, nextY, board, moves)) {
                break;
            }
            nextX--;
            nextY++;
        }

        // down-right
        nextX = (char)(startX + 1);
        nextY = startY - 1;
        while (nextX <= 'H' && nextY >= 1) {
            if (!addMoveOrStop(nextX, nextY, board, moves)) {
                break;
            }
            nextX++;
            nextY--;
        }

        // down-left
        nextX = (char)(startX - 1);
        nextY = startY - 1;
        while (nextX >= 'A' && nextY >= 1) {
            if (!addMoveOrStop(nextX, nextY, board, moves)) {
                break;
            }
            nextX--;
            nextY--;
        }

        return moves;
    }

    public boolean checkForCheck (Board board, Position kingPosition) {
        List<Position> moves = getPossibleMoves(board);
        return moves.contains(kingPosition);
    }


}
