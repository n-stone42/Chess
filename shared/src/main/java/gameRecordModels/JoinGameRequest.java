package gameRecordModels;

public record JoinGameRequest(
        String playerColor,
        int gameID
) {
}