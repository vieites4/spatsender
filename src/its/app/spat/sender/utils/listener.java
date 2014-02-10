package its.app.spat.sender.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public  class listener implements Runnable{
	//@Override
	Socket Sock;
Boolean Continue=true;
Boolean came=true;
	public  listener(Socket SocketS) throws IOException{
		
		Sock=SocketS;
		
	}

	@Override
	public synchronized void run() {
		try {
		// TODO Auto-generated method stub
		Reception recep=new Reception();
		BufferedReader inFromServer=null;
		inFromServer=new BufferedReader(new InputStreamReader(Sock.getInputStream()));
		
		
				
				while(Continue){
					
					if(!Sock.isClosed())
					
						if(inFromServer.readLine().isEmpty()==true ){recep.put(inFromServer.toString().toCharArray());}else{}
				}
		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}

}
