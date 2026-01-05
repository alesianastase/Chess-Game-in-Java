public class Position implements Comparable<Position> {
    private char x;
    private int y;

    public Position(char x, int y) {
        this.x = x;
        this.y = y;
    }

    public char getX() {
        return x;
    }

    public int getY(){
        return y;
    }

    public int compareTo(Position other) {
        if (this.y != other.y) {
            return Integer.compare(this.y, other.y);
        }

        return Character.compare(this.x, other.x);
    }

    public boolean equals(Object o){
        if (this == o)
            return true;

        //verific daca e null sau daca nu sunt din aceeasi clasa
        if (o == null || this.getClass() != o.getClass())
            return false;

        Position other = (Position) o;

        if (this.x == other.x && this.y == other.y)
            return true;

        return false;
    }

    public String toString() {
        return "" + this.x + this.y;
    }

}
