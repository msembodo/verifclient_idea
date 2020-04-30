package com.idemia.jkt.tec.VerifClient.service;

import com.idemia.jkt.tec.VerifClient.model.CreateScriptConfig;
import com.idemia.jkt.tec.VerifClient.response.CreateScriptResponse;

public interface CreateScriptService {

    public CreateScriptConfig initConfig();
    public void saveConfig(CreateScriptConfig scriptConfig);
    public CreateScriptResponse generateLight() throws Exception;
    public CreateScriptResponse generateFull() throws Exception;

}
