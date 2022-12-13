package com.example.cis296project4;

import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Client2 extends Application {
  // IO streams


  DataOutputStream toServer = null;
  DataInputStream fromServer = null;

  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    // Panel p to hold the label and text field
    BorderPane paneForTextField = new BorderPane();
    paneForTextField.setPadding(new Insets(5, 5, 5, 5));
    paneForTextField.setStyle("-fx-border-color: green");
    paneForTextField.setLeft(new Label("Message: "));

    TextField tf = new TextField();
    tf.setAlignment(Pos.BOTTOM_RIGHT);
    paneForTextField.setCenter(tf);

    BorderPane mainPane = new BorderPane();
    // Text area to display contents
    TextArea ta = new TextArea();
    mainPane.setCenter(new ScrollPane(ta));
    mainPane.setTop(paneForTextField);

    // Create a scene and place it in the stage
    Scene scene = new Scene(mainPane, 450, 200);
    primaryStage.setTitle("Client"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage

    tf.setOnAction(e -> {
      try {
        // Get the message from the text field
        String outgoingMessage = tf.getText().trim();
        ta.appendText("You: " + outgoingMessage + "\n");

        // Send the message to the server by first serializing the data,
        // then sending the length of the data, then the serialized data
        byte[] outBuff = outgoingMessage.getBytes("UTF-8");
        toServer.writeInt(outBuff.length);
        toServer.write(outBuff);
        toServer.flush();
        tf.setText("");

      }
      catch (IOException ex) {
        System.err.println(ex);
      }
    });

    try {
      // Create a socket to connect to the server
      Socket socket = new Socket("localhost", 8000);


      // Create an input stream to receive data from the server
      fromServer = new DataInputStream(socket.getInputStream());

      // Create an output stream to send data to the server
      toServer = new DataOutputStream(socket.getOutputStream());

      // Create a BackgroundReader thread and start it
      new Thread(new BackgroundReader(fromServer, ta)).start();

    }
    catch (IOException ex) {
      ta.appendText(ex.toString() + '\n');
    }
  }





  public static void main(String[] args) {
    launch(args);
  }
}