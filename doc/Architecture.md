The Architecture of Testrecorder
================================

## The Serialization/Deserialization Process

The default serialization process is designed to first find the best serializer for a given object and then extract the model from it. Nulls and Primitives have special serializers, all others will default to the `GenericSerializer`. This `GenericSerializer` scan the given object with reflection and stores every field found into its model. Although with this serializer we get almost all information available to the JVM, it has some disadvantages:
- The serializer cannot see native information. Any data written to the unmanaged heap or coming from native variables cannot be reliably stored into the model.
- The serializer skips some system classes because reflectively scanning such special classes is a very probable source of trouble.
- The serializer skips synthetic fields. Testrecorder itself inserts synthetic attributes into objects (which only store meta information needed for serialization), but there are also many other applications (e.g. code coverage) that insert synthetic attributes, that would pollute the serialized model.
- The serializer will see information that is part of the "transient" model. Some fields are relevant at runtime, but can (or should be) skipped for serialization. E.g. most java collections maintain a modified counter helping to identify concurrent modifications while using an iterator. This field is not relevant unless the exception has to be reproduced.
