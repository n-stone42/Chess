package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class DataBaseTests {
    public DatabaseUserData databaseUserData = new DatabaseUserData();
    public DatabaseManager manger = new DatabaseManager();


    void addUserBob() throws DataAccessException{
        databaseUserData.createUser("Bob", "password", "B0BTheGoat67@gmail.com");
    }

    @Test
    void createThenDelateUserTable() {
        assertDoesNotThrow(() -> {
            databaseUserData.makeUserTable();
        });
        assertDoesNotThrow(() -> {
            databaseUserData.deleteUserTable();
        });
    }

    @Test
    void testCreateDBWithoutError() {
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

    @Test
    void createUserWithoutError() {

        assertDoesNotThrow( () -> {
            addUserBob();
        });
    }


}
