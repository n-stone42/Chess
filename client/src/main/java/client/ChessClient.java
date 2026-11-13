package client;


import dataaccess.DataAccessException;

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

        while (!evaluation.equals("quit")) { //if quit, is just breaks the loop
            String nextLine = scanner.nextLine();


            try {
                evaluation = eval(nextLine);
                System.out.println(evaluation);
            } catch (Exception exception) {
                System.out.println(exception.toString());
            }
        }
        System.out.println();
        return;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command;
            if (tokens.length > 0) {
                command = tokens[0];
            }
            else { command = "help";}

            return switch (command) {
                default -> "unknown command \n" + help();
                case "quit" -> "quit";
                case "help" -> help();
            };
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    public String help() {
        if (!isSignedIn()) {
            return """
                register <username> <password> - register a new user 
                login <Username> <password> - once a user is registered, login with that user
                help
                quit""";
        }
        return """
                create <GameName> - create a new chess game
                list - list chess games
                observe <GameID) - to watch a Chess game
                logout  - logs out current user
                quit - quits program
                help - list possible commands
                """;
    }
}
