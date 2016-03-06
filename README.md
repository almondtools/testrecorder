[Testrecorder](http://almondtools.github.io/testrecorder/)
============

__Testrecorder__ is a tool for generating test code from runnable Java code. The generated tests can then be executed with a JUnit-Runner.

* You can use these tests as part of your integration tests
* Or you can try to refactor them to make up proper unit tests
* Even without reusing the generated code it could give valuable insights for code understanding

It is not recommended to replace a test-driven strategy with generated-test strategy but in many cases the productive code is there and the tests are missing (=legacy code). In this case the first step should be to fix the current behavior by creating integration tests. This task is often hard and thankless. __Testrecorder__ can support you in this case.

Serializing any Object as Java Code
===================================
Serializing an object to code is done like this:

	CodeSerializer codeSerializer = new CodeSerializer();
	String code = codeSerializer.serialize(objectToSerialize);

Serializing any Object as Hamcrest Matcher Code
===============================================
Serializing an object to matcher code  is done like this:

	SerializationProfile profile = new DefaultSerializationProfile();
	SerializerFacade facade = new ConfigurableSerializerFacade(profile);
	SerializedValueVisitorFactory factory = new ObjectToMatcherCode.Factory();
					
	CodeSerializer codeSerializer = new CodeSerializer(facade, factory);
	String code = codeSerializer.serialize(objectToSerialize);

Generating Tests from Productive Code
=====================================
- put the test recorder jar on your class path
- you will also need the jar with dependencies
- annotate the methods you want to record with `@Snapshot`
- Write a class `YourConfig implements SnapshotConfig` and configure it
- start your application with `-javaagent:testrecorder-jar-with-dependencies.jar=YourConfig`
- examples can be found at [testrecorder-examples](https://github.com/almondtools/testrecorder-examples)

Assumptions and Restrictions
============================
TestRecorder serialization (for values and tests) does not cover all of an objects properties. Problems might occur with:
- static fields
- synthetic fields (added by some bytecode rewriting framework)
- native state

Furthermore there are assumptions on the common collection interfaces. The serialized values will not contain the exact type found, but:
- any field typed `Map<K,V>` will be filled with a `LinkedHashMap<K,V>`  
- any field typed `Set<T>` will be filled with a `LinkedHashSet<T>`
- any field typed `List<T>` will be filled with a `ArrayList<T>`

Both, restrictions and assumptions, can be adjusted by modifying the serialization strategies by both
- defining a new `SerializationProfile` to ensure that the internal representation is correct
- and modifying your Serializer (e.g. `CodeSerializer` or `TestGenerator`) to use another `SerializedValueVisitorFactory`


TODOs
=====
- More readable generated Tests
- Less  warnings in generated Tests
- Other Profiles (than `DefaultSerializationProfile)` that can handle serialization of static or generated data
- Higher Test Coverage
- Tutorial
