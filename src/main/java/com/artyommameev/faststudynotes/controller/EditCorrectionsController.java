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
 * A controller of window popup for editing {@link Correction}s.
 *
 * @author Artyom Mameev
 */
public class EditCorrectionsController {

    @FXML
    private TextField expressionTextField;
    @FXML
    private TextField correctionTextField;
    @FXML
    private CheckBox checkBoxCode;

    private Correction selectedCorrection;

    /**
     * Necessary actions to initialize the controller.
     */
    @FXML
    public void initialize() {
        selectedCorrection = CorrectionsController.getSelectedCorrection();

        expressionTextField.setText(selectedCorrection.getExpression());
        correctionTextField.setText(selectedCorrection.getCorrection());

        if (selectedCorrection.getType().equals(Correction.TYPE.CODE)) {
            checkBoxCode.setSelected(true);
        }
    }

    @FXML
    private void onEditButtonAction() {
        if (expressionTextField.getText().isEmpty() &
                expressionTextField.getText().isEmpty()) {
            return;
        }

        val updatedCorrectionType = checkBoxCode.isSelected() ?
                Correction.TYPE.CODE : Correction.TYPE.TEXT;

        val updatedCorrection = new Correction(expressionTextField.getText(),
                correctionTextField.getText(), updatedCorrectionType);

        try {
            CorrectionsDatabase.update(selectedCorrection, updatedCorrection);
        } catch (CorrectionsDatabase.CorrectionsDatabaseException e) {
            e.printStackTrace();

            SimpleAlertCreator.createDatabaseErrorAlert()
                    .show();

            return;
        }

        CorrectionsController.clearSelection();

        closePopup();
    }

    @FXML
    private void closePopup() {
        val currentStage = (Stage) expressionTextField.getScene().getWindow();

        currentStage.close();
    }
}