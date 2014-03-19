package its.app.spat.sender.utils;
import its.app.spat.sender.bundle.Activator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
public class RegulatorConection extends Thread  {
	//Timer timer100;
	int first=0;
	public byte[][] message_end;
	public List<typeTemp> List_temp=new ArrayList<typeTemp> ();
	public List<Integer> List_ID=new ArrayList<Integer> ();
	public int intersectionId;
	public int[] arrayTLtopo;  
	public boolean waitingACK=false; 
	public boolean waitingResponse=false;
	public boolean connect=true;
	public int  Temp10=20;
	public int Temp100=200;
	Timer timer1,timer2;
	Socket requestSocket=null;//=new Socket();
	ObjectOutputStream out;
	boolean newConnection;
	BufferedReader br;
	InputStream in;
	int [] arrayGlobal;
	int intersectionIdGlobal;
	private boolean isRunning = true;
	Functions functions = new Functions();
	byte ACK=0x06;
	int first1=0;
	public RegulatorConection() {
	}

	byte[][] listener_response() throws IOException{//int[] tl
		//in=requestSocket.getInputStream();
		byte[] message=null;
		System.out.println("recibo¿?");
		int length=0;
		while(length==0){in=requestSocket.getInputStream();length=in.available();    }
		if (length>0){
			message = new byte[length];
			length=in.read(message);
			System.out.println("message rx");
			this.Temp100=200;}
		boolean desired=(message[0]==0x2) && (message[6]==0x03) && (message[3]==ACK);
		

		byte[] message1=new byte[length];//=message;
		
		
		if (desired ==true){
			System.out.println("ack recibido" + length);
			this.Temp10=20;this.waitingACK=false;message1=new byte[length];
			if (length>7){message1=new byte[length -7];
			for (int i = 0; i < length - 7; i++) {
				message1[i]=message[7+i];
			}
			//} else	length=in.read(message1);
			}}else{message1=new byte[length];message1=message;}
		for (int i = 0; i < message1.length; i++) {
			System.out.print(""+ (int)message1[i]+ " ");
		}System.out.println(" message");
		
		
		if((desired&&(length==7))==false){
			byte[][]message_response=new byte[9][11];//o 9 é un exemplo, teño que poñer o axeitado
			boolean desired1=(message1[0]==0x02)&& (message1[3]==2);
			while (desired1==false){return null;}
			//if (desired1==true){
			this.waitingResponse=false; //tengo que incluir los errores de ack
			int length_mes;int id_m;
			int val=0;
			
			if (desired){length=length-7;if(message[7+2]<0)val=256+message[7+2];else val=(int)message[7+2];
			length_mes=message[7+1]*256+message[7+2];id_m=message[7+3];
			}else{if(message[2]<0)val=256+message[2];else val=(int)message[2];
			length_mes=message[1]*256+val;id_m=message[3];}
			int error=0;
			if((length-4)!=length_mes)error=1;else if(id_m!=2)error=2;
			byte[] msg1 = createACK((byte)2,(byte)0); 
			
		for (int i = 0; i < message1.length; i++) {
				System.out.print(message1[i]+ " ");
			}System.out.println();
			sendMessage(msg1);
			if (error==0){

				int i=0;int next_pos=0;int old_pos=0;int a=0;
				if(message1[13]!=0){
				while(a<(length-1)){	
					if (message1[4+9 +next_pos]!=0){
						typeTemp e=new typeTemp();
						message_response[i][0]=message1[4+8 +next_pos];System.out.println("grupo "+message[4+8+ next_pos]);
						message_response[i][1]=message1[4+10 + next_pos];System.out.println("color "+message[4+10 + next_pos]);
						message_response[i][2]=message1[4+11+ next_pos];//System.out.println("veo2 "+message[4+11+ next_pos]);
						message_response[i][3]=message1[4+12+ next_pos];//System.out.println("veo2 "+message[4+12+ next_pos]);
						message_response[i][4]=message1[4+13+ next_pos];//System.out.println("voeo-1? "+message[4+13+ next_pos]);
						message_response[i][5]=message1[4+14+ next_pos];
						message_response[i][6]=message1[4+15+ next_pos];
						message_response[i][7]=message1[4+16+ next_pos];
						if(message1[4+9 +next_pos]>1){
							message_response[i][8]=message1[4+17+ next_pos];
							message_response[i][9]=message1[4+18+ next_pos];
							message_response[i][10]=message1[4+19+ next_pos];
							byte[] b2=new byte[2];
							b2[0]=message1[4+19+ next_pos];
							b2[1]=message1[4+18+ next_pos];
							e.Timer_last2=((b2[1]&0xff) * 256)+ b2[0]&0xff;
							e.color2=message_response[i][8];
						}else{
							e.Timer_last2=0;
							e.color2=0;						
						}					
						e.ID=(int)message_response[i][0]&0xff;
						int[] b=new int[2];
						b[0]=(int)message1[4+12+ next_pos]&0xff;
						b[1]=(int)message1[4+11+ next_pos]&0xff;
						e.Timer_last=(b[1]* 256)+ b[0];
						e.color=message_response[i][1];
						e.first=true;
						int num=0;
						int io=0;boolean contains=false;
						while(io<this.List_ID.size() && contains==false){
							if (this.List_ID.get(io)==e.ID)contains=true;
							io++;
						}
						
						if(contains==false){System.out.println("entra no contains");this.List_temp.add(e);this.List_ID.add(e.ID);}else {
							num=io-1;
							this.List_temp.set(num, e);	System.out.println("entra CONTAINS");
						}
					}
					next_pos=((int)message1[9+4+old_pos]*7) +2+old_pos;
					old_pos=next_pos;
					i++;
					
					a=a+next_pos;
					//System.out.println("a "+a );
				}}else{message_response=null;}}
			for (int j = 0; j < this.List_temp.size(); j++) {
				System.out.println("tempos na lista "+this.List_ID.get(j)+" "+this.List_temp.get(j).Timer_last+ "color: "+this.List_temp.get(j).color);
			}System.out.println("envia message_response");
			return(message_response);
		}return(null);
	}


