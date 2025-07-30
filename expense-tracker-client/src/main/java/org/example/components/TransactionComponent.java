package org.example.components;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.example.controllers.DashboardController;
import org.example.dialog.CreateOrEditTransactionDialog;
import org.example.models.Transaction;
import org.example.utils.SqlUtil;

public class TransactionComponent extends HBox {
    private Label transactionCategoryLabel, transactionNameLabel, transactionDateLabel, transactionAmountLabel;
    private Button editButton, delButton;

    public DashboardController dashboardController;
    private Transaction transaction;

    public TransactionComponent(DashboardController dashboardController, Transaction transaction){
        this.dashboardController = dashboardController;
        this.transaction = transaction;

        setSpacing(10);
        getStyleClass().addAll("main-background", "rounded-border", "padding-10px");
        setAlignment(Pos.CENTER_LEFT);

        VBox categoryNameDateSection = createCategoryNameDateSection();
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        transactionAmountLabel = new Label("₹" + transaction.getTransactionAmount());
        transactionAmountLabel.getStyleClass().add("text-size-md");
        if(transaction.getTransactionType().equalsIgnoreCase("expense")){
            transactionAmountLabel.getStyleClass().add("text-light-red");
        }
        else{
            transactionAmountLabel.getStyleClass().add("text-light-green");
        }

        HBox actionButtonSection = createActionButtons();

        getChildren().addAll(categoryNameDateSection, region, transactionAmountLabel, actionButtonSection);
    }

    private VBox createCategoryNameDateSection(){
        VBox categoryNameDateSelection = new VBox();
        if(transaction.getTransactionCategory() == null){
            transactionCategoryLabel = new Label("Undefined");
            transactionCategoryLabel.getStyleClass().addAll("text-light-gray");
        }
        else{
            transactionCategoryLabel = new Label(transaction.getTransactionCategory().getCategoryName());
            transactionCategoryLabel.setTextFill(Paint.valueOf("#" + transaction.getTransactionCategory().getCategoryColor()));
        }


        transactionNameLabel = new Label(transaction.getTransactionName());
        transactionNameLabel.getStyleClass().addAll("text-light-gray", "text-size-md");

        transactionDateLabel = new Label(transaction.getTransactionDate().toString());
        transactionDateLabel.getStyleClass().addAll("text-light-gray");

        categoryNameDateSelection.getChildren().addAll(transactionCategoryLabel, transactionNameLabel, transactionDateLabel);
        return categoryNameDateSelection;
    }

    private HBox createActionButtons(){
        HBox actionButtonSection = new HBox(20);
        actionButtonSection.setAlignment(Pos.CENTER);

        editButton = new Button("Edit");
        editButton.getStyleClass().addAll("text-size-sm", "rounded-border");
        editButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                new CreateOrEditTransactionDialog(dashboardController, TransactionComponent.this, true).showAndWait();
            }
        });

        delButton = new Button("Del");
        delButton.getStyleClass().addAll("text-size-sm", "rounded-border", "bg-light-red", "text-white ");
        delButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(!SqlUtil.deleteTransactionById(transaction.getId())){
                    return;
                }
                setVisible(false);
                setManaged(false);
                if(getParent() instanceof VBox){
                    ((VBox) getParent()).getChildren().remove(TransactionComponent.this);
                }

                dashboardController.fetchUserData();
            }
        });

        actionButtonSection.getChildren().addAll(editButton, delButton);
        return actionButtonSection;
    }

    public Transaction getTransaction(){
        return transaction;
    }
}
