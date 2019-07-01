package testg02;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer extends Frame implements ActionListener {
	
	Button btnExit;
	TextArea ta;
	Vector vChatList;
	ServerSocket ss;
	Socket sockClient;
	
	public ChatServer() {
		setTitle("채팅서버");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		vChatList=new Vector();
		btnExit=new Button("서버종료");
		btnExit.addActionListener(this);
		ta=new TextArea();
		add(ta, BorderLayout.CENTER);
		add(btnExit, BorderLayout.SOUTH);
		setBounds(250,250,200,200);
		setVisible(true);
		//chatStart() 메소드 호출
		chatStart();
	}
	
	public void chatStart() {
		//소켓 생성
		try {
			ss=new ServerSocket(5005);
			while (true) {
				sockClient=ss.accept();
				ta.append(sockClient.getInetAddress().getHostAddress()+" 접속함\n");
				//접속자의 ip 얻기
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		new ChatServer();
	}
	
	class ChatHandle extends Thread {
		BufferedReader br=null;
		PrintWriter pw=null; //출력 담당
		
		public ChatHandle() {
			try {
				//입력
				InputStream is=sockClient.getInputStream();
				br=new BufferedReader(new InputStreamReader(is));
				//출력
				OutputStream os=sockClient.getOutputStream();
				pw=new PrintWriter(new OutputStreamWriter(os));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void sendAllClient(String msg) {
			int size=vChatList.size();
			try {
				for (int i = 0; i < size; i++) {
					ChatHandle chr=(ChatHandle)vChatList.elementAt(i);
					chr.pw.println(msg);
					chr.pw.flush();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				String name=br.readLine();
				sendAllClient(name+"님께서 입장");
				while (true) { //채팅 내용 받기
					String msg=br.readLine();
					String str=sockClient.getInetAddress().getHostName();
					ta.append(msg+"\n"); //채팅내용을 ta에 추가
					if (msg.equals("@@Exit")) {
						break;
					} else {
						sendAllClient(name+" : "+msg);
						//접속자 모드에게 메시지 전달
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				vChatList.remove(this);
				try {
					br.close();
					pw.close();
					sockClient.close();
				} catch (IOException e) {
				} //catch
			} //finally
		} //run
	}

}
