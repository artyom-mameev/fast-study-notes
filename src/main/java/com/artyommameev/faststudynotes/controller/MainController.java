package com.artyommameev.faststudynotes.controller;

import com.artyommameev.faststudynotes.correct.BadOcrCorrector;
import com.artyommameev.faststudynotes.database.CorrectionsDatabase;
import com.artyommameev.faststudynotes.domain.Correction;
import com.artyommameev.faststudynotes.util.SimpleAlertCreator;
import com.artyommameev.faststudynotes.writer.DocWriter;
import com.artyommameev.faststudynotes.writer.MultiWriter;
import com.artyommameev.faststudynotes.writer.TextFlowWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Cleanup;
import lombok.val;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A controller of the main application window.
 *
 * @author Artyom Mameev
 */
public class MainController {

    @FXML
    private TextArea textArea;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextFlow textFlow;
    @FXML
    private Button saveButton;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem lastSavedFileMenuItem;
    @FXML
    private CheckBox uppercaseAtTheBeginningCheckBox;
    @FXML
    private CheckBox periodAtTheEndCheckBox;

    private ObservableList<Correction> correctionsList;

    private MultiWriter multiWriter;

    private File fileToSave;

    private boolean isTextShouldStartWithUppercase;
    private boolean isTextShouldEndWithPeriod;

    /**
     * Necessary actions to initialize the controller.
     */
    public void initialize() {
        try {
            CorrectionsDatabase.init();
        } catch (CorrectionsDatabase.CorrectionsDatabaseException e) {
            SimpleAlertCreator.createDatabaseErrorAlert()
                    .show();
        }

        updateCorrections();

        val docWriter = new DocWriter();
        val textFlowWriter = new TextFlowWriter(textFlow);

        multiWriter = new MultiWriter(docWriter, textFlowWriter);

        // for auto scroll
        scrollPane.vvalueProperty().bind(textFlow.heightProperty());

        lastSavedFileMenuItem.setDisable(true);
    }

    private void updateCorrections() {
        correctionsList = FXCollections.observableArrayList();

        try {
            correctionsList.addAll(CorrectionsDatabase.getAll());
        } catch (CorrectionsDatabase.CorrectionsDatabaseException e) {
            SimpleAlertCreator.createDatabaseErrorAlert()
                    .show();
        }
    }

    @FXML
    private void undoAction() {
        if (multiWriter.isEmpty()) {
            return;
        }

        multiWriter.undo();
        multiWriter.trimEnd();

        configureButtonsAfterChange();
    }

    @FXML
    private void saveAction() {
        if (fileToSave == null) {
            val fileChooser = createFileChooser();

            fileToSave = fileChooser.showSaveDialog(saveButton.getScene()
                    .getWindow());
        }

        if (fileToSave != null) { /*if user closes the file chooser,
                                   file to save can still be null*/
            saveFile(fileToSave);

            configureButtonsAfterSaving();
        }
    }

    @FXML
    private void saveAsAction() {
        val fileChooser = createFileChooser();

        if (fileToSave != null) {
            // set last saved directory
            fileChooser.setInitialDirectory(fileToSave.getParentFile());
        }

        fileToSave = fileChooser.showSaveDialog(saveButton.getScene()
                .getWindow());

        if (fileToSave != null) { /*if user closes the file chooser,
                                   file to save can still be null*/
            saveFile(fileToSave);

            configureButtonsAfterSaving();
        }
    }

    @FXML
    private void lastSavedFileAction() {
        if (fileToSave == null || !fileToSave.exists()) {
            return;
        }

        val desktop = Desktop.getDesktop();

        try {
            desktop.open(fileToSave);
        } catch (Exception e) {
            e.printStackTrace();

            SimpleAlertCreator.createErrorAlert("Open Saved File",
                    "Error is occurred!")
                    .show();
        }
    }

    @FXML
    private void closeAction() {
        System.exit(0);
    }

    @FXML
    private void pasteTextAction() {
        val clipboard = Toolkit.getDefaultToolkit()
                .getSystemClipboard();

        val clipboardContents = clipboard.getContents(this);

        if (clipboardContents == null || !clipboardContents
                .isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return;
        }

        String clipboardText;

        try {
            clipboardText = (String) clipboardContents.getTransferData(
                    DataFlavor.stringFlavor);
        } catch (Exception e) {
            e.printStackTrace();

            SimpleAlertCreator.createErrorAlert("Paste Text",
                    "Error is occurred!")
                    .show();

            return;
        }

        Platform.runLater(() ->
                textArea.setText(clipboardText));
    }

