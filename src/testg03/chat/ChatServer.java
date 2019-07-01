package testg03.chat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class ChatServer extends Frame implements ActionListener{

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
		//chatStart()메소드 호출
		chatStart();
		
	}
	public void chatStart() {
		//소켓 생성
		try {
			ss=new ServerSocket(6005);
			while (true) {
				sockClient=ss.accept();
				ta.append(sockClient.getInetAddress().getHostAddress()+"접속함\n");
				//접속자의 ip얻기
				ChatHandle threadChat=new ChatHandle();
				vChatList.add(threadChat);
				threadChat.start();
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
	class ChatHandle extends Thread{
		BufferedReader br=null;//입력당담
		PrintWriter pw=null;//출력담당
		
		public ChatHandle() {
			try {
				InputStream is=sockClient.getInputStream();//입력
				br=new BufferedReader(new InputStreamReader(is));
				OutputStream os=sockClient.getOutputStream();//출력
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
				while (true) {//채팅내용받기
					String msg=br.readLine();//
					String str=sockClient.getInetAddress().getHostName();
					ta.append(msg+"\n");//채팅내용을 ta에 추가
					if (msg.equals("@@Exit")) {
						break;
					}else {
						sendAllClient(name+" : "+msg);
						//접속자 모드에게 메세지 전달
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				vChatList.remove(this);
				try {
					br.close();
					pw.close();
					sockClient.close();
				} catch (IOException e) {
				}//catch
			}//finally
		}//run
	}

}
