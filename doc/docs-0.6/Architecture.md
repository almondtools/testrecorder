The Architecture of Testrecorder
================================

Testrecorder generates tests in two phases:

* serialization (recording)
* deserialization (generation)

Yet both phases are strongly coupled. It is planned to keep them more separate in future.

Each phase has its own extension points.

## Serialization (Recording)

The serializations reponsibility is two transform the objects available at runtime two a serialized form. Each object at runtime has its represenation as `SerializedValue`. The serialization is dispatched to different `Serializer`s, e.g.

* Null serializer for null values
* Primitive serializer for primitive values
* Array serializer for array values
* Immutable serializers for special object values that are considered immutable (Primitive Wrappers, BigInteger, BigDecimal, Enums)
* Object serializer for object values
* Collection serializers for different collection values (lists, queues, sets, maps, ...)
* Custom serializers

While some of this serializers (null, primitive, object) produce an exact copy of the object to serializer, some others only serialize an abstraction (collection serializers). Copying an abstraction has the advantage of being faster and more robust then the generic object serializer, because not every field of an object is necessary to replay an object (some are even disruptive).

Not that testrecorder cannot determine whether an object to be recorded is serializable with generic serializers. This task must be left to the user. Such objects could be excluded from serialization or serialized by a custom serializer.  

Clearly, native information cannot be properly recorded. It is strongly recommended to exclude system classes and reflection from serialization (via the serialization profile), because most of these classes are not serializable by design. Yet if your program relys on such classes you will have to implement a custom serializer.

Another information that is skipped, is synthesized fields. Such fields are usually added by class loaders or instrumentation interfaces (e.g spring proxys, coverage instrumtation, ...) and obey a custom logic, that testrecorder is not aware of. 

Extension points for serialization are:

* `net.amygdalum.testrecorder.profile.SerializationProfile`   
* `net.amygdalum.testrecorder.types.Serializer`   

## Deserialization (Generation)

The deserializations repsonsibility is transforming the model given by the serializer to a running test code.

The default test generation is done by classes like `TestGenerator` or `ScheduledTestGenerator`. It is possible to use a completely different code generation strategy by implementing another `SnapshotConsumer`. The default code generators generate test code in three phases (AAA-pattern):

- Arrange phase: all objects needed for executing the test method are set up
- Act phase: the method which is tested is called with all objects it depends on
- Asset phase: all objects residing after the call are validated to be as expected

Consequently each phase need its own strategys for code generation. The arrange phase dispatches to `SetupGenerator`s, the act phase is trivial, so there is nothing special to do, the asset phase dispatches to `MatcherGenerator`s.

Creating an object from a model with a `SetupGenerator`  is much more complicated than serializing to a model. So there do exist much more strategies for deserializing objects, e.g.

* Null deserializers for null values
* Primitive deserializers for serialized primitive values
* Immutable deserializers for different kind of values resulting in String, Numbers, Primitive Wrappers, Enums
* Array deserializers for array values
* Lambda deserializer for lambda values
* Collection deserializers for collection values
* Special collection deserializers for collection values with special properties (e.g immutable, singleton, synchronized, ...)
* Bean deserializers for common values that are heuristically considered to be reconstructed in a quite readable way
* Custom deserializers for object values that can be reconstructed with some knowledge by the user
* Generic Object deserializers for anything that is not deserializable by a dedicated deserializer

Creating a validator from a model with a `MatcherGenerator` is probably a kind more generic (if we accept that such a matcher only validates structural properties). Yet code gets more readable with more abstract matcher generators, yet there do some strategies for deserializing a matcher, e.g.
* Null matchers for null values
* Equality matchers for primitive or immutable values
* Containment matchers for collection values
* Lambda matchers for lambda values
* Generic structural matcher for anything that is not deserializable by a dedicated deserializer
 
Extension points for serialization are:

* `net.amygdalum.testrecorder.SnapshotConsumer`   
* `net.amygdalum.testrecorder.deserializers.builder.SetupGenerator`   
* `net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator`   
