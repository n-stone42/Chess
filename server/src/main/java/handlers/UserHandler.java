package handlers;

import service.UserService;

public class UserHandler {
    public UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }
}
