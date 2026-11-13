package gameRecordModels;

import model.ModifiedGameData;

import java.util.ArrayList;

public record ListGameResult(
        ArrayList<ModifiedGameData> games) {
}
