package its.app.spat.sender.utils;

//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.io.BufferedReader;
//import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
import java.util.TimerTask;

import javax.swing.Timer;

//import javax.swing.Timer;
public class RegulatorConection {
	//Timer timer100;
	int first=0;
	Timer timer1,timer2;
	Socket requestSocket=null;
	ObjectOutputStream out;
	listener list_thread;
	//Thread list_t;
	BufferedReader br;
	//ObjectInputStream in;//={0x02,0x00,0x03,0x33,0x04,0x05,0x06,0x03};
	InputStream in;
	int [] arrayGlobal;
	int intersectionIdGlobal;
	private boolean isRunning = false;
	listen_and_ack listen=new listen_and_ack();
	Functions functions = new Functions();
	byte ACK=0x06;
	int first1=0;
	public RegulatorConection() {
		// RegulatorConection client = new RegulatorConection();
		// client.run();
		isRunning = true;
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

			} catch (IOException e) {
				// TODO Auto-generated catch block
				//	e.printStackTrace();
			}
			try {
				sending(arrayGlobal,intersectionIdGlobal);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
				System.out.println("ENTRO NA TASK!");
				//list_t.interrupt();//list_t.stop();
				//	list_thread.Continue=false;
				requestSocket.close();
				//	new Thread(list_thread).
				System.out.println("Pechei a conexión!");
				//		this.notify();
				sending(arrayGlobal,intersectionIdGlobal);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	};
	Reception recep;
	public  int listener_ack() throws IOException {
		//int length=0;
		byte[] message = new byte[1000];
		//	byte[] msg=new byte[7];
		//timer1.stop();
		in=requestSocket.getInputStream();
		int length = in.read(message);                  // read length of incoming message
		//	System.out.println("ack");
		if (length>0){ 		/**for (int j = 0; j < length; j++) {
			System.out.println(message[j]);
		}**/
			//& (message[5]==0x1) 
			boolean desired=true;//(message[0]==0x2); // & (message[6]==0x03) ;//& (message[3]==ACK)
			if (desired ==true){
				System.out.println("ack recibido");

				//if(in.ready()) mesa//recep.put(in.toString().toCharArray());
				//char[] message=recep.get();
				//	length=message.length;
				// read length of incoming message

				//	if(length>0) {

				//br.read(message, 0, message.length);
				//boolean desired=message[3]==ACK;		
				//	if (desired==true){
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
	byte[][] listener_response(int[] tl) throws IOException{
		//System.out.println("entro no listener_response");
		byte[] message = new byte[1000];
		in=requestSocket.getInputStream();
		int length=in.read(message);                  // read length of incoming message
		//if(length>0) {
		/**for (int i = 0; i < length; i++) {
				System.out.println(message[i]);
			}**/

		boolean desired=message[0]==0x2& (message[6]==0x03) & (message[3]==ACK);
		if (desired ==true){
			System.out.println("ack recibido");
			length=in.read(message);
		}
		byte[][]message_response=new byte[tl.length][8];
		desired=message[0]==0x02& (message[3]==2);
		//in.read(message, 0, message.length);
		while (desired==false){return null;}
		if (desired==true){
			//isRunning=true;//con first==1??
			//listen.interrupted();
			//timer100.stop();
			int lon0=message[1]&(0xff);
			int lon1=message[2]&(0xff);
			int lon= lon0*256+lon1;
			int result=0;
			if ((length - 4)!= lon)  result=1;else
				if (message[3]!=2) result=2;

			byte[] msg=createACK((byte)2,(byte)result);
			sendMessage(msg);
			int i=0;int next_pos=0;int old_pos=0;
			while(i<tl.length){	
				//		System.out.println("que collo "+tl[i]+" "+message[4+8 +next_pos]);
				//	j=i;
				//	while(!well){

				//	if(message[4+8 +next_pos]==tl[j]){

				message_response[i][0]=message[4+8 +next_pos];System.out.println("grupo "+message[4+8+ next_pos]);
				message_response[i][1]=message[4+10 + next_pos];System.out.println("color "+message[4+10 + next_pos]);
				message_response[i][2]=message[4+11+ next_pos];System.out.println("veo2 "+message[4+11+ next_pos]);
				message_response[i][3]=message[4+12+ next_pos];System.out.println("veo2 "+message[4+12+ next_pos]);
				message_response[i][4]=message[4+13+ next_pos];System.out.println("voeo-1? "+message[4+13+ next_pos]);
				message_response[i][5]=message[4+14+ next_pos];
				message_response[i][6]=message[4+15+ next_pos];
				message_response[i][7]=message[4+16+ next_pos];
				next_pos=((int)message[9+4+old_pos]*7) +2+old_pos;
				old_pos=next_pos;
				//	}
				//	j=(j+1)%(tl.length-1);
				//	}
				i++;
			}}

		return(message_response);

		//}else listener_response(tl);

	}



	public  byte[][] sending(int[] arrayTLtopo,int intersectionId) throws InterruptedException {
		arrayGlobal= arrayTLtopo;
		intersectionIdGlobal=intersectionId;

		// Creating a socket to connect to the server
		//System.out.println("intersectionId"+intersectionId);
		/*for (int i = 0; i < arrayTLtopo.length; i++) {
				System.out.println("arrayTLtopo "+arrayTLtopo[i]);
			} */
		this.isRunning=true;
		boolean connect=false;
		//
		while(!connect&& this.isRunning){
			try {
				
				if (requestSocket==null) requestSocket = new Socket("195.77.187.234", 21000);else
					if (requestSocket.isClosed())requestSocket = new Socket("195.77.187.234", 21000);
				out = new ObjectOutputStream(requestSocket.getOutputStream());
				connect=true;
			} catch (IOException e) {
				System.out.println("conexion imposible"+this.isRunning);
			}
		}
		//listener.
if(this.isRunning==false){return null;}
		//requestSocket.close();
		//	Socket connection = requestSocket.accept();
		//list_thread=new listener(requestSocket);
		//list_t= new Thread(list_thread);
		//list_t.start();
		System.out.println("Connected to host in port 13500");
		// // Get Input and Output streams
		//
		//in = new InputStreamReader(System.in);

		//	br = new BufferedReader(in);

		//out.flush();
		//in = new ObjectInputStream(requestSocket.getInputStream());
		byte[] msg = createPetitionTlTimes(intersectionId, arrayTLtopo); //porque 0x55
		System.out.println("Create petition");
		//isRunning=false;//listen.interrupt();
		sendMessage(msg);
		System.out.println("msg sent");

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
		byte[][] message_end=null;
		try {
			message_end=listener_response(arrayTLtopo);
			if (message_end==null){
				requestSocket.close();return null;}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Thread listen_t=new Thread(new listen_and_ack());
		//if (first1==0)listen.start();isRunning=true;
		first1=1;
		//return valores
		//	requestSocket.close();

		return(message_end);
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

		//		System.out.println("entro en createPetitionTlTimes"+idreg);

		ByteBuffer b=ByteBuffer.allocate(4);
		b.putInt(idreg);
		byte[] idregulator=b.array();
		//System.out.println("idregulator[0] mau");
		//System.out.println(idregulator[3]);
		//System.out.println(idregulator[2]);
		int sizeTL = tlListInt.length;
		int sizeMessage = 5 + 2 + sizeTL;
		int sizeMessageInfo = 3 + sizeTL;

		byte[] message_pet = new byte[sizeMessage];
		b=ByteBuffer.allocate(4);
		b.putInt(sizeMessageInfo);
		byte[] sizeMessageBytes=b.array();

		/**	System.out.println("sizeMessageBytes[0]");
		System.out.println(sizeMessageBytes[3]);
		System.out.println(sizeMessageBytes[2]);
		System.out.println(sizeMessageInfo);**/
		message_pet[0] = ini; // init byte
		message_pet[1] = sizeMessageBytes[2]; // byte up size
		message_pet[2] = sizeMessageBytes[3]; // byte down size
		message_pet[3] = 0x1; // message id
		/*************** INFO *********/
		message_pet[4] = 0;//idregulator[2]; // byte up regulator id
		message_pet[5] = 1;//idregulator[3]; // byte down regulator id
		//System.out.println("client>" + message[0]+message[1]+message[2]+message[3]);
		for (int i = 0; i < tlListInt.length; i++) {
			message_pet[i + 6] = (byte)tlListInt[i];//System.out.println(tlListInt[i]);
		}
		/******************************/
		message_pet[sizeMessage - 1] = (byte) end; // end of message

		// Paint msg
		/*for (int i = 0; i < message_pet.length; i++) {
			System.out.println("Mensaje de petición " + message_pet[i]);
		}*/
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
		/*for (int i = 0; i < message_ack.length; i++) {
			//System.out.println("Mensaje de ack " + i + " " + message_ack[i]);
		}*/
		return message_ack;
	}

	public void decodifyPetitionTlTimes(byte[] msg) {

	}

	public void sendMessage(byte[] msg) {
		try {
			// for (int i = 0; i < msg.length; i++) {
			// out.writeByte(msg[i]);
			// }
			out.write(msg);
			out.flush();
			//System.out.println("client>" + msg.toString());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void close() {
		try {
			in.close();
			out.close();
		
			requestSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void close_reg() {
		this.isRunning=false;
		System.out.println("Close RegulatorConection");
	}
}