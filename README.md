testrecorder
============

Starting with legacy code (meaning "untested code") is usually hard and thankless. The last time I sat together with some developers we agreed that the right way would be to fix the current behavior with automated integration tests and then refactor the system until it is understandable.

Yet writing integration tests is hard and thankless, too. And wouldn't it be amazing if a tool generates all tests for you. It is important to know that writing integration tests is not really complex if you start with common user scenarios. The tool would have to collect all input and all output of tested methods. An integration test will setup the input objects and assert that the output objects equal the collected output objects.

Our tool for this is **testrecorder**.

How to use it
=============

- put the test recorder jar on your class path
- you will also need the jar with dependencies
- annotate the methods you want to record with `@Snapshot`
- Write a class `YourConfig implements SnapshotConfig` and configure it
- start your application with `-javaagent:testrecorder-jar-with-dependencies.jar=YourConfig com.almondtools.testrecorder.examples.FizzBuzz` 


Bugs
====
- equals of SerializedValue sub classes may loop infinitely because of recursive beans (solution is already implemented in GenericComparison)
- separate class loaders for instrumented tests (junit executes all tests with the same system class loader such that a redefinition of a class causes errors) 

TODOs
=====
- More readable generated Tests
- Triggering Object Serialization without Instrumentation (if only a value has to be serialized)
- Less  warnings in generated Tests
- Other Profiles (than `DefaultSerializationProfile)` that can handle serialization of static or generated data
- Higher Test Coverage
- Tutorial
