package com.idemia.jkt.tec.VerifClient.controller;

import com.idemia.jkt.tec.VerifClient.VerifClientApplication;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScanOptionsController {

    private VerifClientApplication application;

    @Autowired
    private RootLayoutController root;

    @FXML
    private CheckBox chkAllowExOtReadHeader;
    @FXML
    private CheckBox chkAuditOsLocks;
    @FXML
    private CheckBox chkActivateVarChanger;
    @FXML
    private CheckBox chkDisplayLog;
    @FXML
    private Label lblDFList;
    @FXML
    private TextField txtDFList;

    public ScanOptionsController() {}

    public void setMainApp(VerifClientApplication application) {
        this.application = application;
    }

    @FXML
    private void initialize() {
        chkAllowExOtReadHeader.setSelected(root.getScriptConfig().isAllowExOtReadHeader());
        chkAuditOsLocks.setSelected(root.getScriptConfig().isAuditOsLocks());
        chkActivateVarChanger.setSelected(root.getScriptConfig().isUseVarChanger());
        chkDisplayLog.setSelected(root.getScriptConfig().isDisplayLog());
        txtDFList.setText(root.getScriptConfig().getDfList());
        txtDFList.setTooltip(new Tooltip("amount of available memory will be replaced with XX..XX for DF in the list"));

        lblDFList.setDisable(true);
        txtDFList.setDisable(true); // disabled by default

        if (chkActivateVarChanger.isSelected()) chkDisplayLog.setDisable(false);
        else chkDisplayLog.setDisable(true);
        chkActivateVarChanger.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (chkActivateVarChanger.isSelected()) chkDisplayLog.setDisable(false);
            else chkDisplayLog.setDisable(true);
        });

        // update on varchanger module v3 is to select DFs from SaveFS XML
//        if (chkActivateVarChanger.isSelected()) {
//            lblDFList.setDisable(false);
//            txtDFList.setDisable(false);
//        }
//
//        chkActivateVarChanger.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (chkActivateVarChanger.isSelected()) {
//                lblDFList.setDisable(false);
//                txtDFList.setDisable(false);
//            }
//            else {
//                lblDFList.setDisable(true);
//                txtDFList.setDisable(true);
//            }
//        });
    }

    @FXML
    public void handleButtonSave() {
        root.getScriptConfig().setAllowExOtReadHeader(chkAllowExOtReadHeader.isSelected());
        root.getScriptConfig().setAuditOsLocks(chkAuditOsLocks.isSelected());
        root.getScriptConfig().setUseVarChanger(chkActivateVarChanger.isSelected());
        root.getScriptConfig().setDisplayLog(chkDisplayLog.isSelected());
        root.getScriptConfig().setDfList(txtDFList.getText());
        application.getScanOptionStage().close();
    }

    @FXML
    public void handleButtonCancel() {
        application.getScanOptionStage().close();
    }

}
