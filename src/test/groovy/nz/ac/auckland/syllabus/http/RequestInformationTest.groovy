package nz.ac.auckland.syllabus.http

import org.junit.Test
import nz.ac.auckland.syllabus.events.Event

/**
 * User: marnix
 * Date: 26/03/13
 * Time: 10:17 AM
 *
 * Test grayles servlet functionality
 */
class RequestInformationTest {

	@Test
	public void testGenericMethod() {

		RequestInformation s = new RequestInformation()
		assert s.getUrlPartIfPresent("/1/2/3", 0, 0) == "1"
		assert s.getUrlPartIfPresent("/1/2/3", 0, 1) == "1"
		assert s.getUrlPartIfPresent("/1/2/3", 0, 2) == "1"
		assert s.getUrlPartIfPresent("/1/2/3", 0, 3) == "1"

		assert s.getUrlPartIfPresent("/1/2/3", 1, 0) == "2"
		assert s.getUrlPartIfPresent("/1/2/3", 1, 2) == "2"
		assert s.getUrlPartIfPresent("/1/2/3", 1, 3) == "2"


	}

	@Test
	public void testUrlPartRetrieval() {
		def testNamespace = { String url ->
			RequestInformation s = new RequestInformation(url)
			return s.getNamespace()
		}
		def testVersion = { String url ->
			RequestInformation s = new RequestInformation(url)
			return s.getVersion()
		}
		def testAction = { String url ->
			RequestInformation s = new RequestInformation(url)
			return s.getAction()
		}

		assert testNamespace("/1/namespace/action") == "namespace"
		assert testNamespace("/namespace/action") == "namespace"
		assert testNamespace("/action") == Event.DEFAULT_NAMESPACE

		assert testVersion("/2/namespace/action") == "2"
		assert testVersion("/v1/namespace/action") == "v1"
		assert testVersion("/namespace/action") == RequestInformation.DEFAULT_VERSION
		assert testVersion("/action") == RequestInformation.DEFAULT_VERSION

		assert testAction("/1/namespace/action") == "action"
		assert testAction("/namespace/action") == "action"
		assert testAction("/action") == "action"
	}


}
