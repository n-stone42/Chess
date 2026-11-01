package handlers;

import service.GameService;

public class GameHandler {

    public GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }
}
