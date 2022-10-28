package com.artyommameev.faststudynotes.util;

import javafx.scene.control.Alert;
import lombok.NonNull;
import lombok.val;

/**
 * A simple utility class for creating JavaFX Alert dialogs.
 *
 * @author Artyom Mameev
 */
public class SimpleAlertCreator {

    /**
     * Creates a simple JavaFX error alert dialog.
     *
     * @param title      the alert title.
     * @param headerText the alert header text.
     * @return the simple JavaFX error alert dialog with the given title and
     * header text.
     * @throws NullPointerException if any parameter is null.
     */
    public static Alert createErrorAlert(@NonNull String title,
                                         @NonNull String headerText) {
        val alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText("");

        return alert;
    }

    /**
     * Creates a simple JavaFX error alert dialog with database error text.
     *
     * @return the simple JavaFX error alert dialog with title "Database Error"
     * and header "An error occurred while interacting with the database!".
     */
    public static Alert createDatabaseErrorAlert() {
        val alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Database Error");
        alert.setHeaderText("An error occurred while interacting with " +
                "the database!");
        alert.setContentText("");

        return alert;
    }
}
