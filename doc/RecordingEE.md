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
* their state is not really relevant, there behavior is (another argument not to record it)

With this insight we conclude, that testrecorder should ignore the state of these objects and instead record interactions with them. This may seem familiar, the resulting test code should stub or mock these objects, e.g.:

* the database layer
* the webservice layer
* injected services or resources
* component factories

To reach this we need following features:

* a way to record interactions with selected objects
* a way to suspend serialization for selected objects

We recommend to approach such problems with enterprise objects in the following way:

* Identify your enterprise objects
* Suppress recording state of such objects by annotating fields containing such an object with `@Facade` 
* Identify the input/output to be recorded on such objects and handle them as described in [RecordingIO](RecordingIO.md)

## Facade

A Testrecorder Facade is an object that is only partially recorded. Type and interface are recorded, the complete internal state is not. To record an object as facade, there are two variants:

* Mark the field/class to be recorded as facade with the `@Facade` annotation
* Use a `SerializationProfile` implementing the method `getFieldFacades()`/`getClassFacades()` and return the field/class that should be facaded

Whatever variant you choose, an object in the specified field/of the specified class will be recorded as facade. Note that you will have to record interactions (input/output) separately as described in [RecordingIO](RecordingIO.md).