package GUI.clientScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


 /**
     * 
     * this class acts at the popup window for when private calls happen
     * 
     */
public class PopupController {

    @FXML
    private Button btnPopSend;
    @FXML
    private TextArea popChat;
    @FXML
     private TextField popType;   
    @FXML
    private Button btnDisconnect;

     /**
     * 
     * insert message for the chat that we display on private call
     * 
     */
    public void popMSG(ActionEvent e) {
        String message = popType.getText();
        popChat.appendText(message + "\n");
       // popType.setWrapText(true);
        popChat.setWrapText(true);
        popType.clear();
        // System.out.println(message + " function");
    }

}
