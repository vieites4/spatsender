package its.app.spat.sender.utils;

import java.io.IOException;

public class listen_and_ack extends Thread{
	
RegulatorConection RegulatorConection;
	public void listen_and_ack(){
		boolean go=false;
		while(go){//RegulatorConection.requestSocket.isClosed()==false && RegulatorConection.isRunning){
		byte[] buffer=new byte[1000];
		System.out.println(RegulatorConection.isRunning + "is running");
		int len;
		try {
			len = RegulatorConection.in.read(buffer);
		//	byte[][]message_response=new byte[tl.length][8];
			boolean desired=buffer[3]==0x02;
			//in.read(message, 0, message.length);
			if (desired==true){
		
		int lon0=buffer[1]&(0xff);
		int lon1=buffer[2]&(0xff);
		int lon= lon0*256+lon1;
		int result=0;
		if ((len - 4)!= lon)  result=1;else
			if (buffer[3]!=2) result=2;
		
		byte[] msg=RegulatorConection.createACK((byte)2,(byte)result);
		RegulatorConection.sendMessage(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}}
}
