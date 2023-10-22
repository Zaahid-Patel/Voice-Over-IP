javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/clientScene/ClientMainSceneController.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/clientScene/ClientMainScene.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/clientScene/PopupController.java

javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml Client/ClientSignaller.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml Client/ReceiveSound.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml Client/SendSound.java

javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml api/clientAPI.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml api/startClientGUI.java



java --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml api.clientAPI 25.39.156.217 9999 