package its.app.spat.sender.impl;

import its.app.spat.sender.thread.SenderThread;
import its.fac.messages.api.services.ItsMessagesSenderService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTrackerCustomizerImpl implements ServiceTrackerCustomizer {

	private final static Logger logger = LoggerFactory
			.getLogger(ServiceTrackerCustomizerImpl.class);

	private SenderThread senderThread;
	private BundleContext bundleContext;

	public ServiceTrackerCustomizerImpl(BundleContext bundleContext,
			SenderThread senderThread) {
		this.senderThread = senderThread;
		this.bundleContext = bundleContext;
	}

	public final Object addingService(ServiceReference serviceReference) {
		ItsMessagesSenderService itsMessagesService = (ItsMessagesSenderService) bundleContext
				.getService(serviceReference);
		this.senderThread.setItsMessagesSenderService(itsMessagesService);
		logger.info("SPATSender: Got the ItsMessagesSenderService service.");
		this.senderThread.checkServiceRegisterer();
		return itsMessagesService;
	}

	public final void modifiedService(ServiceReference serviceReference,
			Object serviceObject) {
		ItsMessagesSenderService itsMessagesService = (ItsMessagesSenderService) bundleContext
				.getService(serviceReference);
		this.senderThread.setItsMessagesSenderService(itsMessagesService);
		logger.info("SPATSender: ItsMessagesSenderService has been modified.");

		this.senderThread.checkServiceRegisterer();
	}

	public final void removedService(ServiceReference serviceReference,
			Object paramObject) {
		this.senderThread.setItsMessagesSenderService(null);
		logger.info("SPATSender: ItsMessagesSenderService service removed.");

		this.senderThread.serviceUnregister();
	}
}
