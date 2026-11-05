package dataaccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class DataBaseTests {
    public DatabaseUserData databaseUserData = new DatabaseUserData();
    public DatabaseManager manger = new DatabaseManager();

    @Test
    void testCreateWithoutError() {
        assertDoesNotThrow(() -> {
            manger.createDatabase();
            }, "making database failed " );
    }

    @Test
    void testCreateUserTable() {
        assertDoesNotThrow(() -> {
            databaseUserData.makeUserTable();
        });
    }

    @Test
    void testClearUserTableWithoutError() {
        assertDoesNotThrow(() -> {
            databaseUserData.clear();
        }, "clearing user data failed " );
    }

}
