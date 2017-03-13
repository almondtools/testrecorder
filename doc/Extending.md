Extending Testrecorder with Custom Components
=============================================

Testrecorder provides some common features in the base framework. However we are aware of many features that would fit certain real problems, that cannot be solved in a generic way. So we designed Testrecorder to be extendible.

Testrecorder could be extended in different ways:
- Custom serializers allow you to simplify the model of a recorded object
- Custom setup generators allow you to adjust the way the model is transformed to test setup code
- Custom matcher generators allow you to adjust the way the model is transformed to matcher code
- Custom intializers are needed if some instrumentations are needed before executing the agent/the test

You can write such extensions in the workspace of your applications. Sometimes it seems better to bundle the extension to a common artifact that could be shared. There is also a section describing how to create a __testrecorder-jar-with-dependencies__ with all your custom components.

## Custom Serializers

The default serialization process is designed to first find the best serializer for a given object and than extract the model from it. Nulls and Primitives have special serializers, all others will default to the `GenericSerializer`. This `GenericSerializer` scan the given object with reflection and stores every field found into its model. Although with this serializer we get almost all information available to the JVM, it has some disadvantages:
- the serializer cannot see native information. Any data written to the unmanaged heap or coming from native variables cannot be reliably stored into the model
- the serializer skips some system classes because reflectively scanning such special classes is a very probable source of trouble.
- the serializer skips synthetic fields. Testrecorder itself inserts synthetic attributes into objects (which only store meta information needed for serialization), but there are also many other applications (e.g. code coverage) that insert synthetic attributes, that would pollute the serialized model.  
- the serializer will see information that is part of the "transient" model. Some fields are relevant at runtime, but can (or should be) skipped for serialization. E.g. most java collection maintain a modified counter helping to identify concurrent modifications while using an iterator. This field is not relevant unless the exception has to be reproduced.

To get around such limitations consider to write a custom serializer:

* write a new `CustomSerializer implements Serializer<SerializedObject>`. Each serializer has:
  * a method `getMatchingClasses` return all classes this serializer can handle
  * a method `generateType` being just a factor method to create an empty serialized value
  * a method `populate` being a method that is passed both the empty serialized value and the object to serialize. This should store all necessary information into the serialized value
  * an inner class `Factory implements SerializerFactory` to return an instance of this serializer. 

To enable `CustomSerializer` make it available as ServiceProvider:
* create a directory `META-INF/services` in your class path
* create a file `net.amygdalum.testrecorder.SerializerFactory` in this directory
* put the full qualified class name of `CustomSerializer$Factory` into this file   


## Custom Setup Generators 

TODO

## Custom Matcher Generators

TODO

## Custom Initializers

TODO (TestRecorderAgentInitializer, ServiceLoader)

## Bundling your custom components

TODO (Maven Shade Plugin)