	public  void run() {
		arrayGlobal= arrayTLtopo;
		intersectionIdGlobal=intersectionId;
		boolean go=true;
		if (requestSocket==null){this.connect=false;}
		while((this.connect==false) && (this.isRunning==true)){
			try {
				if (requestSocket==null) {System.out.println("antes do while(!connect)");
				requestSocket = new Socket(Activator.spatRegIp, 21000);newConnection=true;}else
					if (requestSocket.isClosed()){requestSocket = new Socket(Activator.spatRegIp, 21000);newConnection=true;}else newConnection=false;
				out = new ObjectOutputStream(requestSocket.getOutputStream());
				System.out.println("conexión establecida");
				this.connect=true;
			} catch (IOException e) {	System.out.println("conexion imposible");
			try {
				Thread.sleep(1000);	} catch (InterruptedException e1) {	}			}
			if(newConnection==true){	byte[] msg = createPetitionTlTimes(intersectionId, arrayTLtopo); newConnection=false;//porque 0x55
			sendMessage(msg);	System.out.println("msg sent");
			this.waitingACK=true;
			this.waitingResponse=true;
			}
			while(go && this.isRunning){
				message_end=null;
				try {
					message_end=listener_response();//arrayTLtopo
					if (message_end==null){		}
				} catch (IOException e) {	go=false;}
				first1=1;			}}
	}

	public byte[] createPetitionTlTimes(int idreg, int[] tlListInt) {

		byte ini = 0x02;
		byte end = 0x03;
		ByteBuffer b;
		//byte[] idregulator=b.array();
		int [] tlListInt1={1,2,3,4};
		int sizeTL = tlListInt1.length ;
		int sizeMessage = 5 + 2 + sizeTL;
		int sizeMessageInfo = 3 + sizeTL;
		byte[] message_pet = new byte[sizeMessage];
		b=ByteBuffer.allocate(4);
		b.putInt(sizeMessageInfo);
		
		ByteBuffer c=ByteBuffer.allocate(4);
		c.putInt(idreg);
		byte[] idReg_bytes=c.array();
		
		byte[] sizeMessageBytes=b.array();
		message_pet[0] = ini; // init byte
		message_pet[1] = sizeMessageBytes[2]; // byte up size
		message_pet[2] = sizeMessageBytes[3]; // byte down size
		message_pet[3] = 0x1; // message id
		/*************** INFO *********/
		message_pet[4] = idReg_bytes[2]; // byte up regulator id
		message_pet[5] = idReg_bytes[3]; // byte down regulator id
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

	public void decodifyPetitionTlTimes(byte[] msg) {	}

	public void sendMessage(byte[] msg) {
		try {
			out.write(msg);
			out.flush();
		} catch (IOException ioException) {	ioException.printStackTrace();	}
	}

	public void close() {
		try {
			System.out.println("ENTRO EN CLOSE");
			in.close();	out.close();
			requestSocket.close();
		} catch (IOException ioException) {	ioException.printStackTrace();		}
	}
	public void close_reg(){this.isRunning=false;	close();}
}