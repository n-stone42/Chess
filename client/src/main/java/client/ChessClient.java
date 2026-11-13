package client;


import UserRecordsModels.*;
import dataaccess.DataAccessException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class ChessClient  {

    private String sessionAuthToken;
    private ServerFacade serverFacade;

    public ChessClient(String url) throws DataAccessException {
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
                observe <GameID> - to watch a Chess game
                logout  - logs out current user
                quit - quits program
                help - list possible commands
                """;
    }

    public String register(String[] params) throws DataAccessException{
        if (params.length != 3) {
            throw new DataAccessException("Error finding parameters, expected <Username> <Client> <email>",
                    DataAccessException.Code.BadRequest);
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        try {
            if (isSignedIn()) {
                throw new DataAccessException("already registered", DataAccessException.Code.BadRequest);
            }
            RegisterResult result = serverFacade.registerUser(new RegisterRequest(username, password, email));
            sessionAuthToken = result.authToken();
            return username + " registered successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String login(String[] params) throws DataAccessException {
        if (params.length != 2) {
            throw new DataAccessException("error finding paramerts, expected <Username> <Password>",
                    DataAccessException.Code.BadRequest);
        }
        String username = params[0];
        String password = params[1];
        try {
            LoginResult result = serverFacade.login(new LoginRequest(username, password));
            sessionAuthToken = result.authToken();
            return username + " signed in successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String logout() {
        try {
            if (isSignedIn()) {
                LogoutResult result = serverFacade.logout(sessionAuthToken);
                sessionAuthToken = null;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return "logged out";
    }
}
