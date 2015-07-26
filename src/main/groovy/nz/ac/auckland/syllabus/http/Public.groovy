package nz.ac.auckland.syllabus.http

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author: Marnix Cook <m.cook@auckland.ac.nz> -- University of Auckland (c) 2015
 *
 * This is a marker annotation. If it is placed on an event, it will become callable from any domain.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface Public {

}