package com.idemia.jkt.tec.VerifClient.model;

public class CreateScriptConfig {

    private boolean useSaveFS;
    private String fileSystemXml;
    private String destinationFolder;

    public CreateScriptConfig() {}

    public CreateScriptConfig(boolean useSaveFS, String fileSystemXml, String destinationFolder) {
        this.useSaveFS = useSaveFS;
        this.fileSystemXml = fileSystemXml;
        this.destinationFolder = destinationFolder;
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

}
