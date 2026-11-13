package client;


import gameRecordModels.CreateGameRequest;
import gameRecordModels.CreateGameResult;
import gameRecordModels.JoinGameResult;
import gameRecordModels.ListGameResult;
import userRecordsModels.*;
import chess.ChessBoard;
import chess.ChessGame;
import dataAccessException.dataAccessException;
import model.ModifiedGameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class ChessClient  {

    private String sessionAuthToken;
    private ServerFacade serverFacade;
    private DrawBoard drawBoard = new DrawBoard();

    public ChessClient(String url) throws dataAccessException {
        serverFacade = new ServerFacade(url);
    }

    public boolean isSignedIn() {
        return (sessionAuthToken != null);
    }

    public void read() { // will read and print the needed statement
        System.out.println("welcome to the CS240 Chess Server, check it out mate");
        System.out.println(help());

        Scanner scanner =  new Scanner(System.in);
        String evaluation = "";
        System.out.print(basicPrint());
        while (!evaluation.equals("quit")) { //if quit, is just breaks the loop
            String nextLine = scanner.nextLine();
            try {
                evaluation = eval(nextLine);
                System.out.println(evaluation);
                System.out.print(basicPrint());
            } catch (Exception exception) {
                System.out.println(exception.toString());
            }
        }
        System.out.println();
        return;
    }

    private String basicPrint() {
        if (isSignedIn()) {
            return "[logged in] >>>: ";
        }
        return "[logged out] >>>: ";
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command;
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (tokens.length > 0) {
                command = tokens[0];
            }
            else { command = "help";}

            return switch (command) {
                default -> "unknown command \n" + help();
                case "quit" -> "quit";
                case "help" -> help();
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "list" -> list();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> viewGame(params);
            };
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    public String help() {
        if (!isSignedIn()) {
            return """
                register <username> <password> <email> - register a new user
                login <Username> <password> - once a user is registered, login with that user
                help
                quit""";
        }
        return """
                create <GameName> - create a new chess game
                list - list chess games
                join <gameID> <White or Black>
                observe <GameID> - to watch a Chess game
                logout  - logs out current user
                quit - quits program
                help - list possible commands
                """;
    }

    public String register(String[] params) throws dataAccessException {
        if (params.length != 3) {
            throw new dataAccessException("Error finding parameters, expected <Username> <Client> <email>",
                    dataAccessException.Code.BadRequest);
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        try {
            if (isSignedIn()) {
                throw new dataAccessException("already registered", dataAccessException.Code.BadRequest);
            }
            RegisterResult result = serverFacade.registerUser(new RegisterRequest(username, password, email));
            sessionAuthToken = result.authToken();
            return username + " registered successfully";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String login(String[] params) throws dataAccessException {
        if (params.length != 2) {
            throw new dataAccessException("error finding paramerts, expected <Username> <Password>",
                    dataAccessException.Code.BadRequest);
        }
        String username = params[0];
        String password = params[1];
        try {
            LoginResult result = serverFacade.login(new LoginRequest(username, password));
            sessionAuthToken = result.authToken();
            return username + " signed in successfully";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String logout() {
        try {
            if (isSignedIn()) {
                LogoutResult result = serverFacade.logout(sessionAuthToken);
                sessionAuthToken = null;
            }
            else {throw new dataAccessException("Not logged in, must be logged in first to logout",
                    dataAccessException.Code.BadRequest); }

        } catch (dataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "logged out";
    }

    public String list() throws dataAccessException {
        if (isSignedIn()) {
            try {
                int gameNumber =1;
                String output ="";
                ListGameResult result = serverFacade.listGame(sessionAuthToken);
                ArrayList<ModifiedGameData> gameList = result.games();
                for (ModifiedGameData gameData: gameList) {
                    output += "Game " + gameNumber++ + " gameID=" + gameData.gameID()
                            + " Player of White: " + gameData.whiteUsername() + " Player of Black: " + gameData.blackUsername() + "\n";
                }
                return output;
            } catch (dataAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        } throw new dataAccessException("not signed in", dataAccessException.Code.Unathurized);
    }

    public String createGame(String[] params) throws dataAccessException {
        if (params.length != 1) {
            throw new dataAccessException("error finding parameters, expected <gameName>",
                    dataAccessException.Code.BadRequest);
        }
        if (isSignedIn()) {
            try {
                CreateGameResult result = serverFacade.createGame(new CreateGameRequest(params[0]) , sessionAuthToken);
                return "Created game:" + params[0];
            } catch (Exception exception) {
                throw new RuntimeException(exception.getMessage());
            }
        } throw new dataAccessException("not signed in", dataAccessException.Code.Unathurized);
    }

    public String joinGame(String[] params) throws dataAccessException {
        if (!isSignedIn()) {
            throw new dataAccessException("not signed in", dataAccessException.Code.Unathurized);
        }
        if (params.length != 2) {
            throw new dataAccessException("error finding parameters, expected <gameID> playerColor <White or Black>",
                    dataAccessException.Code.BadRequest);
        }
        try {
            try {
                int gameID = Integer.parseInt(params[0]);
                JoinGameResult result = serverFacade.joinGame(sessionAuthToken, params[1], gameID );
                String output =  "joined game:" + gameID + "\n";
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                if (params[1].toLowerCase(Locale.ROOT).equals("white")) {
                    output += drawBoard.toString(board, ChessGame.TeamColor.WHITE);
                }
                else {
                    output += drawBoard.toString(board, ChessGame.TeamColor.BLACK);
                }
                return output;
            } catch (NumberFormatException exception) {
                throw new dataAccessException("expected number for gameID, got " + params[0],
                    dataAccessException.Code.BadRequest);}
        } catch (dataAccessException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public String viewGame(String[] params) throws dataAccessException {
        if (!isSignedIn()) {
            throw new dataAccessException("not signed in", dataAccessException.Code.Unathurized);
        }
        if (params.length != 1) {
            throw new dataAccessException("error finding parameters, expected <gameID>",
                    dataAccessException.Code.BadRequest);
        }
        try {
            int gameID = Integer.parseInt(params[0]);
            ListGameResult result = serverFacade.listGame(sessionAuthToken);
            ArrayList<ModifiedGameData> gameList = result.games();
            for (ModifiedGameData gameData: gameList) {
                if (gameData.gameID() == gameID) {
                    ChessBoard board = new ChessBoard();
                    board.resetBoard();
                    return drawBoard.toString(board, ChessGame.TeamColor.WHITE);
                }
            }
            throw new dataAccessException("game ID " + String.valueOf(gameID) + " not found", dataAccessException.Code.BadRequest);

        } catch (NumberFormatException exception) {
            throw new dataAccessException("Error : expected number for gameID, got " + params[0],
                    dataAccessException.Code.BadRequest);}
    }
}
