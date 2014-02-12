package its.app.spat.sender.thread;

import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import its.app.spat.sender.bundle.Activator;
import its.app.spat.sender.utils.SpatGenerator;
import its.app.spat.sender.utils.TLSimulator;
import its.app.spat.sender.utils.TLTopology;
import its.fac.messages.api.exceptions.MessageIncompleteException;
import its.fac.messages.api.exceptions.ValueOutOfRangeException;
import its.fac.messages.api.services.ItsMessagesSenderService;
import its.fac.messages.api.types.Spat;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class SenderThread extends Thread {

	private final static Logger logger = LoggerFactory
			.getLogger(SenderThread.class);

	private boolean isRunning = false;
	private boolean isServiceRegistered = false;
	private SpatGenerator spatGenerator;
	private TLTopology tlTopology;
	private ItsMessagesSenderService itsMessagesSenderService = null;
	TLSimulator tl1;
	TLSimulator tl2;
	TLSimulator tl3;
	long time0;
	long time1;
	long time2;
	String[] strTLTopo;

	public SenderThread(BundleContext bundleContext) {
	//	logger.info("SPATSender: SPAT frequency is " + Activator.spatFrequency);
		//logger.info("SPATSender: SPAT KML " + Activator.spatKML);
		
		this.tlTopology = new TLTopology();
		try {
			strTLTopo= this.tlTopology.generateTLTopology(Activator.spatKML);
			
		/**	for (int i = 0; i < strTLTopo.length; i++) {
				System.out.println(strTLTopo[i]);
			}**/
			
			
		} catch (ValueOutOfRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		tl1 = new TLSimulator();
		tl1.setTime(180);
		tl1.setColor(1);
		tl1.start();
		
		tl2 = new TLSimulator();
		tl2.setTime(180);
		tl2.setColor(1);
		tl2.start();
		
		tl3 = new TLSimulator();
		tl3.setTime(180);
		tl3.setColor(4);
		tl3.start();**/

		this.isRunning = true;
		this.isServiceRegistered = false;
	}

	public final void setItsMessagesSenderService(
			ItsMessagesSenderService itsMessagesSenderService) {
		this.itsMessagesSenderService = itsMessagesSenderService;
	}

	public final void checkServiceRegisterer() {
		if (this.itsMessagesSenderService != null) {
			
			this.spatGenerator = new SpatGenerator();
			this.isServiceRegistered = true;
		}
	}

	public final void serviceUnregister() {
		this.isServiceRegistered = false;
	}

	public final synchronized void run() {
		while (this.isRunning) {
			long time = System.currentTimeMillis();
			if (this.isServiceRegistered) {
				try {
					
					Spat spat = this.spatGenerator.generateSpatMessage(
							itsMessagesSenderService, strTLTopo);		
					
					
					if (spat != null) {	itsMessagesSenderService.send(spat);
						logger.info("SPATSender: spat messages has been sent to GNBTPAPI on BTP port " + spat.toString());
					}//else {System.out.println("spat==null");}
				} catch (MessageIncompleteException e) {
					logger.error("SPATSender: error sending spat messages "	+ e.getMessage());
				} catch (ValueOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				long timeToSleep = (long) (Double
						.parseDouble(Activator.spatFrequency) - (System
						.currentTimeMillis() - time));
				if (timeToSleep > 0l)
					Thread.sleep(timeToSleep);
			} catch (InterruptedException interruptedException) {
				logger.error("SPATSender: Send thread has been interrupted "
						+ interruptedException.getMessage());
			}

		}
	}

	public final void stopSenderThread() {
		this.isRunning = false;
// NOT NECESARY STOP
//		tl1.stopTL();
//		tl2.stopTL();
//		tl3.stopTL();
		//System.out.println("Stopping sending SPAT");
	}
}