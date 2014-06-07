package nz.ac.auckland.syllabus.http

import groovy.transform.CompileStatic
import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.syllabus.SyllabusContext
import nz.ac.auckland.syllabus.dispatcher.Dispatcher
import nz.ac.auckland.syllabus.errors.TransmissionException
import nz.ac.auckland.syllabus.events.EventDispatcher
import org.springframework.web.bind.ServletRequestDataBinder

import javax.inject.Inject
import javax.servlet.http.HttpServletResponse

/**
 *
 * @author: Richard Vowles - https://plus.google.com/+RichardVowles
 */
@CompileStatic
@UniversityComponent
class GetDispatcher implements Dispatcher {
	@Inject EventDispatcher eventDispatcher

	@Override
	List<String> supports() {
		return ["text/plain"]
	}

	protected Object provideObject(SyllabusContext syllabusContext) {
		HttpSyllabusContext context = syllabusContext as HttpSyllabusContext

		// we should now know our object
		if (context.currentHandle.serializeObject == null) {
			return ""
		}

		// Irina pointed this out, too much chance of xss
		Get getMarker = context.currentHandle.instance.getClass().getAnnotation(Get)
		if (allowGet(getMarker, context)) {
			Object o = context.currentHandle.serializeObject.newInstance()

			ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(o)
			dataBinder.bind(context.request)

			return o
		} else {
			throw new TransmissionException("GET not supported for this request", HttpServletResponse.SC_BAD_REQUEST)
		}
	}

	@Override
	Object dispatch(SyllabusContext syllabusContext) {
		if (syllabusContext instanceof HttpSyllabusContext) {
			HttpSyllabusContext context = syllabusContext as HttpSyllabusContext

			context.requestBody = context.request.queryString

			return eventDispatcher.dispatch(context, this.&provideObject)
		}

		return null
	}

	protected boolean allowGet(Get get, SyllabusContext context) {
		if (!get) return false  // no marker

		Closure getClosure = get.value().newInstance(context.currentHandle.instance, context.currentHandle.instance) as Closure

		return getClosure.call(context)
	}
}