    @FXML
    private void pasteImageAction() {
        val clipboardContents = Toolkit.getDefaultToolkit()
                .getSystemClipboard().getContents(null);

        if (clipboardContents == null || !clipboardContents
                .isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return;
        }

        BufferedImage bufferedImage;

        try {
            bufferedImage = (BufferedImage) clipboardContents
                    .getTransferData(DataFlavor.imageFlavor);
        } catch (Exception e) {
            e.printStackTrace();

            SimpleAlertCreator.createErrorAlert("Paste Image",
                    "Error is occurred!")
                    .show();

            return;
        }

        if (!multiWriter.isEmpty()) { // if any text or image is added
            multiWriter.addParagraphBreak();
        }

        multiWriter.addImage(bufferedImage);

        configureButtonsAfterChange();
    }

    @FXML
    private void selectAllAction() {
        Platform.runLater(() ->
                textArea.selectAll());
    }

    @FXML // show corrections edit window
    private void editCorrectionsAction() {
        FXMLLoader editCorrectionsFxmlLoader = new FXMLLoader(getClass()
                .getResource("/fxml/Corrections.fxml"));

        Parent editCorrectionsParent;

        try {
            editCorrectionsParent = editCorrectionsFxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        val graphicsDevice = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice();

        val editCorrectionsStage = new Stage();

        editCorrectionsStage.setTitle("Corrections");
        editCorrectionsStage.setScene(new Scene(editCorrectionsParent,
                // half of the screen resolution
                graphicsDevice.getDisplayMode().getWidth() / 2.0,
                graphicsDevice.getDisplayMode().getHeight() / 2.0));
        editCorrectionsStage.getIcons().add(new Image(
                MainController.class.getResourceAsStream(
                        "/icon/icon.png")));
        editCorrectionsStage.setAlwaysOnTop(true);
        editCorrectionsStage.showAndWait();

        updateCorrections();
    }

    @FXML
    private void uppercaseAtTheBeginningAction(ActionEvent actionEvent) {
        boolean isSelected = ((CheckMenuItem) actionEvent.getSource())
                .isSelected();

        isTextShouldStartWithUppercase = isSelected;

        uppercaseAtTheBeginningCheckBox.setSelected(isSelected);
    }

    @FXML
    private void periodAtTheEndAction(ActionEvent actionEvent) {
        boolean isSelected = ((CheckMenuItem) actionEvent.getSource())
                .isSelected();

        isTextShouldEndWithPeriod = isSelected;

        periodAtTheEndCheckBox.setSelected(isSelected);
    }

    @FXML
    private void onAddHeadlineContextAction() {
        if (textArea.getSelectedText() == null ||
                textArea.getSelectedText().trim().isEmpty()) {
            return;
        }

        val correctedText = correctText(textArea.getSelectedText().trim(),
                Correction.TYPE.TEXT);

        if (!multiWriter.isEmpty()) {
            multiWriter.addParagraphBreak();
        }

        multiWriter.addHeadline(correctedText);

        configureButtonsAfterChange();
    }

    @FXML
    private void onAddParagraphBreakAndTextContextAction() {
        if (textArea.getSelectedText() == null ||
                textArea.getSelectedText().trim().isEmpty()) {
            return;
        }

        val correctedText = correctText(textArea.getSelectedText().trim(),
                Correction.TYPE.TEXT);

        if (!multiWriter.isEmpty()) {
            multiWriter.addParagraphBreak();
        }

        multiWriter.addText(correctedText);

        configureButtonsAfterChange();
    }

    @FXML
    private void onAddLineBreakAndTextContextAction() {
        if (textArea.getSelectedText() == null ||
                textArea.getSelectedText().trim().isEmpty()) {
            return;
        }

        val correctedText = correctText(textArea.getSelectedText().trim(),
                Correction.TYPE.TEXT);

        if (!multiWriter.isEmpty()) {
            multiWriter.addLineBreak();
        }

        multiWriter.addText(correctedText);

        configureButtonsAfterChange();
    }

    @FXML
    private void onAddPlainTextContextAction() {
        if (textArea.getSelectedText() == null ||
                textArea.getSelectedText().trim().isEmpty()) {
            return;
        }

        val plainText = textArea.getSelectedText();

        if (!multiWriter.isEmpty()) {
            multiWriter.addParagraphBreak();
        }

        multiWriter.addText(plainText);

        configureButtonsAfterChange();
    }

    @FXML
    private void onAddCodeContextAction() {
        if (textArea.getSelectedText() == null ||
                textArea.getSelectedText().trim().isEmpty()) {
            return;
        }

        val correctedText = correctText(textArea.getSelectedText(),
                Correction.TYPE.CODE);

        if (!multiWriter.isEmpty()) {
            multiWriter.addParagraphBreak();
        }

        multiWriter.addCode(correctedText);

        configureButtonsAfterChange();
    }

    @FXML
    private void onCopyContextAction() {
        val stringSelection = new StringSelection(textArea.getSelectedText());

        val clipboard = Toolkit.getDefaultToolkit()
                .getSystemClipboard();

        clipboard.setContents(stringSelection, null);
    }

    @FXML
    private void onPasteContextAction() {
        val clipboard = Toolkit.getDefaultToolkit()
                .getSystemClipboard();

        if (!clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            return;
        }

        String clipboardText;

        try {
            clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            e.printStackTrace();

            SimpleAlertCreator.createErrorAlert("Paste Text",
                    "Error is occurred!")
                    .show();

            return;
        }

        int caretPosition = textArea.getCaretPosition();

        val preCaretPositionText = textArea.getText().substring(
                0, caretPosition);
        val postCaretPositionText = textArea.getText().substring(
                caretPosition);

        val correctedText = preCaretPositionText + clipboardText +
                postCaretPositionText;

        val newCaretPosition = caretPosition + clipboardText.length();

        Platform.runLater(() -> {
            textArea.setText(correctedText);

            textArea.positionCaret(newCaretPosition);
        });
    }

    @FXML
    private void onDeleteContextAction() {
        Platform.runLater(() ->
                textArea.replaceSelection(""));

        val postCaretPositionText = textArea.getText().substring(
                textArea.getCaretPosition());

        if (postCaretPositionText.startsWith("\n")) {
            val preCaretPositionText = textArea.getText().substring(
                    0, textArea.getCaretPosition());
            val postCaretPositionTextWithoutEmptyLine =
                    postCaretPositionText.substring(1);

            val correctedText = preCaretPositionText +
                    postCaretPositionTextWithoutEmptyLine;

            Platform.runLater(() ->
                    textArea.setText(correctedText));
        }
    }

    private FileChooser createFileChooser() {
        val fileChooser = new FileChooser();

        val extensionFilter = new FileChooser.ExtensionFilter(
                "DOC files (*.doc)", "*.doc");

        fileChooser.getExtensionFilters().add(extensionFilter);

        return fileChooser;
    }

    private String correctText(String text, Correction.TYPE type) {
        text = BadOcrCorrector.correct(text, type, correctionsList);

        if (type.equals(Correction.TYPE.TEXT)) {
            if (isTextShouldStartWithUppercase) {
                text = BadOcrCorrector.capitalize(text);
            }

            if (isTextShouldEndWithPeriod) {
                text = BadOcrCorrector.addPeriodAtTheEnd(text);
            }
        }

        return text;
    }

    private void saveFile(File savedFile) {
        val documentsToSave = multiWriter.save();

        try {
            @Cleanup val fileOutputStream = new FileOutputStream(savedFile);

            fileOutputStream.write(documentsToSave.get("doc"));
        } catch (Exception e) {
            e.printStackTrace();

            SimpleAlertCreator.createErrorAlert("Save File",
                    "Save File Error!")
                    .show();
        }
    }

    private void configureButtonsAfterSaving() {
        saveButton.setDisable(true);
        saveMenuItem.setDisable(true);
        lastSavedFileMenuItem.setDisable(false);
    }

    private void configureButtonsAfterChange() {
        saveButton.setDisable(false);
        saveMenuItem.setDisable(false);
    }
}