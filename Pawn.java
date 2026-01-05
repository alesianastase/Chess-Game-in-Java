import java.util.*;

public class Pawn extends Piece{
    public Pawn(Colors color, Position position) {
        super(color, position);
    }

    public char type() {
        return 'P';
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        Position pos = getPosition();
        char x = pos.getX();
        int y = pos.getY();
        int direction, oneStepY;

        //if white, has to go upwards on board
        //if black, goes down
        if (getColor() == Colors.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        oneStepY = y + direction;
        if (oneStepY >= 1 && oneStepY <= 8) {
            Position oneStepPos = new Position(x, oneStepY);
            //can only move forward if square empty
            if(board.getPieceAt(oneStepPos) == null) {
                moves.add(oneStepPos);
            }
        }

        //decides which row pawn starts on, depending on color
        int startingRow;
        if (getColor() == Colors.WHITE) {
            startingRow = 2;
        } else {
            startingRow = 7;
        }

        //moves forward two squares on first move
        if (y == startingRow) {
            int twoStepY = y + 2 * direction;
            if (twoStepY >= 1 && twoStepY <= 8) {
                Position oneStepPos = new Position(x, oneStepY);
                Position twoStepPos = new Position(x, twoStepY);

                // both squares must be empty to move 2
                if (board.getPieceAt(oneStepPos) == null && board.getPieceAt(twoStepPos) == null) {
                    moves.add(twoStepPos);
                }
            }
        }

        //capture diagonally left
        int captureY = y + direction;
        char leftX = (char)(x - 1);
        if (leftX >= 'A' && leftX <= 'H' && captureY >= 1 && captureY <= 8) {
            Position leftPos = new Position(leftX, captureY);
            Piece p = board.getPieceAt(leftPos);

            if (p != null && p.getColor() != this.getColor()) {
                moves.add(leftPos);
            }
        }

        //capture diagonally right
        char rightX = (char)(x + 1);
        if (rightX >= 'A' && rightX <= 'H' && captureY >= 1 && captureY <= 8) {
            Position rightPos = new Position(rightX, captureY);
            Piece p = board.getPieceAt(rightPos);

            if (p != null && p.getColor() != this.getColor()) {
                moves.add(rightPos);
            }
        }
        return moves;
    }

    //diagonal forward attack
    public boolean checkForCheck(Board board, Position kingPosition) {
        Position pos = getPosition();
        char x = pos.getX();
        int y = pos.getY();
        int direction;
        if (getColor() == Colors.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        int attackY = y + direction;

        //left attack
        char leftX = (char)(x-1);
        if (leftX >= 'A' && leftX <= 'H' && attackY >= 1 && attackY <= 8) {
            if (kingPosition.equals(new Position(leftX, attackY))) {
                return true;
            }
        }

        // right attack
        char rightX = (char) (x + 1);
        if (rightX >= 'A' && rightX <= 'H' && attackY >= 1 && attackY <= 8) {
            if (kingPosition.equals(new Position(rightX, attackY))) {
                return true;
            }
        }
        return false;
    }
}
