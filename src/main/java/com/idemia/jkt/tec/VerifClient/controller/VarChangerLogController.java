package com.idemia.jkt.tec.VerifClient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class VarChangerLogController {

    @FXML
    private TextArea txtVarChangerLog;

    public VarChangerLogController() {}

    @FXML
    private void initialize() {
        String changerLogFileName = "varchanger.log";
        try (BufferedReader br = new BufferedReader(new FileReader(changerLogFileName))) {
            StringBuffer sb = new StringBuffer();
            String currentLine;
            while ((currentLine = br.readLine()) != null)
                sb.append(currentLine + "\n");
            txtVarChangerLog.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
