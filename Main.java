import java.util.*;
import input.JsonReaderUtil;
import input.Account;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.Writer;
import java.io.IOException;

public class Main {

    private List<User> users;
    private Map<Integer, Game> games;
    private User currentUser;
    private Scanner scanner;
    private Random random;

    public Main() {
        this.users = new ArrayList<>();
        this.games = new HashMap<>();
        this.currentUser = null;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void read() {
        System.out.println("Se incarca datele din fisierele JSON...");

        Path accountsPath = Paths.get("SURSE", "input", "accounts.json");
        Path gamesPath = Paths.get("SURSE", "input", "games.json");

        try {
            List<Account> jsonAccounts = JsonReaderUtil.readAccounts(accountsPath);
            Map<Long, input.Game> jsonGamesMap = JsonReaderUtil.readGamesAsMap(gamesPath);

            for (Account acc : jsonAccounts) {
                User user = new User(acc.getEmail(), acc.getPassword(), acc.getPoints());
                List<Integer> gameIds = acc.getGames();

                if (gameIds != null) {
                    for (int id : gameIds) {
                        //search for game
                        input.Game rawGameData = jsonGamesMap.get((long) id);

                        if (rawGameData != null) {
                            //check if game was already converted
                            Game logicGame = games.get(id);

                            //convert if not already converted
                            if (logicGame == null) {
                                logicGame = convertJsonGameToLogicGame(rawGameData, user);
                                if (logicGame != null) {
                                    games.put(id, logicGame);
                                }
                            }

                            if (logicGame != null) {
                                user.addGame(logicGame);
                            }
                        }
                    }
                }
                users.add(user);
            }
            System.out.println("Date încărcate cu succes: " + users.size() + " utilizatori.");

        } catch (Exception e) {
            System.out.println("Nu s-au putut încarca datele (sau fisierele lipsesc).");
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private Game convertJsonGameToLogicGame(input.Game data, User loggedUser) {
        try {
            //find players
            Player humanPlayer = null;
            Player opponentPlayer = null;

            //go through json players
            if (data.getPlayers() != null) {
                for (input.Player pData : data.getPlayers()) {
                    Colors col;
                    if (pData.getColor().equalsIgnoreCase("WHITE")) {
                        col = Colors.WHITE;
                    } else {
                        col = Colors.BLACK;
                    }
                    String email = pData.getEmail();
                    Player p = new Player(email, col);

                    if (email.equalsIgnoreCase("computer")) {
                        opponentPlayer = p;
                    } else {
                        if (humanPlayer == null) {
                            humanPlayer = p;
                        } else if (opponentPlayer == null) {
                            opponentPlayer = p;
                        }
                    }
                }
            }

            //if data is incomplete
            if (humanPlayer == null) {
                humanPlayer = new Player("Player 1", Colors.WHITE);
            }
            if (opponentPlayer == null) {
                opponentPlayer = new Player("Computer", Colors.BLACK);
            }

            //rebuild board
            Board board = new Board();

            if (data.getBoard() != null) {
                for (input.Piece pData : data.getBoard()) {
                    //parse the position
                    Position pos = parsePosition(pData.getPosition());
                    Colors color = pData.getColor().equalsIgnoreCase("WHITE") ? Colors.WHITE : Colors.BLACK;
                    String type = pData.getType();

                    //create the specific piece in game logic
                    Piece logicPiece = createSpecificPiece(type, color, pos);

                    if (logicPiece != null) {
                        board.addPiece(logicPiece);
                    }
                }
            }

            int gameId = (int) data.getId();
            Game game = new Game(gameId, board, humanPlayer, opponentPlayer);

            //set current turn
            String turnColor = data.getCurrentPlayerColor();
            if (turnColor != null) {
                Colors currentTurn = turnColor.equalsIgnoreCase("WHITE") ? Colors.WHITE : Colors.BLACK;
                if (humanPlayer.getColor() != currentTurn) {
                    game.switchPlayer(); //default=0;
                }
            }

            return game;

        } catch (Exception e) {
            System.out.println("Eroare conversie: " + e.getMessage());
            return null;
        }
    }

    private Piece createSpecificPiece(String type, Colors color, Position pos) {
        switch (type.toUpperCase()) {
            case "R": return new Rook(color, pos);
            case "N": return new Knight(color, pos);
            case "B": return new Bishop(color, pos);
            case "Q": return new Queen(color, pos);
            case "K": return new King(color, pos);
            case "P": return new Pawn(color, pos);
            default: return null;
        }
    }

    public void write() {
        System.out.println("Se salveaza starea curenta în fisiere JSON...");
        saveAccountsToFile();
        saveGamesToFile();
    }

    private void saveAccountsToFile() {
        JSONArray arr = new JSONArray();

        for (User u : users) {
            JSONObject obj = new JSONObject();
            obj.put("email", u.getEmail());
            obj.put("password", u.getPassword());
            obj.put("points", u.getPoints());

            //list of active games
            JSONArray gamesArray = new JSONArray();
            List<Game> activeGames = u.getActiveGames();
            if (activeGames != null) {
                for (Game g : activeGames) {
                    gamesArray.add(g.getId());
                }
            }
            obj.put("games", gamesArray);

            arr.add(obj);
        }

        Path accountsPath = Paths.get("SURSE", "input", "accounts.json");

        try (Writer w = Files.newBufferedWriter(accountsPath, StandardCharsets.UTF_8)) {
            w.write(arr.toJSONString());
        } catch (IOException e) {
            System.out.println("Eroare la scrierea in accounts.json: " + e.getMessage());
        }
    }

    private User findOwnerOfGame(Game game) {
        for (User u : users) {
            List<Game> active = u.getActiveGames();
            if (active != null && active.contains(game)) {
                return u;
            }
        }
        return null;
    }

    private void saveGamesToFile() {
        JSONArray arr = new JSONArray();

        for (Game g : games.values()) {
            JSONObject obj = new JSONObject();

            // id
            obj.put("id", g.getId());

            // players[]
            JSONArray playersArr = new JSONArray();

            //find user that owns game
            User owner = findOwnerOfGame(g);
            String ownerEmail = (owner != null) ? owner.getEmail() : "player";

            Player human = g.getPlayer();
            Player opponent = g.getOpponent();

            //human player
            JSONObject p1 = new JSONObject();
            p1.put("email", ownerEmail);
            p1.put("color", human.getColor().toString());
            playersArr.add(p1);

            //computer
            JSONObject p2 = new JSONObject();
            p2.put("email", "computer");
            p2.put("color", opponent.getColor().toString());
            playersArr.add(p2);

            obj.put("players", playersArr);

            // currentPlayerColor
            String currentColor;
            if (g.getCurrentPlayerId() == 0) {
                currentColor = human.getColor().toString();
            } else {
                currentColor = opponent.getColor().toString();
            }
            obj.put("currentPlayerColor", currentColor);

            // board[]
            JSONArray boardArr = new JSONArray();
            Board b = g.getBoard();
            for (ChessPair<Position, Piece> pair : b.getAllPieces()) {
                Piece p = pair.getValue();
                Position pos = pair.getKey();

                JSONObject pj = new JSONObject();
                pj.put("type", String.valueOf(p.type()));
                pj.put("color", p.getColor().toString());
                pj.put("position", pos.toString());
                boardArr.add(pj);
            }
            obj.put("board", boardArr);
            JSONArray movesArr = new JSONArray();
            obj.put("moves", movesArr);

            arr.add(obj);
        }

        Path gamesPath = Paths.get("src", "input", "games.json");

        try (Writer w = Files.newBufferedWriter(gamesPath, StandardCharsets.UTF_8)) {
            w.write(arr.toJSONString());
        } catch (IOException e) {
            System.out.println("Eroare la scrierea in games.json: " + e.getMessage());
        }
    }

    public User login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                this.currentUser = u;
                return u;
            }
        }
        return null;
    }

