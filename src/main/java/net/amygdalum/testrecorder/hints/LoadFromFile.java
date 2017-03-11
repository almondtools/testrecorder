package net.amygdalum.testrecorder.hints;

import net.amygdalum.testrecorder.DeserializationHint;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to load the construction of the entity from file (using some kind of serialization reader)
 * 
 * @planned
 */
public class LoadFromFile implements DeserializationHint {

}
