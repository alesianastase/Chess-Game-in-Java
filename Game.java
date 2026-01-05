import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game {
    private int id;
    private Board board;
    private Player player;
    private Player opponent;
    private List<Move> moves;
    private int currentPlayerId;

    public Game() {
        this.moves = new ArrayList<>();
        this.currentPlayerId = 0;
    }

    public Game(int id, Board b, Player p1, Player p2) {
        this.id = id;
        this.board = b;
        this.player = p1;
        this.opponent = p2;
        this.moves = new ArrayList<>();
        this.currentPlayerId = 0;
    }

    public int getCurrentPlayerId() {
        return this.currentPlayerId;
    }

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public Board getBoard () {
        return board;
    }

    public void setBoard (Board board) {
        this.board = board;
    }

    public Player getPlayer () {
        return player;
    }

    public Player getOpponent() {
        return opponent;
    }

    //incep un joc nou
    public void start() {
        if (board == null) {
            board = new Board();
        }

        //initializez tabla de sah
        board.initialize();

        if (moves == null) {
            moves = new ArrayList<>();
        } else {
            moves.clear();
        }

        if (player != null && opponent != null) {
            if (player.getColor() == Colors.WHITE) {
                currentPlayerId = 0;
            } else {
                currentPlayerId = 1; //computer
            }
        } else {
            currentPlayerId = 0; //default
        }
    }

    public void resume() {
        if (board == null) {
            board = new Board();
        }

        if (moves == null) {
            moves = new ArrayList<>();
        }

        if (currentPlayerId != 0 && currentPlayerId !=1) {
            currentPlayerId = 0;
        }
    }

    public void switchPlayer() {
        if (currentPlayerId == 1) {
            currentPlayerId = 0;
        } else {
            currentPlayerId = 1;
        }
    }

    public void addMove(Player p, Position from, Position to) {
        if (moves == null) {
            moves = new ArrayList<>();
        }

        Piece capturedPiece = board.getPieceAt(to);
        Move newMove = new Move(p.getColor(), from, to, capturedPiece);
        moves.add(newMove);
    }

    private Position findKingPosition(Colors color) {
        if (board == null) {
            return null;
        }

        for (ChessPair<Position, Piece> pair : board.getAllPieces()) {
            Position pos = pair.getKey();
            Piece piece = pair.getValue();

            if (piece instanceof King && piece.getColor() == color) {
                return pos;
            }
        }

        return null;
    }

    private boolean isKingInCheck(Colors kingColor) {
        Position kingPos = findKingPosition(kingColor);
        if (kingPos == null) {
            return false;
        }

        for (ChessPair<Position, Piece> pair : board.getAllPieces()) {
            Piece piece = pair.getValue();

            //only opponent pieces can attack this king
            if (piece.getColor() != kingColor) {
                if (piece.checkForCheck(board, kingPos)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasAnyLegalMove(Colors color) {
        if (board == null) {
            return false;
        }

        List<ChessPair<Position, Piece>> duplicate = new ArrayList<>();
        for (ChessPair<Position, Piece> pair : board.getAllPieces()) {
            duplicate.add(pair);
        }

        for (ChessPair<Position, Piece> pair : duplicate) {
            Position from = pair.getKey();
            Piece piece = pair.getValue();

            if (piece.getColor() != color) {
                continue;
            }

            List<Position> moves = piece.getPossibleMoves(board);
            for (Position to : moves) {
                if (!board.isValidMove(from, to)) {
                    continue;
                }

                //check if after move king remains in check
                boolean keepsSafe = isMoveKeepingKingSafe(from, to, color);

                if (keepsSafe) {
                    return true;
                }
            }
        }

        //no move exists that could take king out of check
        return false;
    }

    private boolean isMoveKeepingKingSafe(Position from, Position to, Colors color) {
        Piece moving = board.getPieceAt(from);
        if (moving == null) {
            return false;
        }

        Piece captured = board.getPieceAt(to);

        //remove pieces from positions
        board.removePiece(from);
        if (captured != null) {
            board.removePiece(to);
        }

        Position oldPos = moving.getPosition();
        moving.setPosition(to);
        board.addPiece(moving);

        //check if king is in check after move
        boolean kingStillInCheck = isKingInCheck(color);

        //going back to original status
        board.removePiece(to);
        moving.setPosition(oldPos);
        board.addPiece(moving);

        if (captured != null) {
            board.addPiece(captured);
        }

        //returns true if move keeps king safe
        return !kingStillInCheck;
    }

    public boolean checkForCheckMate() {
        if (board == null) {
            return false;
        }

        Colors white = Colors.WHITE;
        if (isKingInCheck(white) && !hasAnyLegalMove(white)) {
            return true;
        }

        Colors black = Colors.BLACK;
        if (isKingInCheck(black) && !hasAnyLegalMove(black)) {
            return true;
        }
        return false;
    }

}
