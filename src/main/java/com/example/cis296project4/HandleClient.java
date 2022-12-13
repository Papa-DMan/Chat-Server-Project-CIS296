//Daniel Mahaney and Spencer Schneider
package com.example.cis296project4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;

class HandleClient implements Runnable {
	private Socket socket; // A connected socket
	private int clientIndex;

	private ArrayList<Message> db; //reference the queue database
	private ReadWriteLock lock; //reference to the database lock



	/* Construct a thread */
	public HandleClient(Socket socket, int clientNo, ArrayList<Message> db, ReadWriteLock lock) {
		this.socket = socket;
		this.clientIndex = clientNo;
		this.lock = lock;
		this.db = db;
	}


	/**
	 * Run a thread
	 */
	public void run() {
		try {
			// Create data input and output streams
			DataInputStream inputFromClient = new DataInputStream(
					socket.getInputStream());
			DataOutputStream outputToClient = new DataOutputStream(
					socket.getOutputStream());

			// Spawn reading threads
			new Thread(new ServerReader(inputFromClient, outputToClient, db, lock, clientIndex)).start();

			boolean justRead = false;

			while (true) {
				//acquire the reading lock to read from the queue database
				lock.readLock().lock();
				try {
					if (db.size() > 0) { //if the queue database is not empty
						if (db.get(0).clientId() != clientIndex) { //if the latest message in the db queue is not from our client
							//write our message structure to the socket | (int)size  byte[size]
							outputToClient.writeInt(db.get(0).messageLength());
							outputToClient.write(db.get(0).messageData());
							// log the conversation to the console
							String logmsg = new String(db.get(0).messageData());
							System.out.println("Client " + db.get(0).clientId() + ": " + logmsg);
							justRead = true; //set the just read flag to enable the remove code to run
						}
					}
				}
				finally {
					lock.readLock().unlock();
				}
				if (justRead) { //remove code | if we just wrote a message then remove it from the queue
					lock.writeLock().lock(); //acquire the db writing lock
					try {
						db.remove(0); //thread safe remove because of lock
					}
					finally {
						justRead = false; //disable the justRead flag
						lock.writeLock().unlock(); //unlock the db
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

