package GUI.serverScene;

import javafx.application.Platform;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

 /**
     * 
     * this class handles the actions that happen on the server scene
     * 
     */
public class ServerSceneController implements Initializable{
    @FXML
    private TextArea serverEvents;
    @FXML
    private ListView<String> userList;

@Override
public void initialize(URL location, ResourceBundle resources) {
}

     /**
     * 
     *  function for fetching informations from server and displaying it
     * 
     */
public void addEvent(String event){
    serverEvents.appendText(event + "\n");
    serverEvents.setWrapText(true);
}

 /**
     * 
     * makes user list functionality work
     * 
     */
public void addUsers(String[] users) {

    Platform.runLater(() -> {
        userList.getItems().clear();
        userList.getItems().addAll(users);
        //iterate through userlist and adds new one to the end
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
    });
}

}


