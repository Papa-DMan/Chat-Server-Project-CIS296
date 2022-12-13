//Daniel Mahaney and Spencer Schneider
package com.example.cis296project4;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class MultiThreadServer extends Application {
	// Text area for displaying contents
	private TextArea ta = new TextArea();

	private ArrayList<Message> db = new ArrayList<Message>(); //create the database that is a queue
	private static ReadWriteLock _DB_LOCK = new ReentrantReadWriteLock(true);
	/*
	The ReadWrite lock was used because it will allow for thread safe access to the database.
	 */

	// Number a client
	private int clientNo = 0;

	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
		// Create a scene and place it in the stage
		Scene scene = new Scene(new ScrollPane(ta), 450, 200);
		primaryStage.setTitle("MultiThreadServer"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage


		//spawn main thread
		new Thread(() -> {
			try {
				// Create a server socket
				ServerSocket serverSocket = new ServerSocket(8000);
				ta.appendText("MultiThreadServer started at "
						+ new Date() + '\n');

				while (true) {
					// Listen for a new connection request
					Socket socket = serverSocket.accept();

					// Increment clientNo
					clientNo++;

					Platform.runLater(() -> {
						// Display the client number
						ta.appendText("Starting thread for client " + clientNo +
								" at " + new Date() + '\n');

						// Find the client's host name, and IP address
						InetAddress inetAddress = socket.getInetAddress();
						ta.appendText("Client " + clientNo + "'s host name is "
								+ inetAddress.getHostName() + "\n");
						ta.appendText("Client " + clientNo + "'s IP Address is "
								+ inetAddress.getHostAddress() + "\n");
					});

					// Create and start a new thread for the connection
					new Thread(new HandleClient(socket, clientNo, db, _DB_LOCK)).start();
				}
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}).start();
	}
	public static void main (String[]args){
		launch(args);
	}
}

/*

    Server

    local messages[]

    record {
      int id
      int len
      byte[] message
    }

    Client 1
    local messages[]

    Server:
    thread 1 : Client 1 Input Thread
    thread 2 : Client 2 Output Thread

    thread 1: on input create message object and push to the Queue (blocking)
    thread 2: await input then handle queue, if not for this client ignore (locking and unlocking)

    thread 3: client 2 Input Thread
    thread 4: client 2 Output Thread

	thread 3: on input create message object and push to the Queue (blocking)
    thread 4: await input then handle queue, if not for this client ignore (locking and unlocking)

 */

















