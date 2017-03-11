package net.amygdalum.testrecorder.hints;

import net.amygdalum.testrecorder.DeserializationHint;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to keep the construction of the annotated entity in a factory method (default would be inline in the test)
 * 
 * @planned
 */
public class PreferFactoryMethods implements DeserializationHint {

}
