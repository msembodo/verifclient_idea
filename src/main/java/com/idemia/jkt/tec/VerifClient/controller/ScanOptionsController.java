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
    private CheckBox chkActivateVarChanger;
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
        chkActivateVarChanger.setSelected(root.getScriptConfig().isUseVarChanger());
        txtDFList.setText(root.getScriptConfig().getDfList());
        txtDFList.setTooltip(new Tooltip("amount of available memory will be replaced with XX..XX for DF in the list"));

        lblDFList.setDisable(true);
        txtDFList.setDisable(true); // disabled by default

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
        root.getScriptConfig().setUseVarChanger(chkActivateVarChanger.isSelected());
        root.getScriptConfig().setDfList(txtDFList.getText());
        application.getScanOptionStage().close();
    }

    @FXML
    public void handleButtonCancel() {
        application.getScanOptionStage().close();
    }

}
