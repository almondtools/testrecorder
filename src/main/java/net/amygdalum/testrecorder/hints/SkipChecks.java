package net.amygdalum.testrecorder.hints;

import net.amygdalum.testrecorder.DeserializationHint;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to skip the generation of a matcher for this entity (e.g. if this entity is known not to be relevant for the result)
 * 
 * @planned
 */
public class SkipChecks implements DeserializationHint {

}
