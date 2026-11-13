package client;

import gamerecord.CreateGameResult;
import gamerecord.ListGameResult;
import com.google.gson.Gson;
import exception.DataAccessException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import userrecords.*;
import gamerecord.*;

public class ServerFacade {

    //prelogin
    // help, quit, login, register

    //postlogin
    //help, logout, create game, list games, play game, observe game

    private final String serverURL;
    private final HttpClient client = HttpClient.newHttpClient();
    public ServerFacade(String url) {
        serverURL = url;
    }

    public RegisterResult registerUser(RegisterRequest registerRequest) throws DataAccessException {
        HttpRequest httpRequest = makeRequest("POST", "/user", registerRequest, null);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        HttpRequest httpRequest = makeRequest("POST", "/session", loginRequest, null);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, LoginResult.class);
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        HttpRequest httpRequest = makeRequest("DELETE", "/session", null, authToken);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest gameRequest, String authToken) throws DataAccessException {
        HttpRequest httpRequest = makeRequest("POST", "/game", gameRequest, authToken);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, CreateGameResult.class);
    }

    public ListGameResult listGame(String authToken) throws DataAccessException {
        HttpRequest httpRequest = makeRequest("GET", "/game",null, authToken);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, ListGameResult.class);
    }

    public JoinGameResult joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
        HttpRequest httpRequest = makeRequest("PUT", "/game",request, authToken);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, JoinGameResult.class);
    }

    private HttpRequest makeRequest(String action, String path, Object body, String authToken) {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(action, handelBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }

        return request.build();
    }

    private HttpRequest.BodyPublisher handelBody(Object request) {
        if (request == null) {
            return HttpRequest.BodyPublishers.noBody();
        }

        return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception exception) {
            throw new DataAccessException("error in sending request " + exception, DataAccessException.Code.OtherError);
        }
    }
    public void clear() throws DataAccessException {
        HttpRequest httpRequest = makeRequest("DELETE", "/db", null, null);
        sendRequest(httpRequest);
    }

    private <T> T handelResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        if (response.statusCode() == 200) {
            String body = response.body();
            if (body == null) {
                throw new DataAccessException("Error: " + body, DataAccessException.Code.OtherError);
            }


            if (responseClass != null) {
                // change it to JSON and then return
                return new Gson().fromJson(response.body(), responseClass);
            }
        } else {// message was an error
            String body = response.body();
            if (body != null) {
                throw DataAccessException.fromJson(body);
            }
        }return null;
    }
}
