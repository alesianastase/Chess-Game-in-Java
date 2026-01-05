public class ChessPair<K extends Comparable<K>, V> implements Comparable<ChessPair<K, V>> {
    private V value;
    private K key;

    public ChessPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public int compareTo(ChessPair<K, V> other) {
        if (this.key == null && other.key == null) {
            return 0;
        }
        if (this.key == null) {
            return -1;
        }
        if (other.key == null) {
            return 1;
        }

        return this.key.compareTo(other.key);
    }

    public String returnString() {
        return "Key: " + key + ", Value: " + value;

    }

}
