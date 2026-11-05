package dataaccess;

import org.junit.jupiter.api.Test;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class DataBaseTests {
    public DatabaseUserData dataBase = new DatabaseUserData();
    @Test
    void testCreateWithoutError() {
        assertDoesNotThrow(() -> {
            dataBase.makeDatabase();
            }, "making database failed " );
    }

}
