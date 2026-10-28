package Handlers;

import dataaccess.DataAccessException;
import service.ClearService;

public class ClearHandler {
    public ClearService clearService;

    public ClearHandler(ClearService clearService) {
        clearService = clearService;
    }

    public void clear() throws DataAccessException {
        clearService.eraseAllData();
    }
}
