package passoff.server;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.UserService;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    static public  MemoryUserDAO userMemory = new MemoryUserDAO();
    static public MemoryAuthDAO authMemory = new MemoryAuthDAO();
    static public UserService userService = new UserService(userMemory, authMemory);


    UserData User_Bob = new UserData("Bob", "password", "BobTheGOAT67@gmail.com");

    @BeforeEach
    public void reset() {
        userMemory.clear();
    }
    public UserService.RegisterResult registerBob() throws DataAccessException{
        UserService.RegisterRequest request= new UserService.RegisterRequest(User_Bob.username(), User_Bob.password(),User_Bob.email());
        return userService.register(request);
    }

    @Test
    public void CreateUserInMemory() {
            try {
            UserService.RegisterResult result = registerBob();
            String authToken = result.authToken();
            assertNotNull(authMemory.getAuth(authToken));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    @Test
    public void testLogin() {
        try {
            UserService.RegisterResult result = registerBob();
            UserService.LoginResult loginResult = userService.login(new UserService.LoginRequest(User_Bob.username(), User_Bob.password()));
            assertTrue(loginResult.username() == User_Bob.username() && loginResult.authToken() != result.authToken());
            assertEquals(authMemory.getAuth(loginResult.authToken()), new AuthData(loginResult.authToken(),User_Bob.username()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testLogout() {
        try {
            UserService.RegisterResult result = registerBob();
            UserService.LoginResult loginResult = userService.login(new UserService.LoginRequest(User_Bob.username(), User_Bob.password()));
            UserService.LogoutRequest logoutRequest = new UserService.LogoutRequest(loginResult.username(), loginResult.authToken());
            assertEquals(userService.logout(logoutRequest), new UserService.LogoutResult());
            assertThrows(DataAccessException.class,() -> authMemory.getAuth(loginResult.authToken()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
