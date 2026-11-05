package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class DataBaseTests {
    public DatabaseUserData databaseUserData = new DatabaseUserData();
    public DatabaseManager manger = new DatabaseManager();

    public static UserData userBob = new UserData("Bob", "password", "B0BTheGoat67@gmail.com");
    void addUserBob() throws DataAccessException{
        databaseUserData.createUser(userBob.username(), userBob.password(), userBob.email());
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

    @Test
    void testGetUserData() {
            assertDoesNotThrow( () -> {
                assertEquals(userBob.username(), databaseUserData.getUser(userBob.username()).username());
                assertEquals(userBob.email(), databaseUserData.getUser(userBob.username()).email());
            });
    }

    @Test
    void testMatchPassword() {
        assertDoesNotThrow( () -> {
            assertTrue(databaseUserData.matchPassword(userBob.username(), userBob.password()));
        });
        assertDoesNotThrow( () -> {
            assertFalse(databaseUserData.matchPassword(userBob.username(), "badPassword"));
        });
    }


}
