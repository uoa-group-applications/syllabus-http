package nz.ac.auckland.syllabus.http

import nz.ac.auckland.syllabus.hooks.BeforeEvent
import nz.ac.auckland.syllabus.hooks.EventHook
import nz.ac.auckland.syllabus.events.EventHandler
import nz.ac.auckland.syllabus.hooks.EventHookException
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.Logger

/**
 * User: marnix
 * Date: 3/04/13
 * Time: 2:47 PM
 *
 * Referrer logger event hook implementation logs the X-Angular-Referer header
 */
@BeforeEvent
class ReferrerLoggerEventHook implements EventHook {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ReferrerLoggerEventHook.class);

	/**
	 * Header to read
	 */
	private static final String ANGULAR_REFERER_HEADER = "X-Angular-Referer"

	/**
	 * Http context service injected here
	 */
	@Inject
	private HttpContextService httpContextService;

	/**
	 * Logs the angular referer header
	 *
	 * @param event is the event handler that is about to be invoked
	 *
	 * @throws EventHookException
	 */
	@Override
	public void executeHook(EventHandler event) throws EventHookException {

		HttpServletRequest request = httpContextService.request
		String referrer = request?.getHeader(ANGULAR_REFERER_HEADER)

		if (referrer) {
			LOG.info("API request started from frontend at $referrer")
		}

	}
}
