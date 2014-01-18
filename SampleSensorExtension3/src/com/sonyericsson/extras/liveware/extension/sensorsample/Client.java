package com.sonyericsson.extras.liveware.extension.sensorsample;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public Socket socket;
	public BufferedReader reader;
	public PrintWriter writer;
	
	Client(Socket socket, BufferedReader reader, PrintWriter writer) {
		this.socket = socket;
		this.reader = reader;
		this.writer = writer;
	}
}