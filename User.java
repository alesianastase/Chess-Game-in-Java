import java.util.*;

public class User {
    private String email;
    private String password;
    private List<Game> games;
    private int points;

    public User (String email, String password, int points) {
        this.email = email;
        this.password = password;
        this.points = points;
        this.games = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Game> getGames() {
        return games;
    }

    public void addGame(Game game) {
        if (game != null) {
            games.add(game);
        }
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

    public List<Game> getActiveGames(){
        return games;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
