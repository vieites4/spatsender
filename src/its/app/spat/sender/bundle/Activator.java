package its.app.spat.sender.bundle;

import its.app.spat.sender.impl.ServiceTrackerCustomizerImpl;
import its.app.spat.sender.thread.SenderThread;
import its.app.spat.sender.utils.RegulatorConection;
import its.fac.messages.api.services.ItsMessagesSenderService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	private final static Logger logger = LoggerFactory
			.getLogger(Activator.class);

	public static BundleContext bundleContext = null;

	private ServiceTracker itsMessagesSenderServiceTracker = null;

	public SenderThread senderThread;
public RegulatorConection regulatorConection;
	public static volatile String spatFrequency = "2000";
	public static volatile String spatKML = "kml/001.kml";

	
	public void start(BundleContext paramBundleContext) {

		bundleContext = paramBundleContext;
		
		if (System.getProperty("spat.spatFrequency") != null) {
			spatFrequency = System.getProperty("spat.spatFrequency");
		}
		
		if (System.getProperty("spat.spatKML") != null) {
			spatKML = System.getProperty("spat.spatKML");
		}

		this.senderThread = new SenderThread(paramBundleContext);
		this.senderThread.start();

		this.itsMessagesSenderServiceTracker = new ServiceTracker(
				paramBundleContext, ItsMessagesSenderService.class.getName(),
				new ServiceTrackerCustomizerImpl(bundleContext,
						this.senderThread));
		this.itsMessagesSenderServiceTracker.open();
	}

	public void stop(BundleContext paramBundleContext) {
		this.senderThread.stopSenderThread();
		this.itsMessagesSenderServiceTracker.close();
		//this.senderThread.interrupt();
		logger.info("SPATSender: Bundle stopped!");

	}
}