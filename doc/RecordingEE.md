Recording Enterprise Java
=========================

Testrecorder is easy to use for Plain Old Java, yet there are many scenarios where Plain Old Java is just not enough:

* Object relational layers
* Webservice layers
* Dependency Injection Frameworks
* Web Frameworks

I call this Enterprise features, not because it is clumsy and oversized, but because they are mainly used for large integrated projects (and not for academic studies, homework or just for fun). Most of the enterprise frameworks add heavy annotation support and creation of dynamic classes. Testrecorder is very limited in handling dynamically created classes. So is there a way out?

Maybe. I realized that really many enterprise frameworks do only add abstractions to dependencies (some on the local system, some remote). Such abstractions have some properties that make it hard to record them:

* they store transient state, e.g. caches, transaction management (that should not be recorded)
* they may have dynamic classes (which is not easily recorded)
* there state is not really relevant, there behavior is (another argument not to record it)

With this insight we conclude, that testrecorder should ignore the state of these objects and instead record interactions with them. This may seem familiar, the resulting test code should stub or mock these objects, e.g.:

* the database layer
* the webservice layer
* injected services or resources
* component factories

To reach this we need following features:

* a way to record interactions with selected objects
* a way to suspend serialization for selected objects

Maybe you already read of [recording input and output](RecordingIO.md) - you will need this to accomplish this task. Yet this only lets you record object interactions. Just mark the input and output methods with `@Input` or `@Output` (or use the corresponding `SerializationProfile` configurations ). It does not prevent testrecording from recording the whole state of the object.   

For this testrecorder provides the annotation `@Facade` which can be viewed as modified variant of `@Excluded`. While `@Exluded` does not record the annotated value, `@Facade` does not record the state (the fields) of the annotated value. This enables input/output recording (since calling interactions on null objects will throw an exception in test).