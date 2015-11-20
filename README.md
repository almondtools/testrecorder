testrecorder
============

Starting with legacy code (meaning "untested code") is usually hard and thankless. The last time I sat together with some developers we agreed that the right way would be to fix the current behavior with automated integration tests and then refactor the system until it is understandable.

Yet writing integration tests is hard and thankless, too. And wouldn't it be amazing if a tool generates all tests for you. It is important to know that writing integration tests is not really complex if you start with common user scenarios. The tool would have to collect all input and all output of tested methods. An integration test will setup the input objects and assert that the output objects equal the collected output objects.

Our tool for this is **testrecorder**.

Serializing any Object as Java Code
===================================
Serializing an object to code is done like this:

	CodeSerializer codeSerializer = new CodeSerializer();
	String code = codeSerializer.serialize(objectToSerialize);

Serializing any Object as Hamcrest Matcher Code
===============================================
Serializing an object to matcher code  is done like this:

	MatcherSerializer codeSerializer = new MatcherSerializer();
	String code = codeSerializer.serialize(objectToSerialize);

Generating Tests from Productive Code
=====================================
- put the test recorder jar on your class path
- you will also need the jar with dependencies
- annotate the methods you want to record with `@Snapshot`
- Write a class `YourConfig implements SnapshotConfig` and configure it
- start your application with `-javaagent:testrecorder-jar-with-dependencies.jar=YourConfig com.almondtools.testrecorder.examples.FizzBuzz`
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
