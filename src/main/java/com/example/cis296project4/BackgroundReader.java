package com.example.cis296project4;

import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.IOException;

class BackgroundReader implements Runnable {

	private DataInputStream fromServer;
	private TextArea ta;

	public BackgroundReader(DataInputStream fromServer, TextArea ta) {
		this.fromServer = fromServer;
		this.ta = ta;
	}

	@Override
	public void run() {
		try {
			while (true) {
				String incomingMessage = "";
				int len = fromServer.readInt();

				while (len != 0) {
					byte[] inBuff = new byte[len];
					fromServer.readFully(inBuff);
					incomingMessage = new String(inBuff, "UTF-8");
					ta.appendText("Friend: " + incomingMessage + "\n");
					len = fromServer.readInt();
				}
			}
		} catch (IOException ex) {
			ta.appendText(ex.toString() + '\n');
		}
	}
}