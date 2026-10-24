package passoff.server;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;
import dataaccess.UserDataAccess;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTests {

    public MemoryUserDAO UserMemory = new MemoryUserDAO();
    public MemoryAuthDAO AuthMemory = new MemoryAuthDAO();
    public UserService userService = new UserService(UserMemory, AuthMemory);
    UserData User_Bob = new UserData("Bob", "password", "BobTheGOAT67@gmail.com");

    @Test
    public void CreateUserInMemory() {
            try {
            UserService.RegisterRequest request= new UserService.RegisterRequest(User_Bob.username(), User_Bob.password(),User_Bob.email());
            UserService.RegisterResult result = userService.register(request);
            System.out.print(result);
            String authToken = result.authToken();
            assertNotNull(AuthMemory.getAuth(authToken, User_Bob.username()));
            }
            catch (Exception e) {
                throw new RuntimeException(e);

            }
        }

}
