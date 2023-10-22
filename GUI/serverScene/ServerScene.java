package GUI.serverScene;

import api.serverAPI;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


 /**
     * 
     * main class for server interface. initializes the scenes and windows. calls logos.
     * 
     */
public class ServerScene extends Application {
    // added controller
    public ServerSceneController controller;
    FXMLLoader loader;
    @FXML
    private Button btnCall;

    @FXML
    private Button btnDisconnect;

    @FXML
    private Button btnJoin;

    @FXML
    private Button btnVN;

    @FXML
    private TextField textMSG;

    public ServerScene(String init) {
        
    }
    public ServerScene() {
        serverAPI.setGUI(this);
    }

    @Override
    public void start(Stage mainStage) {
        try{
            //added loader so that controller is accessable

            loader = new FXMLLoader(getClass().getResource("ServerBuilder.fxml"));
            

            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the controller
            controller = loader.getController();
            
            // edited path
            Image logo = new Image("/GUI/serverScene/logo.png");
            mainStage.getIcons().add(logo);
            mainStage.setTitle("Server VOIP");
            mainStage.setScene(scene);
            mainStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // added launch function so that GUI can be start from api
    public void launchGUI(String[] args) {
        launch(args);
    }

    // added printServer that should print out text
    public void printServer(String print) {
        if (controller != null) {
            System.out.println("gui: " + print);
            controller.addEvent(print);
        } else {
            System.out.println("fail: " + print);
        }
    }

    
    public void addUsers(String[] users) {
        controller.addUsers(users);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
