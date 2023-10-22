#!/bin/bash
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/clientScene/ClientMainSceneController.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/clientScene/ClientMainScene.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/clientScene/PopupController.java
java --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI.clientScene.ClientMainScene  
