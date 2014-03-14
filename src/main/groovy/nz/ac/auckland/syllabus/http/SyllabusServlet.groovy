package nz.ac.auckland.syllabus.http

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.inject.Inject

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.web.context.support.SpringBeanAutowiringSupport

import nz.ac.auckland.syllabus.events.EventDispatcher

import nz.ac.auckland.syllabus.errors.TransmissionException
import nz.ac.auckland.util.JacksonHelper
import nz.ac.auckland.util.JacksonException
import nz.ac.auckland.syllabus.hooks.EventHookException

/**
 * User: marnix
 * Date: 25/03/13
 * Time: 10:59 AM
 *
 * Transmission servlet implementation will dispatch to the correct event handler
 */
class SyllabusServlet extends HttpServlet {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SyllabusServlet.class);

	/**
	 * Event dispatcher
	 */
	@Inject
	private EventDispatcher eventDispatcher;

	/**
	 * Http context service, to be setup in doPost
	 */
	@Inject
	private HttpContextService httpContextService;

	/**
	 * Initialize autowired spring beans
	 */
	@Override
	void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.servletContext);
	}

	/**
	 * Post handler will interpret payload and dispatch to appropriate object
	 *
	 * @param request is the request object
	 * @param response is the response object
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {

		this.httpContextService.setup(request, response)

		RequestInformation rInfo = this.getRequestInformation(request?.pathInfo);

		try {
			// get request
			String requestBody = request.reader.readLines().join("")

			// dispatch to appropriate location
			Object responseObject = eventDispatcher.dispatch(rInfo.action, rInfo.namespace, rInfo.version, requestBody)

			// try to output response
			try {
				String responseJson = JacksonHelper.serialize(responseObject)

				response.setHeader("Content-Type", "application/json; charset=UTF-8");
				response.writer.print(responseJson)
			}
			catch (JacksonException jEx) {
				LOG.error("Unable to serialize json response message", responseObject);
				response.setStatus(502, jEx.message);
			}

		}
		catch (TransmissionException transEx) {
			// write HTTP error status
			response.setStatus(transEx.statusCode, transEx.message)
		}
		catch (EventHookException ehEx) {
			LOG.info("The execution of this request was stopped because of an exception thrown by a @BeforeEvent event hook", ehEx);
		}
	}

	/**
	 * Get the request information instance to extract request information from
	 *
	 * @param request is the request info to set it up for
	 * @return the request
	 */
	protected RequestInformation getRequestInformation(String pathInfo) {
		return new RequestInformation(pathInfo)
	}
}
