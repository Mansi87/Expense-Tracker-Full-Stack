package org.example.dialog;

import com.google.gson.JsonObject;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jdk.jfr.Category;
import org.example.components.TransactionComponent;
import org.example.controllers.DashboardController;
import org.example.models.Transaction;
import org.example.models.TransactionCategory;
import org.example.models.User;
import org.example.utils.SqlUtil;
import org.example.utils.Utilitie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CreateOrEditTransactionDialog extends CustomDialog{

    private List<TransactionCategory> transactionCategories;
    private TextField transactionNameField, transactionAmountField;
    private DatePicker transactionDatePicker;
    private ComboBox<String> transactionCategoryBox;
    private TransactionComponent trans
    private ToggleGroup transactionTypeToggleGroup;

    private boolean isEditing;

    //editing
    public CreateOrEditTransactionDialog(DashboardController dashboardcontroller, TransactionComponent transactionComponent, boolean isEditing){
        super(dashboardcontroller.getUser());
        this.isEditing = isEditing;

        setTitle(isEditing ? "Edit Transaction" : "Creating New Transaction");
        setWidth(700);
        setHeight(595);

        transactionCategories = SqlUtil.getAllTransactionCategoriesByUser(user);

        VBox mainContentBox = createMainContentBox();
        getDialogPane().setContent(mainContentBox);
        mainContentBox.setPrefSize(600, 495);
        getDialogPane().setPrefSize(600, 495);
    }

    //use for creating transactions
    public CreateOrEditTransactionDialog(DashboardController dashboardController, boolean isEditing){
        this(dashboardController, null, isEditing);
    }

    private VBox createMainContentBox(){
        VBox mainContentBox = new VBox(30);
        mainContentBox.setAlignment(Pos.CENTER);

        transactionNameField = new TextField();
        transactionNameField.setPromptText("Enter Transaction Name: ");
        transactionNameField.getStyleClass().addAll("field-background","text-light-gray", "text-size-md", "rounded-border");

        transactionAmountField = new TextField();
        transactionAmountField.setPromptText("Enter Transaction Amount: ");
        transactionAmountField.getStyleClass().addAll("field-background","text-light-gray", "text-size-md", "rounded-border");

        transactionDatePicker = new DatePicker();
        transactionDatePicker.setPromptText("Transaction Date?");
        transactionDatePicker.getStyleClass().addAll("field-background","text-light-gray", "text-size-md", "rounded-border");
        transactionDatePicker.setMaxWidth(Double.MAX_VALUE);

        transactionCategoryBox = new ComboBox<>();
        transactionCategoryBox.setPromptText("Choose Category");
        transactionCategoryBox.getStyleClass().addAll("field-background","text-light-gray", "text-size-md", "rounded-border");
        transactionCategoryBox.setMaxWidth(Double.MAX_VALUE);
        for(TransactionCategory transactionCategory: transactionCategories){
            transactionCategoryBox.getItems().add(transactionCategory.getCategoryName());
        }

        if(isEditing){
            Transaction transaction =
        }

        mainContentBox.getChildren().addAll(transactionNameField, transactionAmountField, transactionDatePicker, transactionCategoryBox, createTransactionTypeRadioButtonGroup(),
                createConfirmAndCancelButtonsBox());
        return mainContentBox;
    }

    private HBox createTransactionTypeRadioButtonGroup(){
        HBox radioButtonsBox = new HBox(50);
        radioButtonsBox.setAlignment(Pos.CENTER);

        transactionTypeToggleGroup = new ToggleGroup();

        RadioButton incomeRadioButton = new RadioButton("Income");
        incomeRadioButton.setToggleGroup(transactionTypeToggleGroup);
        incomeRadioButton.getStyleClass().addAll("text-size-sm", "text-light-gray");

        RadioButton expenseRadioButton = new RadioButton("Expense");
        expenseRadioButton.setToggleGroup(transactionTypeToggleGroup);
        expenseRadioButton.getStyleClass().addAll("text-size-sm", "text-light-gray");

        radioButtonsBox.getChildren().addAll(incomeRadioButton, expenseRadioButton);
        return radioButtonsBox;
    }

    private HBox createConfirmAndCancelButtonsBox(){
        HBox confirmAndCancelBox = new HBox(50);
        confirmAndCancelBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(200);
        saveButton.getStyleClass().addAll("bg-light-blue", "text-white", "text-size-md", "rounded-border");
        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                JsonObject transactionDataObject = new JsonObject();

                String transactionName = transactionNameField.getText();
                transactionDataObject.addProperty("transactionName", transactionName);

                double transactionAmount = Double.parseDouble(transactionAmountField.getText());
                transactionDataObject.addProperty("transactionAmount", transactionAmount);

                LocalDate transactionDate = transactionDatePicker.getValue();
                transactionDataObject.addProperty("transactionDate", transactionDate.format(
                        DateTimeFormatter.ISO_LOCAL_DATE
                ));

                String transactionType = ((RadioButton) transactionTypeToggleGroup.getSelectedToggle()).getText();
                transactionDataObject.addProperty("transactionType", transactionType);

                String transactionCategoryName = transactionCategoryBox.getValue();
                JsonObject transactionCategoryData = new JsonObject();
                if(transactionCategoryName != null){
                    TransactionCategory transactionCategory = Utilitie.findTransactionCategoryByName(
                            transactionCategories,
                            transactionCategoryName
                    );
                    JsonObject userData = new JsonObject();
                    transactionCategoryData.addProperty("id", transactionCategory.getId());
                    transactionDataObject.add("transactionCategory", transactionCategoryData);
                }

                JsonObject userData = new JsonObject();
                userData.addProperty("id", user.getId());
                transactionDataObject.add("user", userData);

                if(SqlUtil.postTransaction(transactionDataObject)){
                    Utilitie.showAlertDialog(Alert.AlertType.INFORMATION, "Transaction creation Successful!");
                }
                else{
                    Utilitie.showAlertDialog(Alert.AlertType.ERROR, "Error: Failed to create..");
                }
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(200);
        cancelButton.getStyleClass().addAll("text-size-md", "rounded-border");
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                CreateOrEditTransactionDialog.this.close();
            }
        });

        confirmAndCancelBox.getChildren().addAll(saveButton, cancelButton);
        return confirmAndCancelBox;
    }

}
