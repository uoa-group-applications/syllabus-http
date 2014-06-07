package nz.ac.auckland.syllabus.http

import groovy.transform.CompileStatic
import nz.ac.auckland.syllabus.SyllabusContext
import nz.ac.auckland.syllabus.hooks.BeforeEvent
import nz.ac.auckland.syllabus.hooks.EventHook
import nz.ac.auckland.syllabus.hooks.EventHookException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Referrer logger event hook implementation logs the X-Angular-Referer header
 */
@BeforeEvent
@CompileStatic
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
	 * Logs the angular referer header
	 *
	 * @param event is the event handler that is about to be invoked
	 *
	 * @throws EventHookException
	 */
	@Override
	void executeHook(SyllabusContext context) throws EventHookException {
		if (context instanceof HttpSyllabusContext) {
			HttpSyllabusContext hContext = context as HttpSyllabusContext

			String referrer = hContext.request.getHeader(ANGULAR_REFERER_HEADER)

			if (referrer) {
				LOG.info("API request started from frontend at $referrer")
			}
		}
	}
}
