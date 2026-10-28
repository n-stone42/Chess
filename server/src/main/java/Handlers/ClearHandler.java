package Handlers;

import dataaccess.DataAccessException;
import service.ClearService;

public class ClearHandler {
    public ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear() throws DataAccessException {
        System.out.println("called clear() in ClearHandler");
        clearService.eraseAllData();
    }
}
