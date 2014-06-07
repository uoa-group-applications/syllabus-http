package nz.ac.auckland.syllabus.http

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Marker class: If it exists on a handler class, Get will be supported.
 *
 * @author: Richard Vowles - https://plus.google.com/+RichardVowles
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface Get {
	Class value() default {return true}
}
