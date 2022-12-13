//Daniel Mahaney and Spencer Schneider
package com.example.cis296project4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;

public class ServerReader implements Runnable {

	private DataInputStream inputToClient;
	private DataOutputStream outputToClient;
	private ArrayList<Message> db;
	private ReadWriteLock lock;
	private int clientIndex;

	//Class that handles incoming messages from client to server
	public ServerReader(DataInputStream inputToClient,DataOutputStream outputToClient, ArrayList<Message> db, ReadWriteLock lock, int clientId) {
		this.inputToClient = inputToClient;
		this.db = db;
		this.outputToClient = outputToClient;
		clientIndex = clientId;
		this.lock = lock;
	}

	@Override
	public void run() {
		try {

			while (true) {

				/*		| size_t size | byte[size] |
				*		|-----int-----|---byte[]---|
				*/
				int messageLength = inputToClient.readInt();
				byte[] inBuff = new byte[messageLength];
				inputToClient.readFully(inBuff); 					//blocking
				Message inmsg = new Message(clientIndex, messageLength, inBuff);
				//Locks to write message to the message queue
				lock.writeLock().lock();
				try {
					db.add(inmsg);
				} finally {
					lock.writeLock().unlock();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
