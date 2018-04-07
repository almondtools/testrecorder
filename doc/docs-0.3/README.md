[Testrecorder](http://testrecorder.amygdalum.net/)
============
[![Build Status](https://travis-ci.org/almondtools/testrecorder.svg?branch=master)](https://travis-ci.org/almondtools/testrecorder)
[![codecov](https://codecov.io/gh/almondtools/testrecorder/branch/master/graph/badge.svg)](https://codecov.io/gh/almondtools/testrecorder)

__Testrecorder__ is a tool for recording runtime behavior of java programs. The results of such a recording are executable JUnit-tests replaying the recorded behavior.

* You can use these tests as part of your characterization tests
* Or you can refactor them (they are pure java) to make up proper unit tests
* Even without reusing the generated code it could give valuable insights for code understanding

__Testrecorder__ uses an api to serialize objects to executable java code or hamcrest matchers.

Basic Usage
===========

## 1. Annotate the method to record
Annotate the method to record with `@Recorded`. For example you want to record this simple example

    public class FizzBuzz {
        @Recorded
        public String fizzBuzz(int i) {
            if (i % 15 == 0) {
                return "FizzBuzz";
            } else if (i % 3 == 0) {
                return "Fizz";
            } else if (i % 5 == 0) {
                return "Buzz";
            } else {
                return String.valueOf(i);
            }
        }
    }

## 2. Configure the test serialization
Write a java configuration file that implements `TestRecorderAgentConfig`. For example:

    public class AgentConfig extends DefaultTestRecorderAgentConfig {
        
        @Override
        public SnapshotConsumer getSnapshotConsumer() {
            return new ScheduledTestGenerator()
                .withDumpOnShutDown(true)                       
                .withDumpTo(Paths.get("target/generated"));     
        }
        
        @Override
        public long getTimeoutInMillis() {
            return 100_000;
        }
        
        @Override
        public List<Packages> getPackages() {
            return asList(Packages.byName("com.almondtools.testrecorder.examples"));
        }
    
    }

Now some explanations:

`getSnapshotConsumer` should return the client that generates your test. You can use any class implementing `SnapshotConsumer` yet there are two default implementations:

* `TestGenerator` is a low level implementation. It can collect tests but will write them only driven by API calls. Actually you should use this class only as super class for your own implementations. Such sub classes are not limited when and where to write tests.
* `ScheduledTestGenerator` is a simple `TestGenerator` implementation allowing you to specify when to write tests to the file system. As you can see in the example you can specify to write tests at program shutdown (with `withDumpOnShutDown(true)`) and you should specify the directory where serialized tests should be stored (with `withDumpTo([directory])`)

`getTimeoutInMillis` will be the limit for the recording time. This threshold is built in to skip unexpectedly long (possibly infinite) serializations. The value of `100_000` will actually only stop such long serializations. 

`getPackages` should return a list of java packages that should be analyzed. Only methods in these packages are recorded (`@Recorded`-Annotations that are in packages not specified here will not have any effect)

## 3. Run your program with TestRecorderAgent
To run your program with test recording activated you have to call it with an agent

`-javaagent:testrecorder-[version]-jar-with-dependencies.jar=AgentConfig`

`testrecorder-[version]-jar-with-dependencies.jar` is an artifact provided by the maven build (available in maven repository).

`AgentConfig` is your configuration class from the former step.

## 4. Interact with the program and check results
You may now interact with your program and every call to a `@Recorded` method will be captured. After shutdown of your program all captured recordings will be transformed to executable JUnit tests, e.g.

    @Test
    public void testFizzBuzz0() throws Exception {
    
        //Arrange
        FizzBuzz fizzBuzz1 = new FizzBuzz();
        
        //Act
        String string1 = fizzBuzz1.fizzBuzz(1);
        
        //Assert
        assertThat(string1, equalTo("1"));
        assertThat(fizzBuzz1, new GenericMatcher() {
        }.matching(FizzBuzz.class));
    }

    ...
    
Advanced Topics
===============
Following subjects could be of further interest:

### [An Introduction to the Testrecorder Architecture](doc/Architecture.md)

### [Tuning the Output of Testrecorder](doc/TuningOutput.md)

### [Recordering Input/Output with Testrecorder](doc/RecordingIO.md)

### [Using the Testrecorder-API to serialize data and generate code](doc/API.md)

### [Extending Testrecorder with Custom Components](doc/Extending.md)

Limitations
===========
TestRecorder serialization (for values and tests) does not cover all of an objects properties. Problems might occur with:

* static fields
* synthetic fields (e.g. added by some bytecode rewriting framework)
* native state
* state that influences object access (e.g. modification counter in collections)

Examples
========
Examples can be found at [testrecorder-examples](https://github.com/almondtools/testrecorder-examples)

Some additional notes ...
=========================
The objective of Testrecorder is to provide an interface that is powerful, clean and extensible. To achieve this we will provide more and more configuration settings to extend the core framework. The fact that tests are generated automatically might rise wrong expectations: Testrecorder will probably always be an experts tool, meaning strong programming and debug skills are recommended to find the correct configuration and the necessary custom extensions.

Testrecorder was not yet tested on a large set of code examples. Some classes are not as easy to serialize as others, so if you encounter problems, try to write an issue. Hopefully - most fixes to such problems should be solvable with custom serializers or custom deserializers. 
 