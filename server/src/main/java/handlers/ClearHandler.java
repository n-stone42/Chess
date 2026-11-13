package handlers;

import dataAccessException.dataAccessException;
import service.ClearService;

public class ClearHandler {
    public ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear() throws dataAccessException {
        clearService.eraseAllData();
    }
}
