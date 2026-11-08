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
//
    final private Code code;

    public DataAccessException(String message, Code code) {
        super(message);
        this.code = code;
    }

    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
        this.code = Code.OtherError;
    }

    public int toHttpStatuscode() {
        return switch (code) {
            case BadRequest -> 400;
            case Unathurized -> 401;
            case AlreadyTaken -> 403;
            case OtherError -> 500;
        };
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }
}