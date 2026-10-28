package Handlers;

import service.UserService;

public class UserHandler {
    public UserService userService;
    public UserHandler(UserService userService) {
        userService = userService;
    }
}
