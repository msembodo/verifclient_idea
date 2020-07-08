package com.idemia.jkt.tec.VerifClient.model;

import com.google.gson.Gson;

public class CreateScriptConfig {

    private boolean useSaveFS;
    private String fileSystemXml;
    private String destinationFolder;
    private boolean useVarChanger;
    private boolean displayLog;
    private String dfList;
    private boolean allowExOtReadHeader;
    private boolean auditOsLocks;

    public CreateScriptConfig() {}

    public CreateScriptConfig(boolean useSaveFS, String fileSystemXml, String destinationFolder, boolean useVarChanger,
                              boolean displayLog, String dfList, boolean allowExOtReadHeader, boolean auditOsLocks) {
        this.useSaveFS = useSaveFS;
        this.fileSystemXml = fileSystemXml;
        this.destinationFolder = destinationFolder;
        this.useVarChanger = useVarChanger;
        this.displayLog = displayLog;
        this.dfList = dfList;
        this.allowExOtReadHeader = allowExOtReadHeader;
        this.auditOsLocks = auditOsLocks;
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

    public boolean isDisplayLog() {
        return displayLog;
    }

    public void setDisplayLog(boolean displayLog) {
        this.displayLog = displayLog;
    }

    public String getDfList() {
        return dfList;
    }

    public void setDfList(String dfList) {
        this.dfList = dfList;
    }

    public boolean isAllowExOtReadHeader() {
        return allowExOtReadHeader;
    }

    public void setAllowExOtReadHeader(boolean allowExOtReadHeader) {
        this.allowExOtReadHeader = allowExOtReadHeader;
    }

    public boolean isAuditOsLocks() {
        return auditOsLocks;
    }

    public void setAuditOsLocks(boolean auditOsLocks) {
        this.auditOsLocks = auditOsLocks;
    }

    // for debugging
    public String toJson() {
        return new Gson().toJson(this);
    }

}
