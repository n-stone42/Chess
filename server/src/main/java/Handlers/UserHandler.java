package Handlers;

import dataaccess.DataAccessException;
import service.UserService;

public class UserHandler {
    public UserService userService;
    public UserHandler(UserService userService) {
        this.userService = userService;
    }
    public UserService.RegisterResult RegisterUser(UserService.RegisterRequest request) throws DataAccessException  {
       return userService.register(request);
    }
}
