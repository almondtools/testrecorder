[Testrecorder](http://testrecorder.amygdalum.net/)
============

__Testrecorder__ is a tool for generating test code from runnable Java code. The generated tests can then be executed with a JUnit-Runner.

* You can use these tests as part of your integration tests
* Or you can try to refactor them to make up proper unit tests
* Even without reusing the generated code it could give valuable insights for code understanding

It is not recommended to replace a test-driven strategy with generated-test strategy but in many cases the productive code is there and the tests are missing. In this case the first step should be to fix the current behavior by creating integration tests. This task is often hard and thankless. __Testrecorder__ can support you in this case.

We shall start with some basics on Runtime Object Serialization. This action has a simple interface, yet it is not as powerful as test recording. Then we shall dive into the configuration of Test Recording.

Runtime Object Serialization - the Basics
=========================================

In this section we give you an impression how code can be serialized and directly deserialized to code. The following examples will use the following `ExampleObject`:

    public class ExampleObject {
        private String name;
    
        public void setName(String name) {
            this.name = name;
        }
    
        public String getName() {
            return name;
        }
    }

    ExampleObject exampleObject = new ExampleObject();
    exampleObject.setName("Testrecorder");

Serializing any Object as Java Code
-----------------------------------
Serializing an object to code is done like this:

	CodeSerializer codeSerializer = new CodeSerializer();
	String code = codeSerializer.serialize(exampleObject);

The string `code` will then contain:

	ExampleObject exampleObject1 = new ExampleObject();
	exampleObject1.setName("Testrecorder");
	ExampleObject serializedObject1 = exampleObject1;


Serializing any Object as Hamcrest Matcher Code
-----------------------------------------------
Serializing an object to matcher code  is done like this:

	SerializationProfile profile = new DefaultSerializationProfile();
	SerializerFacade facade = new ConfigurableSerializerFacade(profile);
	DeserializerFactory factory = new ObjectToMatcherCode.Factory();
					
	CodeSerializer codeSerializer = new CodeSerializer(facade, factory);
	String code = codeSerializer.serialize(exampleObject);

The string `code` will then contain:

	Matcher<ExampleObject> serializedObject1 = new GenericMatcher() {
        String name = "Testrecorder";
    }.matching(ExampleObject.class);

Test Recording - Advanced Topics
================================

Test Recording is strictly putting together the upper code for Runtime Object Serialization. 

How to start
------------
The first step to Test Recording should be to instrument your code.

- Put the test recorder jar on your class path
- You will also need the jar with dependencies
- Select one method of interest and annotate it with `@Snapshot`. Now the Testrecorder knows which method has to be recorded.
- Configure your Testrecording by writing a class `YourConfig implements SnapshotConfig`
  - `getSnapshotConsumer` should return an instance of `ScheduledTestGenerator`
  - `getTimeoutMillis` may be set to `100.000`
  - `getPackages` should return the packages containing the classes/methods you want to record
  - `getInitializer` may be set to null 
- start your application with `-javaagent:testrecorder-jar-with-dependencies.jar=YourConfig`

Examples
--------
Examples can be found at [testrecorder-examples](https://github.com/almondtools/testrecorder-examples)

Custom Serializers
------------------
Sometimes you will encounter problems with automatic serialization because the testrecorder engine does not know the best abstraction how to serialize an object. In most times it will choose the `GenericSerializer` class, which is very generic but may contain too much of unnecessary data.

If you depend on an Object that should be serialized in a special way, you can define a new `CustomSerializer implements Serializer<SerializedObject>`. Each serializer has:
- a method `getMatchingClasses` return all classes this serializer can handle
- a method `generateType` being just a factor method to create an empty serialized value
- a method `populate` being a method that is passed both the empty serialized value and the object to serialize. This should store all necessary information into the serialized value
- an inner class `Factory implements SerializerFactory` to return an instance of this serializer. 

To enable `CustomSerializer` make it available as ServiceProvider:
- create a directory `META-INF/services` in your class path
- create a file `net.amygdalum.testrecorder.SerializerFactory` in this directory
- put the full qualified class name of `CustomSerializer$Factory` into this file   


Custom Deserializers (SetupGenerators, MatcherGenerators, ...) 
--------------------------------------------------------------
You can also modify your output code by introducing custom deserializers. More on that in later updates.

Limitations
-----------
TestRecorder serialization (for values and tests) does not cover all of an objects properties. Problems might occur with:
- static fields
- synthetic fields (added by some bytecode rewriting framework)
- native state

The objective of Testrecorder is to provide an interface that is powerful, clean and extensible. To achieve this we will provide more and more configuration settings to extend the core framework. The fact that tests are generated automatically might rise wrong expectations: Testrecorder will probably always be an experts tool, meaning strong programming and debug skills are recommended to find the correct configuration and the necessary custom extensions.

Testrecorder was not yet tested on a large set of code examples. Some classes are not as easy to serialize as others, so if you encounter problems, try to write an issue. Hopefully - most fixes to such problems should be solvable with custom serializers or custom deserializers. 

