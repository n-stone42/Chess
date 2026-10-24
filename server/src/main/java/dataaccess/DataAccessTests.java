package dataaccess;
import dataaccess.DAOs;
import dataaccess.DataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class DataAccessTests {
//    static final PetService service = new PetService(new MemoryDataAccess());

//    private static UserData TestUser;
//    private static DataAccess DataAccess;
    static DataAccess Data_Access = new UserService(new DAOs());
    UserData User_Bob = new UserData("Bob", "password", "BobTheGOAT67@gmail.com");

    @Test
    public void clearUserData() throws DataAccessException {
//        DataAccess test = DataAccess;
        Data_Access.createUser("Bob", "password", "BobTheGOAT67@gmail.com");
        assertThrows(DataAccessException.class, () -> Data_Access.clear());
    }
}
