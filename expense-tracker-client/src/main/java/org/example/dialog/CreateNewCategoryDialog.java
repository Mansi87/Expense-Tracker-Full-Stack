package org.example.dialog;

import com.google.gson.JsonObject;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.example.models.User;
import org.example.utils.SqlUtil;
import org.example.utils.Utilitie;

public class CreateNewCategoryDialog extends CustomDialog{
    private TextField newCategoryTextField;
    private ColorPicker colorPicker;
    private Button createCategoryBtn;

    public CreateNewCategoryDialog(User user){
        super(user);
        setTitle("Create New Category");
        getDialogPane().setContent(createDialogContentBox());
    }

    private VBox createDialogContentBox(){
        VBox dialogContentBox = new VBox(10);

        newCategoryTextField = new TextField();
        newCategoryTextField.setPromptText("Enter Category Name");
        newCategoryTextField.getStyleClass().addAll("text-size-sm", "field-background", "text-light-gray");

        colorPicker = new ColorPicker();
        colorPicker.getStyleClass().add("text-size-sm");
        colorPicker.setMaxWidth(Double.MAX_VALUE);

        createCategoryBtn = new Button("Create");
        createCategoryBtn.getStyleClass().addAll("bg-light-blue", "text-size-sm", "text-white");
        createCategoryBtn.setMaxWidth(Double.MAX_VALUE);
        createCategoryBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String categoryName = newCategoryTextField.getText();
                String color = Utilitie.getHexColorValue(colorPicker);

                JsonObject userData = new JsonObject();
                userData.addProperty("id", user.getId());
                JsonObject transactionCategoryData = new JsonObject();
                transactionCategoryData.add("user", userData);
                transactionCategoryData.addProperty("categoryName", categoryName);
                transactionCategoryData.addProperty("categoryColor", color);

                boolean postTransactionCategoryStatus = SqlUtil.postTransactionCategory(transactionCategoryData);
                if(postTransactionCategoryStatus){
                    Utilitie.showAlertDialog(Alert.AlertType.INFORMATION,
                            "Creation Successful");
                }
                else{
                    Utilitie.showAlertDialog(Alert.AlertType.ERROR,
                            "Failed to create");
                }
            }
        });

        dialogContentBox.getChildren().addAll(newCategoryTextField, colorPicker, createCategoryBtn);
        return dialogContentBox;
    }
}
