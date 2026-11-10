package GameRecordModels;

import java.util.ArrayList;

public record JoinGameRequest(
        String playerColor,
        int gameID
) {
}