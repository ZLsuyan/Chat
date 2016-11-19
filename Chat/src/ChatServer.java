import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * 写一个基于网络版的聊天室
 * 可以实现多个客户端
 * 【注意】：如果想在一个外部包装类ChatServer的静态方法中new一个内部类，是很麻烦的。
 *          有两种解决办法:
 *           1、先new一个外部包装类，然后再接着.new一个内部类：
 *              例如： new ChatServer().new Client();
 *           2、直接在外部包装类内部写一个方法start()，这个方法中可以new一个内部类，
 *              然后让外部包装类的静态方法main()去调用这个方法start()。
 *           第二种方法是较常用方法。
 * @author zengli
 * @date 2016/5/31
 */
public class ChatServer {
	boolean started = false;
	ServerSocket ss = null;
	
	List<Client> clients = new ArrayList<Client>();
	
	public static void main(String[] args) {
		new ChatServer().start();
			
	}
		
	//在外部包装内中定义一个start()方法，然后在start()方法中new一个内部类。
	public void start(){
		try {
			ss = new ServerSocket(8888);
			started = true;
		}catch(BindException e){
			System.out.println("端口使用中...");
			System.out.println("请关掉相关程序，并重新运行服务器！");
			System.exit(0);
		}catch (IOException e){
			e.printStackTrace();
		}
		
		try{
			while(started){
				boolean bConnected = false;
				Socket s = ss.accept();
				
				//启动一个单独的线程
				Client c = new Client(s);
				clients.add(c);
System.out.println("a client connected!");
                new Thread(c).start();
			 	//dis.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				if(ss != null){
					ss.close();
				} 
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 线程类：代表客户端在服务器这边的一个包装;
	 * 内部类可以直接使用它的包装类的成员变量或成员方法
	 */
	class Client implements Runnable{
		private Socket s ;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private  boolean bConnected = false;
		
		public Client(Socket s){
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (NullPointerException e) {
				clients.remove(this);
				System.out.println("对方退出了！我把它从list中去除了");
			//	e.printStackTrace();
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("对方退出了！我把它从list中去除了");
			//	e.printStackTrace();
			}
		}
		
		public void run() {
			try {
				while(bConnected){
					String str = dis.readUTF();
System.out.println(str);
					for(int i=0;i<clients.size();i++){
						Client c = clients.get(i);
						//当其中某一个客户端已经关闭时，它在集合中还在，你再send时就会报错，因为其实它已经关闭了。
						c.send(str);
					}
					
					//Iterator会进行锁定，但是发东西不是修改，没必要锁定，而且锁定会效率低。
					/*
					for(Iterator<Client> it = clients.iterator();it.hasNext();){
						Client c = it.next();
						c.send(str);
					}
					*/
					
					/*
					Iterator<Client> it = clients.iterator();
					while(it.hasNext()){
						Client c = it.next();
						c.send(str);
					}
					*/
					
				}
			}catch(SocketException e){
				clients.remove(this);
				System.out.println("a client quit!");
			}catch(EOFException e){
				System.out.println("Client closed!");
			}catch (Exception e) {			
				e.printStackTrace();	
			} finally {
				try {
					if(dis != null){
						dis.close();
						dis = null;
					}
					if(dos != null){
						dos.close();
						dos = null;
					}
					if(s != null){
						s.close();
						s = null;
					}				
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		}
		
	}
}
