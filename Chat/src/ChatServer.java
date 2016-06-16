import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * дһ������������������
 * ����ʵ�ֶ���ͻ���
 * ��ע�⡿���������һ���ⲿ��װ��ChatServer�ľ�̬������newһ���ڲ��࣬�Ǻ��鷳�ġ�
 *          �����ֽ���취:
 *           1����newһ���ⲿ��װ�࣬Ȼ���ٽ���.newһ���ڲ��ࣺ
 *              ���磺 new ChatServer().new Client();
 *           2��ֱ�����ⲿ��װ���ڲ�дһ������start()����������п���newһ���ڲ��࣬
 *              Ȼ�����ⲿ��װ��ľ�̬����main()ȥ�����������start()��
 *           �ڶ��ַ����ǽϳ��÷�����
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
		

	public void start(){
		try {
			ss = new ServerSocket(8888);
			started = true;
		}catch(BindException e){
			System.out.println("�˿�ʹ����...");
			System.out.println("��ص���س��򣬲��������з�������");
			System.exit(0);
		}catch (IOException e){
			e.printStackTrace();
		}
		
		try{
			while(started){
				boolean bConnected = false;
				Socket s = ss.accept();
				
				//����һ���������߳�
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
	 * �߳��ࣺ����ͻ����ڷ�������ߵ�һ����װ;
	 * �ڲ������ֱ��ʹ�����İ�װ��ĳ�Ա�������Ա����
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
				System.out.println("�Է��˳��ˣ��Ұ�����list��ȥ����");
			//	e.printStackTrace();
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("�Է��˳��ˣ��Ұ�����list��ȥ����");
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
						//������ĳһ���ͻ����Ѿ��ر�ʱ�����ڼ����л��ڣ�����sendʱ�ͻᱨ����Ϊ��ʵ���Ѿ��ر��ˡ�
						c.send(str);
					}
					
					//Iterator��������������Ƿ����������޸ģ�û��Ҫ����������������Ч�ʵ͡�
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
