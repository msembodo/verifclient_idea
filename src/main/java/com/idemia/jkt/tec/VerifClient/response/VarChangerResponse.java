package com.idemia.jkt.tec.VerifClient.response;

import com.google.gson.Gson;

public class VarChangerResponse {

    private boolean changeSuccess;
    private String message;

    public VarChangerResponse() {}

    public VarChangerResponse(boolean changeSuccess, String message) {
        this.changeSuccess = changeSuccess;
        this.message = message;
    }

    public boolean isChangeSuccess() {
        return changeSuccess;
    }

    public void setChangeSuccess(boolean changeSuccess) {
        this.changeSuccess = changeSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // for debug
    public String toJson() {
        return new Gson().toJson(this);
    }

}
