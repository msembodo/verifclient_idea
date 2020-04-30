package com.idemia.jkt.tec.VerifClient.response;

import com.google.gson.Gson;

public class CreateScriptResponse {

    private boolean generationSuccess;
    private String message;

    public CreateScriptResponse() {}

    public CreateScriptResponse(boolean generationSuccess, String message) {
        this.generationSuccess = generationSuccess;
        this.message = message;
    }

    public boolean isGenerationSuccess() {
        return generationSuccess;
    }

    public void setGenerationSuccess(boolean generationSuccess) {
        this.generationSuccess = generationSuccess;
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
