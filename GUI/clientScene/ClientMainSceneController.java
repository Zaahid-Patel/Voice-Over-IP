package GUI.clientScene;

import java.net.URL;
import java.util.ResourceBundle;

import api.clientAPI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
//skeleton code yet to be compiled with scenebuilder since ill be using api terms and will look at it after wednesday
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.control.ListView;

public class ClientMainSceneController implements Initializable {
    public ObservableList<String> allUsernames;
    @FXML
    private ListView<String> userList;
    @FXML
    private Button btnCall;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Button btnJoin;
    @FXML
    private Button btnVN;
    @FXML
    private TextArea chat;
    @FXML
    private TextArea textMSG;
    @FXML
    private Button btnPoke;
    @FXML
    private Button btn01;
    @FXML
    private Button btn02;
    @FXML
    private Button btn03;
    @FXML
    private Button btn04;
    @FXML
    private Button btn05;

    String currentSelected;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    public void Call(ActionEvent e) {
        //under construction, test with a populated array
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
              
                currentSelected = userList.getSelectionModel().getSelectedItem();
                
                //  PRINTS FOR BUGFIXING

                //System.out.println(userList.getItems().toString());
                //System.out.println("Call Function to user " + currentSelected);

            }

        });

        //makes sure that the first time it is lauches it does not select null
        if (userList.getItems().get(0) != null) {
            currentSelected = userList.getItems().get(0);
        }
        System.out.println("Call Function to user " + currentSelected);
        
        //calls towards client API
        clientAPI.callRoom((short)6);
        try {
            Stage popStage = new Stage();
            Parent popRoot = FXMLLoader.load(getClass().getResource("Popup.fxml"));
            Scene popScene = new Scene(popRoot);

            Image logo = new Image("GUI/clientScene/logo.png");
            popStage.getIcons().add(logo);
            popStage.setTitle("Private Call VOIP");
            popStage.setScene(popScene);
            popStage.show();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

     /**
     * 
     * funcation for disconnecting from channel
     * 
     */
    public void Disconnect(ActionEvent e) {
        System.out.println("Disconnect Function");
        // clientAPI.disconnect();
        clientAPI.callRoom((short)0);
    }

     /**
     * 
     * not used in this current state, stuck to a fixed limit on channel buttons below
     * 
     */
    public void Join(ActionEvent e) {
        System.out.println("Join Function");
        // TODO: select room
        // clientAPI.callRoom(0);
    }
 /**
     * 
     * voice not not implemented yet
     * 
     */
    public void VN(ActionEvent e) {
        System.out.println("VN Function");
    }

    /**
     * 
     * adds message to the chatbox that is sent to client API
     * 
     */
    public void MSG(ActionEvent e) {
        String message = textMSG.getText();
        clientAPI.sendMessage("all", message);
        chat.appendText(message + "\n");
        textMSG.setWrapText(true);
        chat.setWrapText(true);
        textMSG.clear();
        // System.out.println(message + " function");
    }
    /**
     * 
     * not currently using
     * 
     */
    public void Poke(ActionEvent e) {
        System.out.println("MSG Function");

        clientAPI.sendMessage("", "poke");
    }

    //code for adding anything do the chatbox
    public void addEvent(String event) {
        chat.appendText(event + "\n");
        chat.setWrapText(true);
    }

     /**
     * main function for updating the active users when adding a user
     * 
     */
    public void addUsers(String[] users) {
        userList.getItems().removeAll();
        userList.getItems().addAll(users);
        
        //goes through users to update the list
        for (int i = 0; i < userList.getItems().size(); i++) {
            boolean remove = true;
            if (users.length > 0) {
                for (int j = 0; j < users.length; j++) {
                    if (userList.getItems().get(i).equals(users[j])) {
                        remove = false;
                    }
                }
        
                if (remove) {
                    userList.getItems().remove(i);
                    i = i - 1;
                }
            } else {
                userList.getItems().remove(0);
                // break;
            }   
        }
    }

     /**
     * 
     * functions for joining various channels
     * 
     */
    public void pressedbtn01(ActionEvent e) {
        clientAPI.callRoom(Short.valueOf("1"));
    }
    public void pressedbtn02(ActionEvent e) {
        // TODO: select room
         clientAPI.callRoom(Short.valueOf("2"));
    }
    public void pressedbtn03(ActionEvent e) {
        // TODO: select room
         clientAPI.callRoom(Short.valueOf("3"));
    }
    public void pressedbtn04(ActionEvent e) {
        // TODO: select room
         clientAPI.callRoom(Short.valueOf("4"));
    }
    public void pressedbtn05(ActionEvent e) {
        // TODO: select room
         clientAPI.callRoom(Short.valueOf("5"));
    }

}
