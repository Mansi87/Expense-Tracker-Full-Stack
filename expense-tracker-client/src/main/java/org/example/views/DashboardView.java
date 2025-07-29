package org.example.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.animations.LoadingAnimationPane;
import org.example.controllers.DashboardController;
import org.example.utils.Utilitie;
import org.example.utils.ViewNavigator;

public class DashboardView {
    private String email;
    private LoadingAnimationPane loadingAnimationPane;
    private Label currentBalanceLabel, currentBalance;
    private Label totalIncomeLabel, totalIncome;
    private Label totalExpenseLabel, totalExpense;
    private Button addTransactionButton;
    private VBox recentTransactionBox;
    private ScrollPane recentTransactionScrollPane;

    private MenuItem createCategoryMenuItem, viewCategoriesMenuItem, logoutMenuItem;

    public DashboardView(String email){
        this.email = email;
        loadingAnimationPane = new LoadingAnimationPane(Utilitie.APP_WIDTH, Utilitie.APP_HEIGHT);
        currentBalanceLabel = new Label("Current Balance:");
        totalIncomeLabel = new Label("Total Income:");
        totalExpenseLabel = new Label("Total Expense:");

        addTransactionButton = new Button("+");

        currentBalance = new Label("₹0.00");
        totalIncome = new Label("₹0.00");
        totalExpense = new Label("₹0.00");
    }
    public void show(){
        Scene scene = createScene();
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        new DashboardController(this);

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                loadingAnimationPane.resizeWidth(t1.doubleValue());
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                loadingAnimationPane.resizeHeight(t1.doubleValue());
            }
        });
        ViewNavigator.switchViews(scene);
    }

    private Scene createScene(){
        MenuBar menuBar = createMenuBar();

        VBox mainContainer = new VBox();
        mainContainer.getStyleClass().addAll("main-background");

        VBox mainContainerWrapper = new VBox();
        mainContainerWrapper.getStyleClass().addAll("dashboard-padding");
        VBox.setVgrow(mainContainerWrapper, Priority.ALWAYS);

        HBox balanceSummaryBox = createBalanceSummaryBox();
        GridPane contentGridPane = createContentGridPane();
        VBox.setVgrow(contentGridPane, Priority.ALWAYS);

        mainContainerWrapper.getChildren().addAll(balanceSummaryBox, contentGridPane);
        mainContainer.getChildren().addAll(menuBar, mainContainerWrapper, loadingAnimationPane);
        return new Scene(mainContainer, Utilitie.APP_WIDTH, Utilitie.APP_HEIGHT);
    }

    private MenuBar createMenuBar(){
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Home");

        createCategoryMenuItem = new MenuItem("Create Category");
        viewCategoriesMenuItem = new MenuItem("View Categories");
        logoutMenuItem = new MenuItem("Logout");

        fileMenu.getItems().addAll(createCategoryMenuItem, viewCategoriesMenuItem, logoutMenuItem);
        menuBar.getMenus().addAll(fileMenu);
        return menuBar;
    }

    private HBox createBalanceSummaryBox(){
        HBox balanceSummaryBox = new HBox();

        VBox currentBalanceBox = new VBox();
        currentBalanceLabel.getStyleClass().addAll("text-size-md", "text-light-gray");
        currentBalance.getStyleClass().addAll("text-size-lg", "text-white");
        currentBalanceBox.getChildren().addAll(currentBalanceLabel, currentBalance);

        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);

        VBox totalIncomeBox = new VBox();
        totalIncomeLabel.getStyleClass().addAll("text-size-md", "text-light-gray");
        totalIncome.getStyleClass().addAll("text-size-lg", "text-white");
        totalIncomeBox.getChildren().addAll(totalIncomeLabel, totalIncome);

        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);

        VBox totalExpenseBox = new VBox();
        totalExpenseLabel.getStyleClass().addAll("text-size-md", "text-light-gray");
        totalExpense.getStyleClass().addAll("text-size-lg", "text-white");
        totalExpenseBox.getChildren().addAll(totalExpenseLabel, totalExpense);

        balanceSummaryBox.getChildren().addAll(currentBalanceBox,region1, totalIncomeBox,region2, totalExpenseBox);
        return balanceSummaryBox;
    }

    private GridPane createContentGridPane(){
        GridPane gridPane = new GridPane();

        ColumnConstraints columnConstraint = new ColumnConstraints();
        columnConstraint.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(columnConstraint, columnConstraint);

        //recent transactions
        VBox recentTransactionsVBox = createRecentTransactionsVBox();
        recentTransactionsVBox.getStyleClass().addAll("field-background", "rounded-border", "padding-10px");
        GridPane.setVgrow(recentTransactionsVBox, Priority.ALWAYS);
        gridPane.add(recentTransactionsVBox, 1, 0);
        return gridPane;
     }

     private VBox createRecentTransactionsVBox(){
        VBox recentTransactionVBox = new VBox();

        HBox recentTransactionLabelandAddBtnBox = new HBox();
        Label recentTransactionsLabel = new Label("Recent Transactions");
        recentTransactionsLabel.getStyleClass().addAll("text-size-md", "text-light-gray");


        Region labelAndButtonSpaceRegion = new Region();
        HBox.setHgrow(labelAndButtonSpaceRegion, Priority.ALWAYS);

        addTransactionButton.getStyleClass().addAll("field-background", "text-size-sm", "text-light-gray", "rounded-border");

        recentTransactionLabelandAddBtnBox.getChildren().addAll(recentTransactionsLabel, labelAndButtonSpaceRegion, addTransactionButton);

        recentTransactionBox = new VBox(10);
        recentTransactionScrollPane = new ScrollPane(recentTransactionBox);
        recentTransactionScrollPane.setFitToWidth(true);
         recentTransactionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
         recentTransactionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // hides scrollbar
         recentTransactionScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");


         recentTransactionVBox.getChildren().addAll(recentTransactionLabelandAddBtnBox, recentTransactionScrollPane);
        return recentTransactionVBox;
     }

    public MenuItem getCreateCategoryMenuItem() {
        return createCategoryMenuItem;
    }

    public MenuItem getLogoutMenuItem(){
        return logoutMenuItem;
    }

    public void setCreateCategoryMenuItem(MenuItem createCategoryMenuItem) {
        this.createCategoryMenuItem = createCategoryMenuItem;
    }

    public MenuItem getViewCategoriesMenuItem() {
        return viewCategoriesMenuItem;
    }

    public String getEmail(){
        return email;
    }

    public Button getAddTransactionButton() {
        return addTransactionButton;
    }

    public VBox getRecentTransactionBox() {
        return recentTransactionBox;
    }

    public LoadingAnimationPane getLoadingAnimationPane(){
        return loadingAnimationPane;
    }
}
