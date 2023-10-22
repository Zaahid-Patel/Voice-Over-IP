package GUI.clientScene;

import api.clientAPI;
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
     * The main scene that initates the scene for client
     * 
     */
public class ClientMainScene extends Application {
    public ClientMainSceneController controller;

    //defining buttons for use, functions listed down below for functionality
    
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
    public ClientMainScene(String init) {

    }

    public ClientMainScene() {
        clientAPI.setGUI(this);
    }

    /**
     * 
     * the initiation of the main stage window
     * 
     */
    @Override
    public void start(Stage mainStage) {
        try{
            loader = new FXMLLoader(getClass().getResource("Builder.fxml"));
            
            Parent root = loader.load();
            Scene scene = new Scene(root);
            //sets controller
            controller = loader.getController();
            //creates icon
            Image logo = new Image("GUI/clientScene/logo.png");
            mainStage.getIcons().add(logo);
            mainStage.setTitle("Client VOIP");
            mainStage.setScene(scene);
            mainStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

     /**
     * 
     * launches gui
     */
    public void launchGUI(String [] args) {
        launch(args);
    }
    public static void main(String[] args) {
        launch(args);
    }

     /**
     * 
     * prints client activity
     * 
     */
    public void printClient(String print) {
        if (controller != null) {
            System.out.println("gui: " + print);
            controller.addEvent(print);
        } else {
            System.out.println("fail: " + print);
        }
    }
    /**
     * 
     * adds a user for the gui 
     * 
     */
    public void addUsers(String[] users) {
        if (controller != null) {
            controller.addUsers(users);
        }
        
    }

}
