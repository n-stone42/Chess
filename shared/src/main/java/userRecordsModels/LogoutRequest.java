package userRecordsModels;

public record LogoutRequest(
        String username,
        String authToken) {
}
