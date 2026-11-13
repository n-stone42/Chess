package gamerecord;

public record JoinGameRequest(
        String playerColor,
        int gameID
) {
}