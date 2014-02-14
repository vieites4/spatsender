package its.app.spat.sender.utils;
import its.app.spat.sender.bundle.Activator;
//import its.app.spat.sender.thread.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import javax.swing.Timer;
public class RegulatorConection extends Thread  {
	//Timer timer100;
	int first=0;
	public byte[][] message_end;
	public List<typeTemp> List_temp=new ArrayList<typeTemp> ();
	public List<Integer> List_ID=new ArrayList<Integer> ();
	public int intersectionId;
	public int[] arrayTLtopo;
	private boolean connect=true;
	Timer timer1,timer2;
	Socket requestSocket=null;//=new Socket();
	ObjectOutputStream out;
	listener list_thread;
	boolean newConnection;
	//Thread list_t;
	BufferedReader br;
	//ObjectInputStream in;//={0x02,0x00,0x03,0x33,0x04,0x05,0x06,0x03};
	InputStream in;
	int [] arrayGlobal;
	int intersectionIdGlobal;
	private boolean isRunning = true;
	listen_and_ack listen=new listen_and_ack();
	Functions functions = new Functions();
	byte ACK=0x06;
	int first1=0;
	public RegulatorConection() {
		//RegulatorConection client = new RegulatorConection();
		// client.run();
		//isRunning = true;
		// sending();
	}
	/** **/
	/**	Timer timer1 = new Timer (10000, new ActionListener () 
	{ 
	    public void actionPerformed(ActionEvent e) 
	    { 
	    	timer10.run();
	        // Aquí el código que queramos ejecutar.
	     } 
	}); **/
	TimerTask timer100 = new TimerTask()
	{
		/**
		 * Método al que Timer llamará cada segundo. Se encarga de avisar
		 * a los observadores de este modelo.
		 */
		public void run() 
		{
			// timer100.cancel();
			try {//timer2.stop();
				requestSocket.close();
			} catch (IOException e) {			}
			run();
		}
	};
	TimerTask timer10 = new TimerTask()
	{
		/**
		 * Método al que Timer llamará cada segundo. Se encarga de avisar
		 * a los observadores de este modelo.
		 */
		public synchronized void run() {
			try {
				requestSocket.close();
				//	new Thread(list_thread).
				//	System.out.println("Pechei a conexión!");
				//		this.notify();
				run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	Reception recep;
	public  int listener_ack() throws IOException {
		byte[] message = new byte[1000];
		//	byte[] msg=new byte[7];
		//timer1.stop();
		in=requestSocket.getInputStream();
		int length = in.read(message);                  // read length of incoming message
		//	System.out.println("ack");
		if (length>0){ 		
			boolean desired=true;//(message[0]==0x2); // & (message[6]==0x03) ;//& (message[3]==ACK)
			if (desired ==true){
				//if(in.ready()) mesa//recep.put(in.toString().toCharArray());
				//char[] message=recep.get();
				//	length=message.length;
				//Timer timer=new Timer(10000,null);
				//br.read(message, 0, message.length);
				//	timer1.stop();
				/**	timer2 = new Timer (100000, new ActionListener () 
				{ 
					public void actionPerformed(ActionEvent e) 
					{ 
						timer100.run();
						// Aquí el código que queramos ejecutar.
					} 
				});**/
				//timer2 = new Timer();
			}/**else if (message[3]==2)  { // msg=createACK((byte)2,(byte)0);
			//sendMessage(msg);
			return 1;}**/

		}else {return 1;}
		return 0;
	}
	byte[][] listener_response() throws IOException{//int[] tl
		//System.out.println("entro no listener_response");
		byte[] message = new byte[1000];
		
		
		in=requestSocket.getInputStream();
		int length=0;
		while(length==0)length=in.read(message);    
		for (int i = 0; i < length; i++) {
			System.out.println("message "+message[i]);
		}
		
		
		if (length>0)System.out.println("message rx");
		boolean desired=(message[0]==0x2) && (message[6]==0x03) && (message[3]==ACK);
		byte[] message1=new byte[100];//=message;
		if (length>7)message1=new byte[length -7];
		if (desired ==true){
			System.out.println("ack recibido");

			if (length>7){
				for (int i = 0; i < length - 7; i++) {
					message1[i]=message[7+i];
				}
				
			} else	length=in.read(message1);
		}
		byte[][]message_response=new byte[9][8];//o 9 é un exemplo, teño que poñer o axeitado
		desired=(message1[0]==0x02)&& (message1[3]==2);
		//System.out.println(message[0] + message[3] );
		//in.read(message, 0, message.length);
		while (desired==false){return null;}
		if (desired==true){
			//isRunning=true;//con first==1??
			//listen.interrupted();
			//timer100.stop();
			int lon0=message1[1]&(0xff);
			int lon1=message1[2]&(0xff);
			int lon= lon0*256+lon1;
			int result=0;
			if ((length - 4)!= lon)  result=1;else
				if (message1[3]!=2) result=2;
			byte[] msg=createACK((byte)2,(byte)result);
			sendMessage(msg);
			int i=0;int next_pos=0;int old_pos=0;int a=0;
			while(a<message1.length){	//i<tl.length
				System.out.println("aqui entrei "+length+ " " + message1[4+9 +next_pos]);
				//cambio esto porque agora xa non sei o que espero.
				if (message1[4+9 +next_pos]!=0){
				message_response[i][0]=message1[4+8 +next_pos];//System.out.println("grupo "+message[4+8+ next_pos]);
				message_response[i][1]=message1[4+10 + next_pos];//System.out.println("color "+message[4+10 + next_pos]);
				message_response[i][2]=message1[4+11+ next_pos];//System.out.println("veo2 "+message[4+11+ next_pos]);
				message_response[i][3]=message1[4+12+ next_pos];//System.out.println("veo2 "+message[4+12+ next_pos]);
				message_response[i][4]=message1[4+13+ next_pos];//System.out.println("voeo-1? "+message[4+13+ next_pos]);
				message_response[i][5]=message1[4+14+ next_pos];
				message_response[i][6]=message1[4+15+ next_pos];
				message_response[i][7]=message1[4+16+ next_pos];
				
				typeTemp e=new typeTemp();
				e.ID=message_response[i][0];
				byte[] b=new byte[2];
				
				b[0]=message1[4+12+ next_pos];
				b[1]=message1[4+11+ next_pos];
				System.out.println(b[0]+" "+b[1]);
				String s=new String(b,"UTF-8");
				e.Timer_last=((b[1]&0xff) * 256)+ b[0]&0xff;
				e.color=message_response[i][1];
				
				int num=0;
				if(this.List_ID.contains(message_response[i][0])==false)this.List_temp.add(e);else {
					num=this.List_ID.indexOf(message_response[i][0]);
					this.List_temp.set(num, e);					
				}
				System.out.println("ver lista");
				for (int j = 0; j < this.List_temp.size(); j++) {
					System.out.println("tempos na lista"+this.List_temp.get(i).Timer_last );
				}}
				next_pos=((int)message1[9+4+old_pos]*7) +2+old_pos;
				old_pos=next_pos;
				
				i++;
				a=a+next_pos;
			}}
		return(message_response);
	}


	public  void run() {
		arrayGlobal= arrayTLtopo;
		intersectionIdGlobal=intersectionId;
		// Creating a socket to connect to the server
	///	//Activator.spatRegIp
		boolean go=true;
		if (requestSocket==null){this.connect=false;System.out.println("antes do while(!connect) "+Activator.spatRegIp);}
		while((this.connect==false) && (this.isRunning==true)){
			try {
				if (requestSocket==null) {System.out.println("antes do while(!connect)");requestSocket = new Socket("195.77.187.234", 21000);newConnection=true;}else
					if (requestSocket.isClosed()){requestSocket = new Socket(Activator.spatRegIp, 21000);newConnection=true;}else newConnection=false;
				out = new ObjectOutputStream(requestSocket.getOutputStream());
				System.out.println("conexión establecida");
				//if (requestSocket.isConnected())
					this.connect=true;
			} catch (IOException e) {
				System.out.println("conexion imposible");
				//e.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				//	e1.printStackTrace();
				}	}		}
		//requestSocket.close();
		//	Socket connection = requestSocket.accept();
		//list_thread=new listener(requestSocket);
		//list_t= new Thread(list_thread);
		//list_t.start();
		// // Get Input and Output streams
		//in = new InputStreamReader(System.in);
		//	br = new BufferedReader(in);
		//out.flush();
		//in = new ObjectInputStream(requestSocket.getInputStream());
		if(newConnection==true){	byte[] msg = createPetitionTlTimes(intersectionId, arrayTLtopo); newConnection=false;//porque 0x55
		//	
		//listen.interrupt();
		/**for (int i = 0; i < msg.length; i++) {
			System.out.println(msg[i]);
		}**/
		
		sendMessage(msg);
		System.out.println("msg sent");
		}
	

		// aqui vai o temporizador de 10 segundos
		//	BufferedReader inFromServer=null;
		//		inFromServer=new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
		/**	Timer timer1 = new Timer (10000, new ActionListener () 
			{ 
				public synchronized void actionPerformed(ActionEvent e) 
				{ 
					timer10.run();
					// Aquí el código que queramos ejecutar.
				} 
			}); **/
		//timer1.setRepeats(true);
		//timer1.restart();
		//	timer1.notifyAll();
		/**	try {
				timer1.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
			//	e1.printStackTrace();
			}**/
		//timer1.notify();
		//while(listener_ack()==1){}
		//thread1.setPriority(Thread.MIN_PRIORITY);
		//thread1.start();
		//thread1.notifyAll();
		//if(thread1.isAlive()) thread1.join();
		//synchronize
		//	this.notify();
		//	timer2.start();
		/**	for (int i = 0; i < arrayTLtopo.length; i++) {
				System.out.println("tl"+arrayTLtopo[i]);
			}**/
		//	SenderThread a=new SenderThread();
		while(go && this.isRunning){
			message_end=null;
			try {
				message_end=listener_response();//arrayTLtopo
				if (message_end==null){
					//requestSocket.close();go=false;
					//System.out.println("volvo por esto");
					//run();
					}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				go=false;
				if (this.isRunning){	System.out.println("volvo por esto2");run();}
			}
			//Thread listen_t=new Thread(new listen_and_ack());
			//if (first1==0)listen.start();isRunning=true;
			first1=1;
			//return valores
			//	requestSocket.close();
		}
		//	return;
		//proba	
		/**	int time0=0;
		String num=Integer.toString(time0);
		byte[] aa=num.getBytes();
		byte rr='R';byte rs='A';
		//byte[][] message_end={{0x01,rr,aa[0],aa[1],0x04,0x05,0x06,0x07},{0x01,rs,aa[0],aa[1],0x04,0x05,0x06,0x07}};
		//proba. cambiar por message_end=null; cando remate	
		System.out.println("MALA CONEXION");
		//return null;**/

	}

	public byte[] createPetitionTlTimes(int idreg, int[] tlListInt) {

		byte ini = 0x02;
		byte end = 0x03;
		ByteBuffer b;
		//byte[] idregulator=b.array();
		int [] tlListInt1={8,9,10};
		int sizeTL = tlListInt1.length;
		int sizeMessage = 5 + 2 + sizeTL;
		int sizeMessageInfo = 3 + sizeTL;
		byte[] message_pet = new byte[sizeMessage];
		b=ByteBuffer.allocate(4);
		b.putInt(sizeMessageInfo);
		byte[] sizeMessageBytes=b.array();
		message_pet[0] = ini; // init byte
		message_pet[1] = sizeMessageBytes[2]; // byte up size
		message_pet[2] = sizeMessageBytes[3]; // byte down size
		message_pet[3] = 0x1; // message id
		/*************** INFO *********/
		message_pet[4] = 0;//idregulator[2]; // byte up regulator id
		message_pet[5] = 1;//idregulator[3]; // byte down regulator id
		for (int i = 0; i < tlListInt1.length; i++) {
			message_pet[i + 6] = (byte)tlListInt1[i];
		}
		/******************************/
		message_pet[sizeMessage - 1] = (byte) end; // end of message
		return message_pet;
	}

	public byte[] createACK(byte id, byte ack) {
		byte ini = 0x02;
		byte end = 0x03;
		byte[] message_ack = new byte[7];
		message_ack[0] = ini;// init byte
		message_ack[1] = 0;//sizeMessageBytes[1]; // byte up size
		message_ack[2] = 3;//sizeMessageBytes[0]; // byte down size
		message_ack[3] = 0x6; // message id
		/********** INFO ******/
		message_ack[4] = ack;
		message_ack[5] = id;
		/**********************/
		message_ack[6] = end; // end of message
		return message_ack;
	}

	public void decodifyPetitionTlTimes(byte[] msg) {

	}

	public void sendMessage(byte[] msg) {
		try {
			// for (int i = 0; i < msg.length; i++) {
			// out.writeByte(msg[i]);// }
			out.write(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void close() {
		try {
			System.out.println("ENTRO EN CLOSE");
			in.close();
			out.close();
		//	isRunning=false;
			requestSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	public void close_reg(){
		
		this.isRunning=false;
		close();
		
	}
}