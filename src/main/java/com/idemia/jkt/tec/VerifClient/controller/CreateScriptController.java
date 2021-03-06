package com.idemia.jkt.tec.VerifClient.controller;

import com.idemia.jkt.tec.VerifClient.VerifClientApplication;
import com.idemia.jkt.tec.VerifClient.response.CreateScriptResponse;
import com.idemia.jkt.tec.VerifClient.response.VarChangerResponse;
import com.idemia.jkt.tec.VerifClient.service.CreateScriptService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreateScriptController {

    static Logger logger = Logger.getLogger(CreateScriptController.class.getName());

    private VerifClientApplication application;
    private CreateScriptResponse scriptResponse;

    @Autowired
    private RootLayoutController root;
    @Autowired
    private VarChangerLogController logController;

    private Stage changerLogStage;

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

                    if (root.getScriptConfig().isUseVarChanger()) {
                        // execute var changer
                        VarChangerResponse changerResponse = scriptService.runVarChanger(true);
                        if (!changerResponse.isChangeSuccess()) {
                            logger.warn(changerResponse.getMessage());
                            Alert changerAlert = new Alert(Alert.AlertType.ERROR);
                            changerAlert.initModality(Modality.APPLICATION_MODAL);
                            changerAlert.initOwner(application.getCreateScriptStage());
                            changerAlert.setTitle("VarChanger");
                            changerAlert.setHeaderText(changerResponse.getMessage());
                            changerAlert.setContentText(getVarChangerLog());
                            changerAlert.showAndWait();
                        }

                        if (changerResponse.isChangeSuccess()) {
                            logger.info(changerResponse.getMessage());
                            // move/copy output files to destination directory
                            File source = new File("[LIGHT]_FileVerif.txt");
                            File dest = new File(scriptService.getScriptName(true));
                            File lscFile = new File(scriptService.getLscScript()); // create .LSC file
                            try {
                                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                Files.copy(source.toPath(), lscFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // delete original output
                            try {
                                Files.deleteIfExists(source.toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (root.getScriptConfig().isDisplayLog())
                                displayVarChangerLog();
                        }
                    }
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

    private void displayVarChangerLog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VarChangerLog.fxml"));
        loader.setController(logController);
        try {
            AnchorPane changerLog = loader.load();
//            VarChangerLogController controller = loader.getController();
            changerLogStage = new Stage();
            changerLogStage.setTitle("Variable Changer log");
            changerLogStage.getIcons().add(new Image(VerifClientApplication.class.getResourceAsStream("/outline_menu_book_white_18dp.png")));
            changerLogStage.initModality(Modality.WINDOW_MODAL);
            changerLogStage.initOwner(application.getPrimaryStage());
            Scene scene = new Scene(changerLog);
            changerLogStage.setScene(scene);
            changerLogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

                    if (root.getScriptConfig().isUseVarChanger()) {
                        // execute var changer
                        VarChangerResponse changerResponse = scriptService.runVarChanger(false);
                        if (!changerResponse.isChangeSuccess()) {
                            logger.warn(changerResponse.getMessage());
                            Alert changerAlert = new Alert(Alert.AlertType.ERROR);
                            changerAlert.initModality(Modality.APPLICATION_MODAL);
                            changerAlert.initOwner(application.getCreateScriptStage());
                            changerAlert.setTitle("VarChanger");
                            changerAlert.setHeaderText(changerResponse.getMessage());
                            changerAlert.setContentText(getVarChangerLog());
                            changerAlert.showAndWait();
                        }

                        if (changerResponse.isChangeSuccess()) {
                            logger.info(changerResponse.getMessage());
                            // move/copy output files to destination directory
                            File source = new File("[FULL]_FileVerif.txt");
                            File dest = new File(scriptService.getScriptName(false));
                            try {
                                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // delete original output
                            try {
                                Files.deleteIfExists(source.toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (root.getScriptConfig().isDisplayLog())
                                displayVarChangerLog();
                        }
                    }
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

    private String getVarChangerLog() {
        List<String> records = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("varchanger.log"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("=== varchanger log"))
                    continue;
                records.add(line);
            }
            reader.close();
            StringBuilder logLines = new StringBuilder();
            for (String logLine : records)
                logLines.append(logLine).append("\n");
            return logLines.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
