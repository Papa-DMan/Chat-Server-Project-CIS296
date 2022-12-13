//Daniel Mahaney and Spencer Schneider
package com.example.cis296project4;

//Data structure to hold information about messages
public record Message(
		int clientId,
		int messageLength,
		byte[] messageData
) { }
