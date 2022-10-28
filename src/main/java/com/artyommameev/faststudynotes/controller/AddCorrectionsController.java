package com.artyommameev.faststudynotes.controller;

import com.artyommameev.faststudynotes.database.CorrectionsDatabase;
import com.artyommameev.faststudynotes.domain.Correction;
import com.artyommameev.faststudynotes.util.SimpleAlertCreator;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.val;

/**
 * A controller of popup window for adding new {@link Correction}s.
 *
 * @author Artyom Mameev
 */
public class AddCorrectionsController {

    @FXML
    private TextField expressionTextField;
    @FXML
    private TextField correctionTextField;
    @FXML
    private CheckBox checkBoxCode;

    @FXML
    private void onAddButtonAction() {
        if (expressionTextField.getText().isEmpty() &
                correctionTextField.getText().isEmpty()) {
            return;
        }

        val correctionType = checkBoxCode.isSelected() ?
                Correction.TYPE.CODE : Correction.TYPE.TEXT;

        val correction = new Correction(expressionTextField.getText(),
                correctionTextField.getText(), correctionType);

        try {
            CorrectionsDatabase.insert(correction);
        } catch (CorrectionsDatabase.CorrectionsDatabaseException e) {
            e.printStackTrace();

            SimpleAlertCreator.createDatabaseErrorAlert()
                    .show();

            return;
        } catch (CorrectionsDatabase.CorrectionAlreadyExistsException e) {
            SimpleAlertCreator.createErrorAlert("Add Correction",
                    "The Correction Already Exists!")
                    .show();

            return;
        }

        closePopup();
    }

    @FXML
    private void closePopup() {
        val currentStage = (Stage) expressionTextField.getScene().getWindow();

        currentStage.close();
    }
}