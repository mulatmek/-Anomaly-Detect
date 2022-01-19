package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {
	int port;
	volatile int clientLimit;
	volatile int currentClientAmount;
	ClientHandler ch;
	public Server (int port, ClientHandler ch){
		this.ch=ch;
		this.port=port;
		this.clientLimit=3;
		this.currentClientAmount=0;
	}
	public interface ClientHandler{
		void handle(InputStream infFomClient ,OutputStream outToClient);
	}

	volatile boolean stop;
	public Server() {
		stop=false;
	}


	private void startServer(int port, ClientHandler ch) {
		try {
			ServerSocket server = new ServerSocket(port);
			server.setSoTimeout(1000);//if there is no client timeout

			while (!stop&&currentClientAmount<=clientLimit){
				try{
					Socket client =server.accept();
					//********************
					ch.handle(client.getInputStream(),client.getOutputStream());
					client.close();
					currentClientAmount++;
				}catch (SocketTimeoutException e){}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	// runs the server in its own thread
	public void start(int port, ClientHandler ch) {
		new Thread(()->startServer(port,ch)).start();
	}

	public void stop() {
		stop=true;
	}
}
