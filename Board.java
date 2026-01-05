import java.util.*;

public class Board {
    private TreeSet<ChessPair<Position, Piece>> pieces;

    public Board() {
        pieces = new TreeSet<>();
    }

    public void addPiece(Piece piece) {
        pieces.add(new ChessPair<>(piece.getPosition(), piece));
    }

    public void initialize() {
        addPiece(new Rook(Colors.WHITE, new Position('A', 1)));
        addPiece(new Rook(Colors.WHITE, new Position('H', 1)));

        addPiece(new Bishop(Colors.WHITE, new Position('C', 1)));
        addPiece(new Bishop(Colors.WHITE, new Position('F', 1)));

        addPiece(new Rook(Colors.BLACK, new Position('A', 8)));
        addPiece(new Rook(Colors.BLACK, new Position('H', 8)));

        addPiece(new Bishop(Colors.BLACK, new Position('C', 8)));
        addPiece(new Bishop(Colors.BLACK, new Position('F', 8)));

        addPiece(new Queen(Colors.WHITE, new Position('D', 1)));
        addPiece(new Queen(Colors.BLACK, new Position('D', 8)));

        addPiece(new Knight(Colors.WHITE, new Position('B', 1)));
        addPiece(new Knight(Colors.WHITE, new Position('G', 1)));

        addPiece(new Knight(Colors.BLACK, new Position('B', 8)));
        addPiece(new Knight(Colors.BLACK, new Position('G', 8)));

        addPiece(new King(Colors.WHITE, new Position('E', 1)));
        addPiece(new King(Colors.BLACK, new Position('E', 8)));

        //white pawns
        for (char x = 'A'; x <= 'H'; x++) {
            Position pos = new Position(x, 2);
            Pawn pawn = new Pawn(Colors.WHITE, pos);
            addPiece(pawn);
        }

        //black pawns
        for (char x = 'A'; x <= 'H'; x++) {
            Position pos = new Position(x, 7);
            Pawn pawn = new Pawn(Colors.BLACK, pos);
            addPiece(pawn);
        }
    }

    public void removePiece(Position pos) {
        removePair(pos);
    }

    private void removePair (Position pos) {
        ChessPair<Position, Piece> toRemove = null;

        for (ChessPair<Position, Piece> pair : pieces) {
            Position key = pair.getKey();

            if (key.equals(pos)) {
                toRemove = pair;
                break;
            }
        }

        if (toRemove != null) {
            pieces.remove(toRemove);
        }
    }

    public void movePiece(Position from, Position to) throws InvalidMoveException {
        if (!isValidMove(from, to)) {
            throw new InvalidMoveException("Mutare invalida de la " + from + " la " + to + ".");
        }

        Piece pieceFrom = getPieceAt(from);
        Piece pieceTo = getPieceAt(to);

        removePair(from);

        if (pieceTo != null) {
            removePair(to);
        }

        //move piece to new pos
        pieceFrom.setPosition(to);
        pieces.add(new ChessPair<>(to, pieceFrom));

        //pawn promotion rule check
        if (pieceFrom instanceof Pawn) {
            int row = to.getY();
            boolean whitePromo, blackPromo;

            if (pieceFrom.getColor() == Colors.WHITE && row == 8) {
                whitePromo = true;
            } else {
                whitePromo = false;
            }

            if (pieceFrom.getColor() == Colors.BLACK && row == 1) {
                blackPromo = true;
            } else {
                blackPromo = false;
            }

            if (whitePromo || blackPromo) {
                Scanner sc = new Scanner(System.in);
                System.out.println("Pawn promotion. Choose piece: (Q)ueen,\n (R)ook, \n(B)ishop,\n K(N)ight");
                //reads user input, removes spaces and converts to upper case
                String choice = sc.nextLine().trim().toUpperCase();
                removePair(to);

            Piece promoted;
            switch (choice) {
                case "R":
                    promoted = new Rook(pieceFrom.getColor(), to);
                    break;
                case "B":
                    promoted = new Bishop(pieceFrom.getColor(), to);
                    break;
                case "N":
                    promoted = new Knight(pieceFrom.getColor(), to);
                    break;
                default:
                    promoted = new Queen(pieceFrom.getColor(), to);
                    break;
            }
            pieces.add(new ChessPair<>(to, promoted));
        }
    }
}

    public Collection<ChessPair<Position, Piece>> getAllPieces() {
        return pieces;
    }

    public Piece getPieceAt(Position pos) {
        for (ChessPair<Position, Piece> pair : pieces) {
            Position key = pair.getKey();
            Piece piece = pair.getValue();
            if (key.equals(pos)) {
                return piece;
            }
        }
        return null;
    }

    private boolean isInsideBoard(Position pos) {
        char x = pos.getX();
        int y = pos.getY();

        if ((x>='A' && x<= 'H') && (y >= 1 && y<=8))
            return true;

        return false;
    }

    public boolean isValidMove(Position from, Position to) {
        //check if it is in chess board boundaries
        if(!isInsideBoard(from) || !isInsideBoard(to)) {
            return false;
        }

        Piece piece = getPieceAt(from);
        if (piece == null) {
            return false;
        }

        //moves will have all possible spaces we can move to
        List<Position> moves = piece.getPossibleMoves(this);
        //checks if pos where we want to move to is inside the list of possible positions
        if (!moves.contains(to)) {
            return false;
        }
        return true;
    }

}
