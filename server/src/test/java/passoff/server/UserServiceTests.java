package passoff.server;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;
import dataaccess.UserDataAccess;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class UserServiceTests {

    static MemoryUserDAO UserMemory = new MemoryUserDAO();
    static MemoryAuthDAO AuthMemory = new MemoryAuthDAO();
    static final UserService userService = new UserService(UserMemory, AuthMemory);
    UserData User_Bob = new UserData("Bob", "password", "BobTheGOAT67@gmail.com");

    @Test
    public void clearUserData() throws DataAccessException {
//        DataAccess test = DataAccess;
        userService.register(new UserService.RegisterRequest(User_Bob.username(), User_Bob.password(), User_Bob.email()));
//        assertThrows(DataAccessException.class, () -> Data_Access.clear());
    }
}
