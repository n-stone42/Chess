package dataaccess;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {

    public enum Code {
        BadRequest, //400
        Unathurized, //401
        AlreadyTaken, //403
        OtherError, //500
    }

    final private Code code;

    public DataAccessException(String message, Code code) {
        super(message);
        this.code = code;
    }

    public DataAccessException(String message, Throwable ex, Code code) {
        super(message, ex);
        this.code = code;
    }

    public int toHttpStatuscode() {
        return switch (code) {
            case BadRequest -> 400;
            case Unathurized -> 401;
            case AlreadyTaken -> 403;
            case OtherError -> 500;
        };
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 400 -> Code.BadRequest;
            case 401 -> Code.Unathurized;
            case 403 -> Code.AlreadyTaken;
            case 500 -> Code.OtherError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static DataAccessException fromJson(String error) {
        HashMap map = new Gson().fromJson(error, HashMap.class);
        var statusCode = Code.valueOf(map.get("status").toString());
        String errorMessage = map.get("message").toString();
//        String fullMessage = "Error: " +  errorMessage;
        return new DataAccessException(errorMessage, statusCode);
    }
}
    // list of potiental errors
    // user alreadt take - registration
    // username not found - login
    // unauthrized - login badapssword
    //unauthrized -logout, listgame, create game, join game

    // in join game
    // color already takne

