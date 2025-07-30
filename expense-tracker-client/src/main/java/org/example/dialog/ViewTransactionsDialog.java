package org.example.dialog;


import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.example.components.TransactionComponent;
import org.example.controllers.DashboardController;
import org.example.models.Transaction;
import org.example.utils.SqlUtil;

import java.time.Month;
import java.util.List;

public class ViewTransactionsDialog extends CustomDialog{
    private DashboardController dashboardController;
    private String monthName;

    public ViewTransactionsDialog(DashboardController dashboardController, String monthName){
        super(dashboardController.getUser());
        this.dashboardController = dashboardController;
        this.monthName = monthName;

        setTitle("View Transactions");
        setWidth(815);
        setHeight(500);

        ScrollPane transactionScrollPane = createTransactionScrollPane();
        getDialogPane().setContent(transactionScrollPane);
    }

    private ScrollPane createTransactionScrollPane(){
        VBox vBox = new VBox(20);
        vBox.setPrefWidth(800);
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setMinHeight(getHeight() - 40);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportWidth(800);

        List<Transaction> monthTransaction = SqlUtil.getAllTransactionsByUserId(
                dashboardController.getUser().getId(),
                dashboardController.getCurrentYear(),
                Month.valueOf(monthName).getValue()
        );

        if(monthTransaction != null){
            for(Transaction transaction : monthTransaction){
                TransactionComponent transactionComponent = new TransactionComponent(
                        dashboardController,
                        transaction
                );
                transactionComponent.getStyleClass().addAll("border-light-gray", "rounded-border");
                vBox.getChildren().add(transactionComponent);
            }
        }

        return scrollPane;
    }
}
