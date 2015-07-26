package nz.ac.auckland.syllabus.http

/**
 * @author: Marnix Cook <m.cook@auckland.ac.nz> -- University of Auckland (c) 2015
 *
 * Simple test for additional annotation
 */
class SyllabusServletTest extends GroovyTestCase {

    void testIsPublicEndpoint() {
        SyllabusServlet servlet = new SyllabusServlet();
        assert servlet.isPublicEndpoint(new PublicAnnotatedClass())
        assert !servlet.isPublicEndpoint(new NotPublicAnnotatedClass())
    }


    @Public
    class PublicAnnotatedClass {

    }


    class NotPublicAnnotatedClass {

    }
}
