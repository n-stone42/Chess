package UserRecordsModels;

public record LogoutRequest(
        String username,
        String authToken) {
}
