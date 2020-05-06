package com.idemia.jkt.tec.VerifClient.controller;

import com.idemia.jkt.tec.VerifClient.VerifClientApplication;
import com.idemia.jkt.tec.VerifClient.response.CreateScriptResponse;
import com.idemia.jkt.tec.VerifClient.service.CreateScriptService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CreateScriptController {

    static Logger logger = Logger.getLogger(CreateScriptController.class.getName());

    private VerifClientApplication application;
    private CreateScriptResponse scriptResponse;

    @Autowired
    private RootLayoutController root;

    @FXML
    private StackPane stackPane;

    private MaskerPane maskerPane;

    @FXML
    private CheckBox chkUseSaveFs;
    @FXML
    private TextField txtFileSystemXml;
    @FXML
    private Button btnBrowseFileSystemXml;
    @FXML
    private TextField txtDestinationFolder;

    @Autowired
    private CreateScriptService scriptService;

    public CreateScriptController() {}

    public void setMainApp(VerifClientApplication application) {
        this.application = application;
    }

    @FXML
    private void initialize() {
        chkUseSaveFs.setSelected(root.getScriptConfig().isUseSaveFS());
        txtFileSystemXml.setText(root.getScriptConfig().getFileSystemXml());
        txtDestinationFolder.setText(root.getScriptConfig().getDestinationFolder());

        // disabled by default
        txtFileSystemXml.setDisable(true);
        btnBrowseFileSystemXml.setDisable(true);

        if (chkUseSaveFs.isSelected()) {
            txtFileSystemXml.setDisable(false);
            btnBrowseFileSystemXml.setDisable(false);
        }

        chkUseSaveFs.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (chkUseSaveFs.isSelected()) {
                txtFileSystemXml.setDisable(false);
                btnBrowseFileSystemXml.setDisable(false);
            } else {
                txtFileSystemXml.setDisable(true);
                btnBrowseFileSystemXml.setDisable(true);
            }
        });

        // for masking window while script generation is executing
        maskerPane = new MaskerPane();
        maskerPane.setVisible(false);
        stackPane.getChildren().add(maskerPane);
    }

    @FXML
    public void handleButtonBrowseFileSystemXml() {
        FileChooser fileSystemXmlChooser = new FileChooser();
        fileSystemXmlChooser.setTitle("Select file system XML");
        String xmlFolder;
        if (root.getScriptConfig().getFileSystemXml() != "OFRSAIP_ESv1_2_2__DF.xml") {
            xmlFolder = new File(root.getScriptConfig().getFileSystemXml()).getParent();
            if (xmlFolder == null)
                xmlFolder = "C:\\";
        }
        else
            xmlFolder = "C:\\";
        fileSystemXmlChooser.setInitialDirectory(new File(xmlFolder));
        fileSystemXmlChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SaveFS", "*.xml"));
        File selectedInputXml = fileSystemXmlChooser.showOpenDialog(application.getPrimaryStage());
        if (selectedInputXml != null) {
            root.getScriptConfig().setFileSystemXml(selectedInputXml.getAbsolutePath());
            txtFileSystemXml.setText(selectedInputXml.getAbsolutePath());
        }
    }

    @FXML
    public void handleButtonBrowseDestinationFolder() {
        DirectoryChooser destinationChooser = new DirectoryChooser();
        destinationChooser.setTitle("Select destination folder");
        String initialDirectory;
        if (root.getScriptConfig().getDestinationFolder() != "C:\\") {
            initialDirectory = new File(root.getScriptConfig().getDestinationFolder()).getAbsolutePath();
            if (initialDirectory == null)
                initialDirectory = "C:\\";
        }
        else
            initialDirectory = "C:\\";
        destinationChooser.setInitialDirectory(new File(initialDirectory));
        File destinationDirectory = destinationChooser.showDialog(application.getPrimaryStage());
        if (destinationDirectory != null) {
            root.getScriptConfig().setDestinationFolder(destinationDirectory.getAbsolutePath());
            txtDestinationFolder.setText(destinationDirectory.getAbsolutePath());
        }
    }

    @FXML
    public void handleButtonGenerateLight() {
        saveSettings();
        logger.info(root.getScriptConfig().toJson());
        maskerPane.setText("Scanning light mode..");
        maskerPane.setVisible(true);
        // use threads for asynchronous process
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                scriptResponse = scriptService.runScanner(true);
                if (scriptResponse.isGenerationSuccess())
                    logger.info(scriptResponse.toJson());
                else
                    logger.error(scriptResponse.toJson());
                return null;
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                maskerPane.setVisible(false);
                root.getAppStatusBar().setText(scriptResponse.getMessage()); // update status bar
                if (scriptResponse.isGenerationSuccess()) {
                    Notifications.create().title("Card Scanner").text("Light scan complete.").showInformation();
                    logger.info(scriptResponse.getMessage());
                } else {
                    Notifications.create().title("Card Scanner").text("Light scan failed.").showError();
                    logger.error(scriptResponse.getMessage());
                    // show alert
                    Alert scanAlert = new Alert(Alert.AlertType.ERROR);
                    scanAlert.initModality(Modality.APPLICATION_MODAL);
                    scanAlert.initOwner(application.getCreateScriptStage());
                    scanAlert.setTitle("CardScanner error");
                    scanAlert.setHeaderText("Failed to perform light scan");
                    scanAlert.setContentText(scriptResponse.getMessage());
                    scanAlert.showAndWait();
                }
            }
        };
        Thread lightScanThread = new Thread(task);
        lightScanThread.start();
    }

    @FXML
    public void handleButtonGenerateFull() {
        saveSettings();
        logger.info(root.getScriptConfig().toJson());
        maskerPane.setText("Scanning full mode..");
        maskerPane.setVisible(true);
        // use threads for asynchronous process
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                scriptResponse = scriptService.runScanner(false);
                if (scriptResponse.isGenerationSuccess())
                    logger.info(scriptResponse.toJson());
                else
                    logger.error(scriptResponse.toJson());
                return null;
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                maskerPane.setVisible(false);
                root.getAppStatusBar().setText(scriptResponse.getMessage()); // update status bar
                if (scriptResponse.isGenerationSuccess()) {
                    Notifications.create().title("Card Scanner").text("Full scan complete.").showInformation();
                    logger.info(scriptResponse.getMessage());
                } else {
                    Notifications.create().title("Card Scanner").text("Full scan failed.").showError();
                    logger.error(scriptResponse.getMessage());
                    // show alert
                    Alert scanAlert = new Alert(Alert.AlertType.ERROR);
                    scanAlert.initModality(Modality.APPLICATION_MODAL);
                    scanAlert.initOwner(application.getCreateScriptStage());
                    scanAlert.setTitle("CardScanner error");
                    scanAlert.setHeaderText("Failed to perform full scan");
                    scanAlert.setContentText(scriptResponse.getMessage());
                    scanAlert.showAndWait();
                }
            }
        };
        Thread fullScanThread = new Thread(task);
        fullScanThread.start();
    }

    @FXML
    public void handleButtonOptions() {
        application.showScanOptions();
    }

    private void saveSettings() {
        // update script configuration instance
        root.getScriptConfig().setUseSaveFS(chkUseSaveFs.isSelected());
        root.getScriptConfig().setFileSystemXml(txtFileSystemXml.getText());
        root.getScriptConfig().setDestinationFolder(txtDestinationFolder.getText());

        root.handleMenuSaveConfiguration(); // take all other options into account
    }

}
