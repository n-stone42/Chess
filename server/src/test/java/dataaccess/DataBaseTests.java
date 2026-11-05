package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class DataBaseTests {
    public DatabaseUserData databaseUserData = new DatabaseUserData();
    public DatabaseAuthData databaseAuthData = new DatabaseAuthData();
    public DatabaseManager manger = new DatabaseManager();

    public static UserData userBob = new UserData("Bob", "password", "B0BTheGoat67@gmail.com");

    public AuthData testAuthData = new AuthData("token", userBob.username());
    void addUserBob() throws DataAccessException{
        databaseUserData.createUser(userBob.username(), userBob.password(), userBob.email());
    }

    void addAuthData() throws DataAccessException{
        databaseAuthData.createAuth(testAuthData.authToken(), userBob.username());
    }

    @BeforeEach
    void setup()  throws DataAccessException {
        testCreateDBWithoutError();
        testClearTables();
        testCreateTables();
    }

    @Test
    void testCreateTables() {
        assertDoesNotThrow(() -> {
            databaseUserData.makeUserTable();
        });
        assertDoesNotThrow(() -> {
            databaseAuthData.makeAuthDataTable();
        });
    }

    @Test
    void testCreateDBWithoutError() {
        assertDoesNotThrow(() -> {
            manger.createDatabase();
            }, "making database failed " );
    }

    @Test
    void createUserWithoutError() {
        assertDoesNotThrow( () -> {
            addUserBob();
        });
    }

    @Test
    void testGetUserData() throws DataAccessException{
        addUserBob();
        assertDoesNotThrow( () -> {
            assertEquals(userBob.username(), databaseUserData.getUser(userBob.username()).username());
            assertEquals(userBob.email(), databaseUserData.getUser(userBob.username()).email());
        });
    }

    @Test
    void testMatchPassword()  throws DataAccessException{
        addUserBob();
        assertDoesNotThrow( () -> {
            assertTrue(databaseUserData.matchPassword(userBob.username(), userBob.password()));
        });
        assertDoesNotThrow( () -> {
            assertFalse(databaseUserData.matchPassword(userBob.username(), "badPassword"));
        });
    }

    @Test
    void testGetAuthData() {
        assertDoesNotThrow( () -> {
            addAuthData();
        });
        assertDoesNotThrow( () -> {
            assertEquals(databaseAuthData.getAuth("token"), testAuthData);
        });
    }
    @Test
    void testClearTables() {
        assertDoesNotThrow(() -> {
            databaseUserData.clear();
        });
        assertDoesNotThrow(() -> {
            databaseAuthData.clear();
        });
    }

}
