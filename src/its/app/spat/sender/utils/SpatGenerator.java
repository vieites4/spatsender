package its.app.spat.sender.utils;
import its.app.spat.sender.bundle.Activator;
import its.fac.messages.api.enums.DSRCMessageID;
import its.fac.messages.api.exceptions.ValueOutOfRangeException;
import its.fac.messages.api.services.ItsMessagesSenderService;
import its.fac.messages.api.types.IntersectionState;
import its.fac.messages.api.types.MovementState;
import its.fac.messages.api.types.Spat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Vector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class SpatGenerator extends Thread {
	//private final static Logger logger = LoggerFactory.getLogger(SpatGenerator.class);
	private Spat spat;
	private Vector<IntersectionState> intersectionstate;
	private Vector<MovementState> movementstate;
	private String[] spatKml;
	private String[] spatKmlPoint;
	private int intersectionId;

	public boolean first1=true;
	private  RegulatorConection th1=new RegulatorConection() ;
	public SpatGenerator() {	}

	public int check_colour(char colour){

		if(colour=='V')return(0);else if(colour=='A')return(1);else if (colour=='R')return(2);else if (colour=='-')return(3);
		else return(4);
	};
	public int check_type(String type){

		String Ball="L";
		String Left_arrow="FI";
		String Right_arrow="FD";
		String Straight_arrow="FF";
		String Soft_left_arrow="FOI";
		String Soft_right_arrow="FOD";
		String U_turn_arrow="CS";

		if(type.equals(Ball))return(0);else if(type.equals(Left_arrow))return(1);else if(type.equals(Right_arrow))return(2);
		else if(type.equals(Straight_arrow))return(3);else if(type.equals(Soft_left_arrow))return(4);else if(type.equals(Soft_right_arrow))return(5);
		else if(type.equals(U_turn_arrow))return(6);else return(7);	};

		public Spat generateSpatMessage(	ItsMessagesSenderService itsMessagesSenderService, String[] strTLTopo)
				throws ValueOutOfRangeException, InterruptedException {

			//	System.out.println("entro en generateSpatMessage" );

			long[][] kml=  {
					{ 0x01     ,0x02     ,0x04    , 0x08},
					{ 0x10     ,0x20     ,0x40     ,0x80},
					{ 0x100    ,0x200    ,0x400    ,0x800},
					{ 0x1000   ,0x2000   ,0x4000   ,0x8000}};
			int[] arrayTLtopo=new int[11];//strTLTopo.length
			String[] arraytype=new String[11];
			String lanes;int lon=0;
			int [][] laneset=new int[255][127];
			boolean pedestrian=false;
			for (int i = 0; i < strTLTopo.length; i++) {
				lon=strTLTopo[i].split(";").length;
				if (lon==4){pedestrian=false;
				arrayTLtopo[i]=Integer.parseInt(strTLTopo[i].split(";")[0]);	//grupo de semaforo; lineas separadas por comas;{color tipo}
				arraytype[i]=strTLTopo[i].split(";")[2];//System.out.println("arraytype"+arraytype[i]);
				lanes=strTLTopo[i].split(";")[1];
				intersectionId=Integer.parseInt(strTLTopo[i].split(";")[3]);
				String[] laneSetsString=lanes.split(",");
				for(int j=0; j<laneSetsString.length;j++){
					laneset[i][j]=Integer.parseInt(laneSetsString[j]);
				}
				}else{pedestrian=true;
				lanes=strTLTopo[i].split(";")[1];
				intersectionId=Integer.parseInt(strTLTopo[i].split(";")[0]);
				String[] laneSetsString=lanes.split(",");
				for(int j=0; j<laneSetsString.length;j++){
					laneset[i][j]=Integer.parseInt(laneSetsString[j]);
				}				
				}}
			spatKml = Activator.spatKML.split("/");
			spatKmlPoint = spatKml[1].split("\\.");
			byte [][] response=new byte[100][4];
			th1.arrayTLtopo=arrayTLtopo;
			th1.intersectionId=intersectionId;
			//	System.out.println("antes "+first1);
			if (first1){//System.out.println("mau");
				first1=false;th1.start();}

			typeTemp element;
			int num= th1.List_temp.size();int i=0;
			if(th1.waitingResponse)th1.Temp100=th1.Temp100-(Integer.parseInt(Activator.spatFrequency)/100); if(th1.waitingACK)th1.Temp10=th1.Temp10-(Integer.parseInt(Activator.spatFrequency)/100);

			if(th1.Temp10<=0){th1.close();th1.Temp10=20;}else
				if(th1.Temp100<=0){th1.close();th1.Temp100=200;}
			List<typeTemp> clone1=th1.List_temp;
			List<Integer> clone2=th1.List_ID;
			while(i<num){
				element=clone1.get(i);
				if(element.first)
					element.Timer_last=element.Timer_last -( (Integer.parseInt(Activator.spatFrequency)/100)*(3/2));else
						element.Timer_last=element.Timer_last -( (Integer.parseInt(Activator.spatFrequency)/100));
				element.first=false;
				if(element.Timer_last<=0){
						clone1.remove(i);
						clone2.remove(i);
				}else{ clone1.set(i,element);
				i++;}
				num=clone1.size();
			}
			th1.List_temp=clone1;
			th1.List_ID=clone2;
			//System.out.println(th1.List_temp.size()+ "tamaño lista de temporización");
			int[] arrayTL=new int[clone1.size()];
			for ( i = 0; i < clone1.size(); i++) {

				response[i][0]=(byte)(clone1.get(i).ID);
				response[i][1]=clone1.get(i).color;//System.out.println("color "+message[4+10 + next_pos]);
				byte[] res=new byte[4];
				res=ByteBuffer.allocate(4).putInt(clone1.get(i).Timer_last).array();
				response[i][2]=res[2];//System.out.println("veo2 "+res[2]+" "+res[3]);
				response[i][3]=res[3];
				arrayTL[i]=(int)response[i][0]&0xff;//System.out.println("arraytltopo"+arrayTLtopo[i]);
			}
			if (response==null){			System.out.println("No conection" );return null;}
				spat = itsMessagesSenderService.createSpat();
			spat.setMsdId(DSRCMessageID.SIGNALPHASEANDTIMINGMESSAGE);
			intersectionstate = spat.createIntersectionState(1);//está bien
			intersectionstate.get(0).setIdIntersectionState(intersectionId);
			intersectionstate.get(0).setTimeStamp(58); // No hace falta cubrirlo, sino poner la hora de la notificación
			intersectionstate.get(0).setIntersectionStatus(0); // estados de 0 a 7
			movementstate = intersectionstate.get(0).createMovementState(arrayTL.length);
			long colortl1=0;
			for (int ii = 0; ii < arrayTL.length; ii++) {
				i=clone1.get(ii).ID -1;
				if (laneset[i][0]>=0)	{			
					String[] tls = arraytype[i].split(",");
					String color = tls[0];
					String type_len = tls[1];
					String[] colorSetsString = color.split(":");
					char[][] colorset = new char[arrayTL.length][4];
					String[] typeSetsString = type_len.split(":");
					String[][] type_set = new String[arrayTL.length][3];
					colorset[ii][0]= colorSetsString[0].charAt(2);//porque leva {(diante
					colorset[ii][1]= colorSetsString[1].charAt(0);
					colorset[ii][2]= colorSetsString[2].charAt(0);
					type_set[ii][0]= typeSetsString[0].substring(1);
					type_set[ii][1]= typeSetsString[1];
					type_set[ii][2]= typeSetsString[2].substring(0, typeSetsString[2].length()-2);
					int column=0;	int column1=1;	int aa=0;	int row=0;		int pos=0;
					if(response[ii][1]== 'D'){ colortl1=0x0;}else{
						if(response[ii][1]== 'V'|| response[ii][1]== 'C'|| response[ii][1]== 'P'){

							column=check_colour(colorset[ii][2]);
							if(response[ii][1]== 'C'|| response[ii][1]== 'P'){aa=1;column1=check_colour(colorset[ii][3]);}
							if (column==0)pos=2;else if (column==2)pos=0; else pos=column;
							row=check_type(type_set[ii][pos]);System.out.println("Entrei en V " +column+ " "+row);

						}
						else if(response[ii][1]== 'A' || response[ii][1]== 'F'|| response[ii][1]== 'N'|| response[ii][1]== 'J'|| response[ii][1]== 'I'|| response[ii][1]== 'G'|| response[ii][1]== 'S'|| response[ii][1]== 'E'|| response[ii][1]== 'K'|| response[ii][1]== 'Z')
						{
							column=check_colour(colorset[ii][1]);
							if(response[ii][1]== 'E'|| response[ii][1]== 'K'|| response[ii][1]== 'F'|| response[ii][1]== 'Z'||response[ii][1]== 'J'|| response[ii][1]== 'I'|| response[ii][1]== 'G')
							{column1=check_colour(colorset[ii][3]);aa=1;}
							if(response[ii][1]== 'N'){column1=check_colour(colorset[ii][2]);aa=1;}
							if(response[ii][1]== 'S')	{column1=check_colour(colorset[ii][0]);aa=1;}
							if (column==0)pos=2;else if (column==2)pos=0; else pos=column;
							row=check_type(type_set[ii][pos]);System.out.println("Entrei en A " +column+ " "+row);

						}else if(response[ii][1]== 'R'|| response[ii][1]== 'B'|| response[ii][1]== 'H'){

							column=check_colour(colorset[ii][0]);
							if(response[ii][1]== 'B'|| response[ii][1]== 'H'){column1=check_colour(colorset[ii][3]);aa=1;}
							if (column==0)pos=2;else if (column==2)pos=0; else pos=column;
							row=check_type(type_set[ii][pos]);System.out.println("Entrei en R " +column+ " "+row);
						} 

						if(response[ii][1]== 'F'|| response[ii][1]== 'J'|| response[ii][1]== 'I'|| response[ii][1]== 'G'|| response[ii][1]== 'E'|| response[ii][1]== 'K'|| response[ii][1]== 'Z'){column=3;}
						if (aa==1)	colortl1=kml[row][column]+kml[row][column1];
						else colortl1=kml[row][column];         }
				}else{
					if(response[ii][1]== 'D'||response[ii][1]== 'C'||response[ii][1]== 'F'||response[ii][1]== 'B') colortl1=0x0;
					else if(response[ii][1]== 'V'|| response[ii][1]== 'N'|| response[ii][1]== 'P'|| response[ii][1]== 'H')colortl1=0x1;
					else if(response[ii][1]== 'R'|| response[ii][1]== 'S')colortl1=0x4;	

				}
				movementstate.get(ii).setMovementName("STATE"+"+i+");// no es necesario
				int a=1;int ia=0;

				while(a==1){
					if(laneset[i][ia]==0)a=0;
					ia++;
				}
				int [] laneset1=new int[ia-1]; a=1; ia=0;
				while(ia<laneset1.length){
					laneset1[ia]=laneset[i][ia];
					ia++;
				}		
				for (int j = 0; j < laneset1.length; j++) {
					//		System.out.println(i+" laneset1 "+laneset1[j]+" "+laneset1.length);
				}
				//
				movementstate.get(ii).setLaneSet(laneset1);
				movementstate.get(ii).setCurrState(colortl1);
	//correcto para os casos que non sexa -1, como se faría noutro caso??
				int sum1=response[ii][3]&(0xff);
				int sum2= response[ii][2]&(0xff);
				int sum=sum1+sum2*256;
				if ( response[i][3]!=-1)movementstate.get(ii).setTimeToChange(sum);else{
					sum1=response[ii][7]&(0xff);
					sum2= response[ii][6]&(0xff);
					sum=sum1+sum2*256;
					movementstate.get(ii).setTimeToChange(sum);
				}
				System.out.println("Eu calculo: time to change:"+sum+ " color:"+colortl1+ " laneset1:"+laneset1[0]+" grupo semafórico;"+(i+1));
				//	System.out.println("Colors: " + colortl1 );
				//	System.out.println("Time: " + sum);

			}
			intersectionstate.get(0).setMovementState(movementstate);
			spat.setIntersectionState(intersectionstate);
			int len=spat.getIntersectionState().get(0).getMovementState().size();
		System.out.println("return spat");
			if (th1.connect)return spat;else return null;
		}
		public void close(){
			this.th1.close_reg();
		}
}