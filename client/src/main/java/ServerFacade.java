import GameRecordModels.CreateGameResult;
import GameRecordModels.ListGameResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import service.UserService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    //prelogin
    // help, quit, login, register

    //postlogin
    //help, logout, create game, list games, play game, observe game

    private final String port;
    private final HttpClient client = HttpClient.newHttpClient();
    public ServerFacade(String url) {
        port = url;
    }

    // the pattern for each method for doing something is
    // build request
    //send request
    //return handleResponse



    //before login functions
    //help and quit are handled in the client, not here
    public UserService.RegisterResult registerUser(UserService.RegisterRequest registerRequest) throws DataAccessException{
        HttpRequest httpRequest = makeRequest("POST", "/session", registerRequest);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, UserService.RegisterResult.class);
    }

    public UserService.LoginResult login(UserService.LoginRequest loginRequest) throws DataAccessException{
        HttpRequest httpRequest = makeRequest("POST", "/session",loginRequest);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, UserService.LoginResult.class);
    }

    public UserService.LogoutResult logout(UserService.LogoutRequest logoutRequest) throws DataAccessException{
        HttpRequest httpRequest = makeRequest("DELETE", "/session",logoutRequest);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, UserService.LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameResult gameResult) throws DataAccessException{
        HttpRequest httpRequest = makeRequest("POST", "/game",gameResult);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, CreateGameResult.class);
    }

    public ListGameResult listGame() throws DataAccessException{
        HttpRequest httpRequest = makeRequest("POST", "/game",null);
        HttpResponse httpResponse = sendRequest(httpRequest);
        return handelResponse(httpResponse, ListGameResult.class);
    }



    private HttpRequest makeRequest(String action, String path, Object body) {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(port + path))
                .method(action, handelBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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
            throw new DataAccessException("error in sending request" + exception, DataAccessException.Code.OtherError);
        }
    }

    private <Object> Object handelResponse(HttpResponse<String> response, Class<Object> responseClass) throws DataAccessException {
        int status = response.statusCode();
        if (status == 200) {
            String body = response.body();
            if (body != null) {
                throw new DataAccessException("Error: " + body, DataAccessException.Code.OtherError);
            }
        }

        if (responseClass != null) {
            // change it to JSON and then return
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    // pet shop pattern
    // method (id list)
    //list -> build request -> makeBodyRequest
    // catches above then send request
    //catches above and returs a handleResponse
    //list of helpers buildRequest, makeBodyRequest, sendRequest, handleResponse
}
