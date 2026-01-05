import java.util.*;

public class Rook extends Piece{

    public Rook(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'R';
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

        //possible moves up
        for (int nextY = y + 1; nextY <= 8; nextY++) {
           if (!addMoveOrStop(x, nextY, board, moves)) {
               break;
           }
        }

        //possible moves down
        for (int nextY = y-1; nextY >= 1; nextY--) {
            if (!addMoveOrStop(x, nextY, board, moves)) {
                break;
            }
        }

        //right
        for (char nextX = (char)(x + 1) ; nextX <= 'H'; nextX++) {
            if (!addMoveOrStop(nextX, y, board, moves)) {
                break;
            }
        }

        //left
        for (char nextX = (char)(x - 1) ; nextX >= 'A'; nextX--) {
            if (!addMoveOrStop(nextX, y, board, moves)) {
                break;
            }
        }

        return moves;
    }

    public boolean checkForCheck(Board board, Position kingPosition) {
        List<Position> moves = getPossibleMoves(board);
        return moves.contains(kingPosition);
    }
}
