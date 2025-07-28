package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.example.components.TransactionComponent;
import org.example.dialog.CreateNewCategoryDialog;
import org.example.dialog.CreateOrEditTransactionDialog;
import org.example.dialog.ViewOrEditTransactionCategoryDialog;
import org.example.models.Transaction;
import org.example.models.User;
import org.example.utils.SqlUtil;
import org.example.views.DashboardView;

import java.util.List;

public class DashboardController {
    private final int recentTransactionSize = 5;

    private DashboardView dashboardView;
    private User user;

    private int currentPage;
    private List<Transaction> recentTransactions;

    public DashboardController(DashboardView dashboardView){
        this.dashboardView = dashboardView;
        this.currentPage=0;
        fetchUserData();
        initialize();
    }

    public void fetchUserData(){
        user = SqlUtil.getUserByEmail(dashboardView.getEmail());
        dashboardView.getRecentTransactionBox().getChildren().clear();
        createRecentTransactionComponents();
    }

    private void createRecentTransactionComponents(){
        recentTransactions = SqlUtil.getRecentTransactionByUserId(
                user.getId(),
                0,
                currentPage,
                recentTransactionSize
        );
        if(recentTransactions == null) return;

        for(Transaction transaction: recentTransactions){
            dashboardView.getRecentTransactionBox().getChildren().add(
                    new TransactionComponent(this, transaction)
            );
        }
    }

    private void initialize(){
        addMenuActions();
        addRecentTransactionActions();
    }

    private void addMenuActions(){
        dashboardView.getCreateCategoryMenuItem().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new CreateNewCategoryDialog(user).showAndWait();
            }
        });

        dashboardView.getViewCategoriesMenuItem().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new ViewOrEditTransactionCategoryDialog(user, DashboardController.this).showAndWait();
            }
        });
    }

    private void addRecentTransactionActions(){
        dashboardView.getAddTransactionButton().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                new CreateOrEditTransactionDialog(DashboardController.this, false).showAndWait();
            }
        });
    }


    public User getUser(){
        return user;
    }
}
