package org.example.dialog;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.example.models.User;

public class CustomDialog extends Dialog {
    protected User user;

    public CustomDialog(User user){
        this.user = user;

        getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        getDialogPane().getStyleClass().addAll("main-background");

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setVisible(true);
        okButton.setDisable(false);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return "OK Clicked"; // Replace with actual result
            }
            return null;
        });
    }
}
