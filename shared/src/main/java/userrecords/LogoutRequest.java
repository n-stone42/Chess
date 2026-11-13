package userrecords;

public record LogoutRequest(
        String username,
        String authToken) {
}
