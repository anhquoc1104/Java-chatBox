import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class chatBox extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	private InetAddress host;
	private int port;
	public static String name = null;
	private static ReadClient read = null;
	private static WriteClient write = null;
	private JTextField txtInput;
	private JTextField txtName;
	private JTextArea txtArea;
	private boolean clicked = false;
	private static chatBox frames = null; //new chatBox();
	
	public chatBox(InetAddress host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void execute(String name) throws IOException {
		Socket client = new Socket(host, port);
		read = new ReadClient(client);
		read.start();
		write = new WriteClient(client, name);
		write.start();
	}

	static class ReadClient extends Thread{
		private Socket client;
		public ReadClient(Socket client) {
			this.client = client;
		}
		@Override
		public void run() {
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(client.getInputStream());
				while (true) {
					String sms = dis.readUTF();
					System.out.println(sms);
					if (frames.txtArea != null) {
						frames.txtArea.append(sms + "\n");
					}
				}
			} catch (Exception e) {
				try {
					System.out.println("client close...");
					System.out.println(e.getMessage());
					//dis.close();
					client.close();
				} catch (IOException ex) {
					System.out.println("Disconnect Server...");
				}
			}
		}
	}
	static class WriteClient extends Thread{
		private Socket client;
		private String name;
		public WriteClient(Socket client, String name) {
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
		
		public void sendMessaage(String message) {
			DataOutputStream dos = null;
			try {
				System.out.println(message);
				dos = new DataOutputStream(client.getOutputStream());
				dos.writeUTF(name + ": " + message);
			} catch (Exception e) {
				// TODO: handle exception
				try {
					//dos.close();
					client.close();
				} catch (IOException e2) {
					System.out.println("Disconnect Server...");
				}
			}
		}
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frames = new chatBox();
					frames.setVisible(true);
					name = (String)JOptionPane.showInputDialog(frames, "Nhập Nickname Của Bạn: ", "Input Name", JOptionPane.PLAIN_MESSAGE);
					frames.txtName.setText(name);
					//frames.setP
					chatBox client = new chatBox(InetAddress.getLocalHost(), 8000);
					client.execute(name);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public chatBox() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setBounds(100, 100, 507, 498);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		txtInput = new JTextField("chat here...");
		txtInput.setAutoscrolls(false);
		txtInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String str = txtInput.getText();
				if (write != null) {
					write.sendMessaage(str);
					txtArea.append(name + ": " + str.toString() + "\n");
					txtInput.setText("");
				}
			}
		});
		txtInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if(!clicked){ 
			    	   clicked=true; 
			    	   txtInput.setText(""); 
			       }
			}
		});
		txtInput.setToolTipText("");
		txtInput.setColumns(10);
		//txtInput.setPlaceholder("");
		txtInput.addMouseListener(new MouseAdapter(){ 
			@Override 
		    public void mousePressed(MouseEvent e){ 
		       if(!clicked){ 
		    	   clicked=true; 
		    	   txtInput.setText(""); 
		       } 
			}
		});
		//Scrollbar s = new Scrollbar();
		txtArea = new JTextArea();
		txtArea.setEditable(false);
		txtArea.setWrapStyleWord(true);
		txtArea.setDisabledTextColor(SystemColor.desktop);
		txtArea.setSelectedTextColor(SystemColor.inactiveCaptionBorder);
		txtArea.setSelectionColor(SystemColor.desktop);
		txtArea.setBackground(SystemColor.text);
		txtArea.setFont(new Font("Dialog", Font.PLAIN, 16));
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		txtArea.setColumns(20);
		txtArea.setRows(2);
		//txtArea.add(s);
//		JScrollPane jScrollPane1 = new JScrollPane(txtArea);
//		jScrollPane1.setViewportView(txtArea);
//		jScrollPane1.setAutoscrolls(true);
//		jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//		getContentPane().add(jScrollPane1);
		
		JButton btnEnter = new JButton("ENTER");
		btnEnter.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JButton btnExit = new JButton("EXIT");
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (write != null) {
					write.sendMessaage("exit");
				} else {
					System.out.println("write null");
				}
				System.exit(0);
			}
		});
	
		btnEnter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String str = txtInput.getText();
				// broadcast
				if (write != null) {
					write.sendMessaage(str);
					txtArea.append(name + ": " + str.toString() + "\n");
					txtInput.setText("");
				}
				//String sms = read.sendSmsClinet();
				//txtArea.append(sms + "\n");
			}
		});
		
		txtName = new JTextField();
		txtName.setBorder(null);
		txtName.setDisabledTextColor(new Color(0, 100, 0));
		txtName.setSelectionColor(SystemColor.control);
		txtName.setBackground(SystemColor.control);
		txtName.setFont(new Font("Tahoma", Font.BOLD, 20));
		txtName.setEnabled(false);
		txtName.setColumns(10);
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(txtArea, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(txtInput, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnEnter))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(txtName, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(btnExit)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnExit, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtArea, GroupLayout.PREFERRED_SIZE, 332, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(txtInput)
						.addComponent(btnEnter, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
