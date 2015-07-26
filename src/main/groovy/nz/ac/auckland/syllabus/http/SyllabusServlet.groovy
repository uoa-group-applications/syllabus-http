package nz.ac.auckland.syllabus.http

import groovy.transform.CompileStatic
import nz.ac.auckland.syllabus.dispatcher.CentralDispatch
import nz.ac.auckland.syllabus.errors.TransmissionException
import nz.ac.auckland.syllabus.hooks.EventHookException
import nz.ac.auckland.util.JacksonException
import nz.ac.auckland.util.JacksonHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.context.support.SpringBeanAutowiringSupport

import javax.inject.Inject
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * User: marnix
 * Date: 25/03/13
 * Time: 10:59 AM
 *
 * Transmission servlet implementation will dispatch to the correct event handler
 */
@CompileStatic
class SyllabusServlet extends HttpServlet {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(SyllabusServlet.class);

	/**
	 * Event dispatchers
	 */
	@Inject
	private CentralDispatch centralDispatch

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
		RequestInformation rInfo = this.getRequestInformation(request?.pathInfo);

		try {
			// get request
			String contentType = request.getContentType()?.tokenize(";")?.get(0)

			if (!contentType) {
				contentType = "text/plain"
			}

			log.debug("Incoming content type {}", contentType)

			HttpSyllabusContext context = new HttpSyllabusContext(request: request, response: response,
			    action: rInfo.action, namespace: rInfo.namespace, version: rInfo.version,
				requestBody: request.reader.text, contentType: contentType)

			// dispatch to appropriate location
			Object responseObject = centralDispatch.dispatch(context)

			if (responseObject == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
				response.writer.write("{error:'Bad content type or no response from handler'}")
				response.writer.flush()
				return
			}

			// try to output response - should support JSONP here as well
			try {
				if (isPublicEndpoint(context.currentHandle?.instance)) {
					response.setHeader("Access-Control-Allow-Origin", "*");
				}

				if (responseObject instanceof String && contentType == "text/plain") {
					response.setHeader("Content-Type", "text/plain; charset=UTF-8");
					response.writer.print(responseObject.toString())
				} else {
					response.setHeader("Content-Type", "application/json; charset=UTF-8");
					response.writer.print(JacksonHelper.serialize(responseObject))
				}

				response.writer.flush()
			}
			catch (JacksonException jEx) {
				log.error("Unable to serialize json response message", responseObject);
				writeException(response, 502, jEx.message)
			}
		}
		catch (TransmissionException transEx) {
			// write HTTP error status
			writeException(response, transEx.statusCode, transEx.message)
		}
		catch (EventHookException ehEx) {
			log.info("The execution of this request was stopped because of an exception thrown by a @BeforeEvent event hook", ehEx);
			if (ehEx.statusCode > 0) {
				writeException(response, ehEx.statusCode, ehEx.message)
			}
		}
	}

	/**
	 * @return true if the current handle's instance has a Public annotation on it
	 */
	protected boolean isPublicEndpoint(Object instance) {
		return instance?.getClass()?.getAnnotation(Public) != null;
	}

	protected void writeException(HttpServletResponse response, int status, String message) {
		response.setStatus(status)
		response.writer.write(message)
		response.writer.flush()
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response)
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
