javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml Server/MainServer.java 
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml Server/SenderTCPSignaller.java 
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml Server/SenderTCPSignalListener.java

javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml api/startServerGUI.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml api/serverAPI.java

javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/serverScene/ServerSceneController.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/serverScene/ServerScene.java

java --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml api.serverAPI 9999
