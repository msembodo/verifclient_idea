package com.idemia.jkt.tec.VerifClient.service;

import com.idemia.jkt.tec.VerifClient.model.CreateScriptConfig;
import com.idemia.jkt.tec.VerifClient.response.CreateScriptResponse;
import com.idemia.jkt.tec.VerifClient.response.VarChangerResponse;

public interface CreateScriptService {

    CreateScriptConfig initConfig();
    void saveConfig(CreateScriptConfig scriptConfig);
    CreateScriptResponse runScanner(boolean light) throws Exception;
    VarChangerResponse runVarChanger(boolean light);
    String getScriptName(boolean lightMode);
    String getLscScript();

}
