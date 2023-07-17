package com.viettel.base.cms.common;

import com.viettel.vfw5.base.dto.ExecutionResult;

public class BaseController {
    public static ExecutionResult success(Object data, String description) {
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        res.setDescription(description);
        res.setData(data);
        return res;
    }

    public static ExecutionResult successList(Object data, Object pageInfo, String description) {
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        res.setDescription(description);
        res.setData(data);
        res.setPageInfo(pageInfo);
        return res;
    }

    public static ExecutionResult error(String message) {
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
        res.setDescription(message);
        return res;
    }
}
