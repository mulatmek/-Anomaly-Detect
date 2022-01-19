package test;


import test.Commands.DefaultIO;
import test.Server.ClientHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AnomalyDetectionHandler implements ClientHandler{
	CLI menu;
	@Override
	public void handle(InputStream inFromClient ,OutputStream outToClient) {
		Socket client=new Socket();
		menu = new CLI(new SocketIO(inFromClient,outToClient));
		menu.start();
	}
	public class SocketIO implements DefaultIO{
		BufferedReader in;
		PrintWriter out ;
		public SocketIO(InputStream inFromClient ,OutputStream outToClient){
			in = new BufferedReader(new InputStreamReader(inFromClient));
			out = new PrintWriter(outToClient, true);
		}
		@Override
		public String readText() {
			try{
				return in.readLine();
			}
			catch (IOException e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void write(String text) {
			out.write(text);
			out.flush();
		}

		@Override
		public float readVal() {
			try{
				return in.read();
			}
			catch (IOException e){
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public void write(float val) {
			out.write((int) val);
			out.flush();
		}
		public void close() {
			try {
				in.close();
				out.close();
			}catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}


}
