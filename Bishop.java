import java.util.*;

public class Bishop extends Piece{

    public Bishop(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'B';
    }

    protected boolean addMoveOrStop(char x, int nextY, Board board, List<Position> moves) {
        Position next = new Position(x, nextY);
        Piece p = board.getPieceAt(next);

        if (p == null) {
            moves.add(next);
            return true; //continua in directia asta
        } else {
            if (p.getColor() != this.getColor()) {
                moves.add(next);
            }
            return false;
            //se opreste
        }
    }


    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>(); //result list
        Position pos = getPosition(); //current pos
        char x = pos.getX();
        int y = pos.getY();
        char nextX = (char)(x + 1);
        int nextY = y + 1;

        //possible moves up right diagonal
        while (nextX <= 'H' && nextY <= 8) {
            if (!addMoveOrStop(nextX, nextY, board, moves)) {
                break;
            }
            nextX++;
            nextY++;
        }

        nextX = (char)(x - 1);
        nextY = y + 1;

        //possible moves up left diagonal
        while (nextX >= 'A' && nextY <= 8) {
            if (!addMoveOrStop(nextX, nextY, board, moves)) {
                break;
            }
            nextX--;
            nextY++;
        }

        //possible moves down right diagonal
        nextX = (char)(x + 1);
        nextY = y - 1;
        while (nextX <= 'H' && nextY >= 1) {
            if (!addMoveOrStop(nextX, nextY, board, moves)) {
                break;
            }
            nextX++;
            nextY--;
        }

        //possible moves down left diagonal
        nextX = (char)(x - 1);
        nextY = y - 1;
        while (nextX >= 'A' && nextY >= 1) {
            if (!addMoveOrStop(nextX, nextY, board, moves)) {
                break;
            }
            nextX--;
            nextY--;
        }

        return moves;
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        List<Position> moves = getPossibleMoves(board);
        return moves.contains(kingPosition);
    }
}
