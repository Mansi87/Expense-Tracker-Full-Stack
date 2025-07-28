package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.utils.ViewNavigator;
import org.example.views.DashboardView;
import org.example.views.LoginView;
import org.example.views.SignUpView;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("Expense Tracker");
        ViewNavigator.setMainStage(stage);
        //new SignUpView().show();
        new DashboardView("email@email.com").show();
    }

    public static void main(String[] args){
        launch(args);
    }
}