    public User newAccount(String email, String password) {
        if (email == null || password == null) {
            System.out.println("Email sau parola invalida.");
            return null;
        }

        for (User u : users) {
            if (u.getEmail().equals(email)) {
                System.out.println("Exista deja un cont cu acest email.");
                return null;
            }
        }

        //0 puncte initiale
        User newUser = new User(email, password, 0);
        users.add(newUser);
        this.currentUser = newUser;
        System.out.println("Cont creat cu succes pentru " + email + ".");
        write();
        return newUser;
    }

    public void run() {
        boolean appRunning = true;
        System.out.println("=== CHESS ===");

        while (appRunning) {
            if (currentUser == null) {
                handleAuthMenu();
            } else {
                handleMainMenu();
            }
        }
    }

    private void handleAuthMenu() {
        try {
            System.out.println("\n1) Autentificare\n2) Cont Nou\n3) Iesire");
            System.out.print("Nr. comenzii dorite: ");
            String cmd = scanner.nextLine().trim();

            if (cmd.equals("3")) {
                write();
                System.exit(0);
            }

            if (!cmd.equals("1") && !cmd.equals("2")) {
                throw new InvalidCommandException("Comanda invalida in meniul de autentificare.");
            }

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Parola: ");
            String pass = scanner.nextLine().trim();

            if (cmd.equals("1")) {
                if (login(email, pass) != null) {
                    System.out.println("Logat cu succes!");
                } else {
                    System.out.println("Date incorecte.");
                }
            } else {
                newAccount(email, pass);
            }

        } catch (InvalidCommandException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleMainMenu() {
        try {
            System.out.println("\nUser: " + currentUser.getEmail() + " ---");
            System.out.println("1) Start Joc Nou");
            System.out.println("2) Jocuri Active");
            System.out.println("3) Delogare");
            System.out.print("Introdu numar comanda dorita: ");
            String cmd = scanner.nextLine().trim();

            switch (cmd) {
                case "1":
                    startNewGame();
                    break;

                case "2":
                    handleActiveGames();
                    break;

                case "3":
                    currentUser = null;
                    break;

                default:
                    throw new InvalidCommandException("Comanda invalida in meniul principal.");
            }

        } catch (InvalidCommandException e) {
            System.out.println(e.getMessage());
        }
    }

    private void startNewGame() {
        System.out.print("Alias: ");
        String alias = scanner.nextLine();
        System.out.print("Culoare (WHITE/BLACK): ");
        String cInput = scanner.nextLine().toUpperCase();
        Colors pColor;
        Colors cColor;

        if (cInput.equalsIgnoreCase("BLACK")) {
            pColor = Colors.BLACK;
            cColor = Colors.WHITE;
        } else {
            pColor = Colors.WHITE;
            cColor = Colors.BLACK;
        }

        Player human = new Player(alias, pColor);
        Player computer = new Player("Computer", cColor);
        Board board = new Board();

        int id = games.size() + 1;
        Game game = new Game(id, board, human, computer);

        currentUser.addGame(game);
        games.put(id, game);

        game.start();
        playGame(game);
    }

    private void handleActiveGames() {
        List<Game> active = currentUser.getActiveGames();
        if (active.isEmpty()) {
            System.out.println("Niciun joc activ.");
            return;
        }

        System.out.println("Jocuri salvate:");
        for (Game g : active) {
            System.out.println("ID: " + g.getId() + " vs " + g.getOpponent().getName());
        }

        System.out.print("ID joc sau 'back': ");
        String in = scanner.nextLine();
        if(in.equals("back")) {
            return;
        }

        try {
            int id = Integer.parseInt(in);
            Game g = games.get(id);
            if(g != null && active.contains(g)) {
                g.resume();
                playGame(g);
            } else {
                throw new InvalidCommandException("Nu exista un joc activ cu ID-ul " + id + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID invalid, trebuie sa fie un numar.");
        } catch (InvalidCommandException e) {
            System.out.println(e.getMessage());
        }
    }

    private void playGame(Game game) {
        boolean running = true;

        while (running) {
            printBoard(game.getBoard());

            if (game.checkForCheckMate()) {
                handleEndGame(game, "CHECKMATE");
                break;
            }

            Player currentPlayer;
            boolean isComputerTurn;

            //0 = player, 1 = opponent(computer)
            if (game.getCurrentPlayerId() == 0) {
                currentPlayer = game.getPlayer();
                isComputerTurn = false;
            } else {
                currentPlayer = game.getOpponent();
                isComputerTurn = true;
            }

            System.out.println("La mutare: " + currentPlayer.getName() + " (" + currentPlayer.getColor() + ")");

            if (isComputerTurn) {
                doComputerMove(game, currentPlayer);

                if (game.checkForCheckMate()) {
                    printBoard(game.getBoard());
                    handleEndGame(game, "CHECKMATE");
                    running = false;
                } else {
                    game.switchPlayer();
                }

            } else {
            // user
            try {
                System.out.println("Optiuni: ");
                System.out.println("1) Arata mutari posibile");
                System.out.println("2) Efectueaza mutare (ex: B2-B4)");
                System.out.println("3) Vezi piesele capturate");
                System.out.println("4) Resign");
                System.out.println("5) Exit");
                System.out.print("Scrie nr. optiunii pe care vrei sa o alegi: >> ");

                String input = scanner.nextLine().trim();

                // exit
                if (input.equalsIgnoreCase("5")) {
                    System.out.println("Joc salvat.");
                    write();
                    running = false;
                }

                // resign
                else if (input.equalsIgnoreCase("4")) {
                    handleEndGame(game, "RESIGN");
                    running = false;
                }

                // vezi piese capturate
                else if (input.equalsIgnoreCase("3")) {
                    showCapturedPieces(currentPlayer);
                }

                // possible moves
                else if (input.equals("1")) {
                    System.out.print("Scrie pozitia piesei (ex: B2): >> ");
                    String posStr = scanner.nextLine().trim().toUpperCase();
                    try {
                        Position pos = parsePosition(posStr);
                        Piece p = game.getBoard().getPieceAt(pos);

                        if (p != null && p.getColor() == currentPlayer.getColor()) {
                            List<Position> moves = p.getPossibleMoves(game.getBoard());
                            System.out.println("\n--- Analiza mutari ---");
                            printBoard(game.getBoard());
                            System.out.println("Mutari posibile pentru " + posStr + ": " + moves);
                            System.out.println("Apasa [ENTER] pentru a continua...");
                            scanner.nextLine();
                        } else {
                            System.out.println("Nu exista o piesa de-a ta la pozitia " + posStr);
                        }
                    } catch (Exception e) {
                        System.out.println("Pozitie invalida.");
                    }
                }

                // make move
                else if (input.equals("2")) {

                    System.out.print("Scrie mutarea pe care vrei sa o faci (ex: B2-B4): >> ");
                    String inputLine = scanner.nextLine().trim();

                    if (inputLine.matches("^[A-Ha-h][1-8]-[A-Ha-h][1-8]$")) {
                        try {
                            String[] parts = inputLine.split("-");
                            Position from = parsePosition(parts[0]);
                            Position to = parsePosition(parts[1]);

                            Board board = game.getBoard();
                            Piece p = board.getPieceAt(from);

                            if (p != null && p.getColor() == currentPlayer.getColor()) {
                                //check if there is any piece to be captured
                                Piece captured = board.getPieceAt(to);
                                currentPlayer.makeMove(from, to, board);

                                //add to captured list
                                if (captured != null) {
                                    currentPlayer.addCapturedPiece(captured);
                                }

                                game.addMove(currentPlayer, from, to);

                                if (game.checkForCheckMate()) {
                                    handleEndGame(game, "CHECKMATE");
                                    running = false;
                                } else {
                                    game.switchPlayer();
                                }
                            } else {
                                throw new InvalidMoveException("Nu poti muta acea piesa.");
                            }
                        } catch (InvalidMoveException e) {
                            System.out.println("Mutare invalida! " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println("Eroare. " + e.getMessage());
                        }
                    } else {
                        throw new InvalidMoveException("\n[CAUZA]: Format mutare invalid.\nMutarea trebuie sa fie pe o tabla 8x8, A-H. ( Exemplu: B2-B4 )\n");
                    }
                }

                // orice alt input in meniu
                else {
                    throw new InvalidCommandException("Comanda nerecunoscuta: " + input);
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            } catch (InvalidMoveException e) {
                System.out.println("\nMutare invalida! " + e.getMessage());
            }
        }

    }
    }



    private void doComputerMove(Game game, Player computer) {
        System.out.println("Oponentul se gandeste...");
        Board board = game.getBoard();
        List<String> validMoves = new ArrayList<>();

        //scans the entire chessboard and finds own pieces, checks all posible squares
        //it could go to, keeps only allowed moves
        for (ChessPair<Position, Piece> pair : board.getAllPieces()) {
            Piece p = pair.getValue();
            if (p.getColor() == computer.getColor()) {
                List<Position> targets = p.getPossibleMoves(board);
                for (Position t : targets) {
                    if (board.isValidMove(p.getPosition(), t)) {
                        validMoves.add(p.getPosition().toString() + "-" + t.toString());
                    }
                }
            }
        }

        if (!validMoves.isEmpty()) {
            String chosen = validMoves.get(random.nextInt(validMoves.size()));
            String[] parts = chosen.split("-");
            Position from = parsePosition(parts[0]);
            Position to = parsePosition(parts[1]);

            try {
                //check which piece is at pos to before move
                Piece captured = board.getPieceAt(to);

                //make move
                computer.makeMove(from, to, board);

                //add to computer capture
                if (captured != null) {
                    computer.addCapturedPiece(captured);
                }

                game.addMove(computer, from, to);
                System.out.println("Oponentul a efectuat o mutare: " + chosen);
            } catch (Exception e) {
                System.out.println("Eroare computer: " + e.getMessage());
            }
        } else {
            System.out.println("Computerul nu are mutari.");
        }
    }

    private int calculatePointsFromCaptures(Player player) {
        int y = 0;
        if (player.getCapturedPieces() != null) {
            for (Piece p : player.getCapturedPieces()) {
                switch (p.type()) {
                    case 'Q':
                        y += 90;
                        break; //Queen
                    case 'R':
                        y += 50;
                        break; //Rook
                    case 'B':
                        y += 30;
                        break; //Bishop
                    case 'N':
                        y += 30;
                        break; //Knight
                    case 'P':
                        y += 10;
                        break; //Pawn
                    default:
                        break;
                }
            }
        }
        return y;
    }

    private void showCapturedPieces(Player player) {
        List<Piece> captured = player.getCapturedPieces();

        if (captured == null || captured.isEmpty()) {
            System.out.println("Nu ai capturat nicio piesa pana acum.");
            return;
        }

        System.out.println("\n--- Piese capturate de " + player.getName() + " (" + player.getColor() + ") ---");
        int total = 0;

        for (Piece p : captured) {
            int val = 0;
            switch (p.type()) {
                case 'Q':
                    val = 90;
                    break;
                case 'R':
                    val = 50;
                    break;
                case 'B':
                case 'N':
                    val = 30;
                    break;
                case 'P':
                    val = 10;
                    break;
                default:
                    break;
            }

            String colorText;

            if (p.getColor() == Colors.WHITE) {
                colorText = "alb";
            } else {
                colorText = "negru";
            }
            System.out.println("- " + p.type() + " (" + colorText + "), +" + val + "p");
            total += val;
        }

        System.out.println("Total puncte din capturi: " + total + "p");
        System.out.println("--------------------------------------------\n");
    }

    private void handleEndGame(Game game, String reason) {
        System.out.println("Joc terminat: " + reason);

        //X = current player points
        int currentPts = currentUser.getPoints();

        //Y = accumulated points
        Player humanPlayer = game.getPlayer();
        int capturePts = calculatePointsFromCaptures(humanPlayer);

        int finalPts = currentPts;

        if (reason.equals("CHECKMATE")) {
            //checks who won
            boolean humanWon;
            if (game.getCurrentPlayerId() == 0) {
                humanWon = true; //user
            } else {
                humanWon = false; //computer
            }

            if (humanWon) {
                //X_nou = X + Y + 300
                finalPts = currentPts + capturePts + 300;
                System.out.println("VICTORIE (CHECKMATE)! (+300p bonus + " + capturePts + "p capturi)");
            } else {
                //X_nou = X + Y - 300
                finalPts = currentPts + capturePts - 300;
                System.out.println("INFRANGERE (CHECKMATE). (-300p penalizare + " + capturePts + "p capturi)");
            }

        } else if (reason.equals("RESIGN")) {
            //X_nou = X + Y - 150
            finalPts = currentPts + capturePts - 150;
            System.out.println("AI RENUNTAT. (-150p penalizare + " + capturePts + "p capturi)");
        }

        currentUser.setPoints(finalPts);
        currentUser.removeGame(game);
        games.remove(game.getId());
        write();
        System.out.println("Total puncte cont (X): " + finalPts);
    }

    private Position parsePosition(String s) {
        String clean = s.trim().toUpperCase();
        return new Position(clean.charAt(0), Integer.parseInt(clean.substring(1)));
    }

    private void printBoard(Board board) {
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " ");
            for (char col = 'A'; col <= 'H'; col++) {
                Position pos = new Position(col, row);
                Piece p = board.getPieceAt(pos);
                if (p == null) System.out.print(" ... ");
                else {
                    String c;
                    if (p.getColor() == Colors.WHITE) {
                        c = "W";
                    } else {
                        c = "B";
                    }
                    System.out.print(" " + p.type() + "-" + c.charAt(0) + " ");
                }
            }
            System.out.println(" |");
        }
        System.out.println("--------------------------------------------");
        System.out.println("    A    B    C    D    E    F    G    H\n");
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.read();
        app.run();
    }
}