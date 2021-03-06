package com.idemia.jkt.tec.VerifClient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.idemia.jkt.tec.VerifClient.controller.RootLayoutController;
import com.idemia.jkt.tec.VerifClient.model.CreateScriptConfig;
import com.idemia.jkt.tec.VerifClient.response.CreateScriptResponse;
import com.idemia.jkt.tec.VerifClient.response.VarChangerResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateScriptServiceImpl implements CreateScriptService {

    static Logger logger = Logger.getLogger(CreateScriptServiceImpl.class.getName());

    private File scriptSettingsFile;

    private String exceptionMessage;

    private int exitVal;

    @Autowired
    private RootLayoutController root;

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
            String DF_LIST = "3F00;7F10;5F3A;7F20;7FF0;5F3B";
            // initialize default values and save settings
            CreateScriptConfig defaultSettings = new CreateScriptConfig();
            defaultSettings.setUseSaveFS(false);
            defaultSettings.setFileSystemXml(SAVEFS_PATH_DEFAULT);
            defaultSettings.setDestinationFolder(PROJECT_PATH_DEFAULT);
            defaultSettings.setUseVarChanger(false);
            defaultSettings.setDisplayLog(true);
            defaultSettings.setDfList(DF_LIST); // not applicable anymore; set to not visible instead
            defaultSettings.setAllowExOtReadHeader(true);
            defaultSettings.setAuditOsLocks(true);
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

    @Override
    public VarChangerResponse runVarChanger(boolean light) {
        // syntax: changer <script> <variables> <savefs> [<mode>]
        String varChangerExe = "changer_module_v3.exe";
        String script = getScriptName(light);

        // due to some weird varchanger behavior we need to copy necessary files to VC directory (and delete again)
        Path localScript = copyToUserDir(script);
        Path localVariables = copyToUserDir(root.getVerifConfig().getPathToVariablesTxt());
        Path localSavefs = copyToUserDir(root.getScriptConfig().getFileSystemXml());

//        String dfList = root.getScriptConfig().getDfList().replace(";", ""); // filter semicolon
        if (light)
            runShellCommand(
                    varChangerExe,
                    Paths.get(script).getFileName().toString(),
                    Paths.get(root.getVerifConfig().getPathToVariablesTxt()).getFileName().toString(),
                    Paths.get(root.getScriptConfig().getFileSystemXml()).getFileName().toString(),
                    false
            );
        else
            runShellCommand(
                    varChangerExe,
                    Paths.get(script).getFileName().toString(),
                    Paths.get(root.getVerifConfig().getPathToVariablesTxt()).getFileName().toString(),
                    Paths.get(root.getScriptConfig().getFileSystemXml()).getFileName().toString(),
                    true
            );

        deleteLocalCopy(localScript);
        deleteLocalCopy(localVariables);
        deleteLocalCopy(localSavefs);

        // define possible error codes
        if (exitVal == 0)
            return new VarChangerResponse(true, "VarChanger - OK");
        if (exitVal == 1)
            return new VarChangerResponse(false, "Variables with same values are found");
        else
            return new VarChangerResponse(false, "(Error not defined)");
    }

    private Path copyToUserDir(String sourcePath) {
        File source = new File(sourcePath);
        File userCopy = new File(System.getProperty("user.dir") + "\\" + Paths.get(sourcePath).getFileName().toString());
        try {
            Files.copy(source.toPath(), userCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return userCopy.toPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteLocalCopy(Path localCopy) {
        try {
            Files.deleteIfExists(localCopy);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void launchProcess(List<String> cmdArray) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        exitVal = process.waitFor();
    }

    private void runShellCommand(String varChangerExe, String script, String variables, String saveFsXml, boolean fullMode) {
        List<String> cmdArray = new ArrayList<>();
        cmdArray.add(varChangerExe);
        cmdArray.add(script);
        cmdArray.add(variables);
        cmdArray.add(saveFsXml);
        if (fullMode)
            cmdArray.add("full");
        try {
            launchProcess(cmdArray);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getScriptName(boolean lightMode) {
        String inputFile = Paths.get(root.getScriptConfig().getFileSystemXml()).getFileName().toString();
        final String substring = inputFile.substring(0, inputFile.length() - 4);
        if (lightMode) {
            if (root.getScriptConfig().isUseSaveFS()) {
                String outFile = substring + "__light.pcom";
                return root.getScriptConfig().getDestinationFolder() + "\\" + outFile;
            } else
                return "script__light.pcom";
        }
        else {
            if (root.getScriptConfig().isUseSaveFS()) {
                String outFile = substring + "__full.pcom";
                return root.getScriptConfig().getDestinationFolder() + "\\" + outFile;
            } else
                return "script__full.pcom";
        }
    }

    @Override
    public String getLscScript() {
        if (root.getScriptConfig().isUseSaveFS()) {
            String inputFile = Paths.get(root.getScriptConfig().getFileSystemXml()).getFileName().toString();
            String outFile = inputFile.substring(0, inputFile.length() - 4) + ".lsc";
            return root.getScriptConfig().getDestinationFolder() + "\\" + outFile;
        } else
            return "script.lsc";
    }

}
