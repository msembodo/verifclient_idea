package com.idemia.jkt.tec.VerifClient.controller;

import com.idemia.jkt.tec.VerifClient.VerifClientApplication;
import com.idemia.jkt.tec.VerifClient.service.CreateScriptService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CreateScriptController {

    static Logger logger = Logger.getLogger(CreateScriptController.class.getName());

    private VerifClientApplication application;

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
    }

    @FXML
    public void handleButtonGenerateFull() {
        saveSettings();
    }

    private void saveSettings() {
        // update script configuration instance
        root.getScriptConfig().setUseSaveFS(chkUseSaveFs.isSelected());
        root.getScriptConfig().setFileSystemXml(txtFileSystemXml.getText());
        root.getScriptConfig().setDestinationFolder(txtDestinationFolder.getText());

        scriptService.saveConfig(root.getScriptConfig()); // persist to json file
        root.handleMenuSaveConfiguration(); // take all other options into account
    }

}
