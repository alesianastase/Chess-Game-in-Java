    import java.util.*;
    public class Player {
        String name;
        Colors color;
        private List<Piece> captured;
        private TreeSet<ChessPair<Position, Piece>> available;
        private int points;
        private Board board;

        public Player(String name, Colors color) {
            this.name = name;
            this.color = color;
            this.captured = new ArrayList<>();
            this.available = new TreeSet<>();
            this.points = 0;
        }

        public Colors getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public List<Piece> getCapturedPieces() {
            return new ArrayList<>(captured);
        }

        //set
        public void setCapturedPieces(List<Piece> captured) {
            this.captured = captured;
        }

        public List<ChessPair<Position, Piece>> getOwnedPieces() {
            List <ChessPair<Position, Piece>> result = new ArrayList<>();

            for (ChessPair<Position, Piece> pair : board.getAllPieces()) {
                Piece p = pair.getValue();
                if (p.getColor() == this.color) {
                    result.add(pair);
                }
            }
            return result;
        }

        public void addCapturedPiece(Piece p) {
            if (p != null) {
                captured.add(p);
            }
        }

        public void makeMove(Position from, Position to, Board board) throws InvalidMoveException {
            if (!board.isValidMove(from, to)) {
                throw new InvalidMoveException("Mutare invalida de la " + from + " la " + to + ".\n");
            }

            board.movePiece(from, to); // here we assume it's valid
        }

    }
