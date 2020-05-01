package com.idemia.jkt.tec.VerifClient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.idemia.jkt.tec.VerifClient.model.CreateScriptConfig;
import com.idemia.jkt.tec.VerifClient.response.CreateScriptResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class CreateScriptServiceImpl implements CreateScriptService {

    static Logger logger = Logger.getLogger(CreateScriptServiceImpl.class.getName());

    private File scriptSettingsFile;

    private String exceptionMessage;

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
    public CreateScriptResponse runScanner(boolean light) throws Exception {
        String serverUrl = "http://127.0.0.1:5000/";
        String statusUri = serverUrl + "getServerStatus";
        String scannerModeUri;

        if (light)
            scannerModeUri = "scanlight";
        else
            scannerModeUri = "scanfull";

        String uri = serverUrl + scannerModeUri;
        if (serverIsRunning(statusUri)) {
            String httpGetResult = doGet(uri);
            Gson gson = new Gson();
            return gson.fromJson(httpGetResult, CreateScriptResponse.class);
        } else
            return new CreateScriptResponse(false, exceptionMessage);
    }

    private String doGet(String uri) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() != 200)
            throw new Exception(response.getStatusLine().getReasonPhrase());

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null)
            result.append(line);
        return result.toString();
    }

    private boolean serverIsRunning(String url) {
        try {
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
            return false;
        }
    }

}
