package nz.ac.auckland.syllabus.http

import nz.ac.auckland.syllabus.SyllabusContext

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This allows us to pass the request/response around without making the underlying infrastructure
 * dependent on Http. It also lets us typecast it if we do detect this situation. It also means we don't
 * need a thread local keeping track of the request/response.
 *
 * @author: Richard Vowles - https://plus.google.com/+RichardVowles
 */
class HttpSyllabusContext extends SyllabusContext {
	HttpServletRequest request
	HttpServletResponse response
}
