package com.idemia.jkt.tec.VerifClient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idemia.jkt.tec.VerifClient.model.CreateScriptConfig;
import com.idemia.jkt.tec.VerifClient.response.CreateScriptResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class CreateScriptServiceImpl implements CreateScriptService {

    static Logger logger = Logger.getLogger(CreateScriptServiceImpl.class.getName());

    private File scriptSettingsFile;

    @Override
    public CreateScriptConfig initConfig() {
        scriptSettingsFile = new File("script-settings.json");
        if (scriptSettingsFile.exists()) {
            // read saved settings
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(scriptSettingsFile, CreateScriptConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // constants for default values
            String SAVEFS_PATH_DEFAULT = "OFRSAIP_ESv1_2_2__DF.xml";
            String PROJECT_PATH_DEFAULT = "C:\\";
            // initialize default values and save settings
            CreateScriptConfig defaultSettings = new CreateScriptConfig();
            defaultSettings.setUseSaveFS(false);
            defaultSettings.setFileSystemXml(SAVEFS_PATH_DEFAULT);
            defaultSettings.setDestinationFolder(PROJECT_PATH_DEFAULT);
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(scriptSettingsFile, defaultSettings);
                return defaultSettings;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void saveConfig(CreateScriptConfig scriptConfig) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(scriptSettingsFile, scriptConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CreateScriptResponse generateLight() throws Exception {
        return null;
    }

    @Override
    public CreateScriptResponse generateFull() throws Exception {
        return null;
    }

}
