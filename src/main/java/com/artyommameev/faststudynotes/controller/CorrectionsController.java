package com.artyommameev.faststudynotes.controller;

import com.artyommameev.faststudynotes.Main;
import com.artyommameev.faststudynotes.database.CorrectionsDatabase;
import com.artyommameev.faststudynotes.domain.Correction;
import com.artyommameev.faststudynotes.util.SimpleAlertCreator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A controller of window for viewing and editing {@link Correction}s.
 *
 * @author Artyom Mameev
 */
public class CorrectionsController {

    private static Correction selectedCorrection;
    private static TableView.TableViewSelectionModel<Correction> selectionModel;

    @FXML
    private ScrollPane correctionsScrollPane;
    @FXML
    private TableColumn<Correction, String> expressionColumn;
    @FXML
    private TableColumn<Correction, String> correctionColumn;
    @FXML
    private TableColumn<Correction, String> typeColumn;
    @FXML
    private TableView<Correction> correctionsTableView;

    private ArrayList<Correction> selectedCorrections;
    private ObservableList<Correction> correctionsList;

    /**
     * Returns a {@link Correction} that was selected by the user.
     *
     * @return the {@link Correction} that was selected in the table view.
     */
    public static Correction getSelectedCorrection() {
        return selectedCorrection;
    }

    /**
     * Clears selection in the table view.
     */
    public static void clearSelection() {
        selectionModel.clearSelection();
    }

    /**
     * Necessary actions to initialize the controller.
     */
    @FXML
    public void initialize() {
        updateCorrections();

        correctionsScrollPane.setFitToHeight(true);
        correctionsScrollPane.setFitToWidth(true);

        expressionColumn.prefWidthProperty()
                .bind(correctionsTableView.widthProperty().divide(3));
        correctionColumn.prefWidthProperty()
                .bind(correctionsTableView.widthProperty().divide(3));
        typeColumn.prefWidthProperty()
                .bind(correctionsTableView.widthProperty().divide(3));

        expressionColumn.setCellValueFactory(
                new PropertyValueFactory<>("expression"));
        correctionColumn.setCellValueFactory(
                new PropertyValueFactory<>("correction"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        selectionModel = correctionsTableView.getSelectionModel();

        selectedCorrections = new ArrayList<>();

        selectionModel.selectedItemProperty().addListener(
                (val, oldVal, newVal) ->
                        selectedCorrections.add(0, newVal));
    }

    @FXML
    private void onAddCorrectionsButtonAction() {
        val addPopupFxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/AddPopup.fxml"));

        Parent parent;

        try {
            parent = addPopupFxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createNewPopup("Add Correction", parent)
                .showAndWait();

        updateCorrections();
    }

    @FXML
    private void onEditContextAction() {
        selectedCorrection = selectedCorrections.get(0);

        val editPopupFxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/EditPopup.fxml"));

        Parent parent;

        try {
            parent = editPopupFxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createNewPopup("Edit Correction", parent)
                .showAndWait();

        updateCorrections();
    }

    @FXML
    private void onRemoveContextAction() {
        selectedCorrection = selectedCorrections.get(0);

        correctionsList.remove(selectedCorrection);

        try {
            CorrectionsDatabase.remove(selectedCorrection);
        } catch (CorrectionsDatabase.CorrectionsDatabaseException e) {
            e.printStackTrace();

            SimpleAlertCreator.createDatabaseErrorAlert()
                    .show();

            return;
        }

        updateCorrections();
    }

    @FXML
    private void closePopup() {
        val currentStage = (Stage) correctionsScrollPane.getScene().getWindow();

        currentStage.close();
    }

    private void updateCorrections() {
        correctionsList = FXCollections.observableArrayList();

        try {
            correctionsList.addAll(CorrectionsDatabase.getAll());
        } catch (CorrectionsDatabase.CorrectionsDatabaseException e) {
            e.printStackTrace();

            SimpleAlertCreator.createDatabaseErrorAlert()
                    .show();

            return;
        }

        correctionsTableView.setItems(correctionsList);
    }

    private Stage createNewPopup(String title, Parent parent) {
        val newPopupStage = new Stage();

        newPopupStage.setTitle(title);
        newPopupStage.setScene(new Scene(parent, 340, 75));
        newPopupStage.getIcons().add(new Image(
                Main.class.getResourceAsStream(
                        "/icon/icon.png")));
        newPopupStage.setAlwaysOnTop(true);

        return newPopupStage;
    }
}
