package Handlers;

import service.GameService;
import service.UserService;

public class GameHandler {

    public GameService gameService;
    public GameHandler(GameService gameService) {
        gameService = gameService;
    }
//
//    public UserService userService;
//    public UserHandler(UserService userService) {
//        userService = userService;
//    }
}
