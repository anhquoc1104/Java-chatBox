package chatbox;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private InetAddress host;
	private int port;
	
	public Client(InetAddress host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void execute() throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.print("Nhập tên của bạn: ");
		String name = sc.nextLine();
		Socket client = new Socket(host, port);
		ReadClient read = new ReadClient(client, name);
		read.start();
		WriteClient write = new WriteClient(client, name);
		write.start();
	}
	
	public static void main(String[] args) throws IOException {
		Client client = new Client(InetAddress.getLocalHost(), 8000);
		client.execute();
	}
}

class ReadClient extends Thread{
	private Socket client;
	private String name;
	public ReadClient(Socket client, String name) {
		this.client = client;
	}
	@Override
	public void run() {
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(client.getInputStream());
			while (true) {
				String sms = dis.readUTF();
				System.out.println(name + ": " + sms);
			}
		} catch (Exception e) {
			try {
				dis.close();
				client.close();
			} catch (IOException ex) {
				System.out.println("Disconnect Server...");
			}
		}
	}
}

class WriteClient extends Thread{
	private Socket client;
	private String name;
	public WriteClient(Socket client, String name) {
		// TODO Auto-generated constructor stub
		this.client = client;
		this.name = name;
	}
	
	@Override
	public void run() {
		DataOutputStream dos = null;
		Scanner sc = null;
		try {
			dos = new DataOutputStream(client.getOutputStream());
			sc = new Scanner(System.in);
			while(true) {
				String sms = sc.nextLine();
				dos.writeUTF(name + ": " + sms);
			}
		} catch (Exception e) {
			// TODO: handle exception
			try {
				dos.close();
				client.close();
			} catch (IOException e2) {
				System.out.println("Disconnect Server...");
			}
		}
	}
}
