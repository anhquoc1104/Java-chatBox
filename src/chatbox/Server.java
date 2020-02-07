package chatbox;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Server {
	private int port;
	public static ArrayList<Socket> listSK;
	
	public Server(int port) {
		// TODO Auto-generated constructor stub
		this.port = port;
	}
	
	private void execute() throws IOException {
		ServerSocket server = new ServerSocket(port);
		WriteServer write = new WriteServer();
		//WriteServer always Start
		write.start();
		System.out.println("Connecting...");
		//doi client ket noi
		while (true) {
			Socket socket = server.accept();
			System.out.println("Connected: " + socket);
			Server.listSK.add(socket);
			ReadServer read = new ReadServer(socket);
			read.start();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Server.listSK = new ArrayList<>();
		Server server = new Server(8000);
		server.execute();
	}
}

class ReadServer extends Thread{
	private Socket socket;
	public ReadServer(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(socket.getInputStream());
			while (true) {
				String sms = dis.readUTF();
				//ngat ket noi
				if(sms.contains("exit")) {
					//xoa trong arr
					Server.listSK.remove(socket);
					dis.close();
					System.out.println("Disconnected: " + socket);
					socket.close();
					continue;
				}
				for (Socket item : Server.listSK) {
					if(item.getPort() != socket.getPort()) {
						DataOutputStream dos = new DataOutputStream(item.getOutputStream());
						dos.writeUTF(sms);
					}
				}
				System.out.println(sms);
			}
		} catch (Exception e) {
			try {
				dis.close();
				socket.close();
			} catch (IOException e2) {
				// TODO: handle exception
				System.out.println("Disconnect Server...");
			}
		}
	}
}
class WriteServer extends Thread{
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		DataOutputStream dos = null;
		Scanner sc = new Scanner(System.in);
		while (true) {
			String sms = sc.nextLine(); //wait server
				try {
					for (Socket item : Server.listSK) {
						dos = new DataOutputStream(item.getOutputStream());
						dos.writeUTF("SERVER: " + sms);
					}
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Loi: WriteServer");
					}
		}
	}
}

