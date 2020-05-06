package com.idemia.jkt.tec.VerifClient.model;

import com.google.gson.Gson;

public class CreateScriptConfig {

    private boolean useSaveFS;
    private String fileSystemXml;
    private String destinationFolder;
    private boolean useVarChanger;
    private String dfList;

    public CreateScriptConfig() {}

    public CreateScriptConfig(boolean useSaveFS, String fileSystemXml, String destinationFolder, boolean useVarChanger, String dfList) {
        this.useSaveFS = useSaveFS;
        this.fileSystemXml = fileSystemXml;
        this.destinationFolder = destinationFolder;
        this.useVarChanger = useVarChanger;
        this.dfList = dfList;
    }

    public boolean isUseSaveFS() {
        return useSaveFS;
    }

    public void setUseSaveFS(boolean useSaveFS) {
        this.useSaveFS = useSaveFS;
    }

    public String getFileSystemXml() {
        return fileSystemXml;
    }

    public void setFileSystemXml(String fileSystemXml) {
        this.fileSystemXml = fileSystemXml;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public boolean isUseVarChanger() {
        return useVarChanger;
    }

    public void setUseVarChanger(boolean useVarChanger) {
        this.useVarChanger = useVarChanger;
    }

    public String getDfList() {
        return dfList;
    }

    public void setDfList(String dfList) {
        this.dfList = dfList;
    }

    // for debugging
    public String toJson() {
        return new Gson().toJson(this);
    }

}
