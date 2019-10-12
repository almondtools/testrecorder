Using the Testrecorder API
==========================

Besides from generating Tests from running code one may also utilize Testrecorder to serialize certain objects to code by API.

The advantage of using the API is to be more flexibel and more precise. The Testrecorder tool will capture almost all reachable state and serialize it to the test. There are certain situations where a small subset of the state is sufficient. And the API will give you the flexibility to serialize exactly those objects you want at the time you want.

The disadvantage is that many features the Testrecorder tool takes care of must be implemented manually. One will have to find the reachable state, the possible exceptions and the mocking of input and output.

Now find out how to generate setup or verification code manually:

## Runtime Object Serialization with CodeSerializer

How can we generate builders and matchers for existing code, just by inserting API-call to testrecorder? The following code in [codeserializer](https://github.com/almondtools/testrecorder-examples/tree/master/src/main/java/com/almondtools/testrecorder/examples/codeserializer) will serve as example:

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

### Serializing any Object as Java Code

Serializing an object to code is done like this:

    CodeSerializer codeSerializer = new CodeSerializer();
    String code = codeSerializer.serialize(exampleObject);

The string `code` will then contain:

    ExampleObject exampleObject1 = new ExampleObject();
    exampleObject1.setName("Testrecorder");

Feel free to modify the example code to find out the power of the testrecorder code generator:

* it is quite clever in finding the best way how to build an object
* it is not limited to bean style java objects
* it does not rely on conventions (instead it uses conventions as heuristics, but validates their correctness)

### Serializing any Object as Hamcrest Matcher Code

Serializing an object to matcher code  is done like this:

    CodeSerializer codeSerializer = new CodeSerializer("", ConfigurableSerializerFacade::new, MatcherGenerators::new);
        codeSerializer.getTypes().registerTypes(Matcher.class);
        String code = codeSerializer.serialize(exampleObject);

The string `code` will then contain:

    Matcher<ExampleObject> serializedObject1 = new GenericMatcher() {
        String name = "Testrecorder";
    }.matching(ExampleObject.class);

The matcher generation is mostly straight forward, just validating the structural properties of an object with some special extensions for aggregations.

## Recording a Method Call with CallsiteRecorder

The full featured testrecorder requires your program to be started with an agent. Yet there is another method to record code, that allows you to record certain code parts explicitly. This is done with the `CallsiteRecorder`. Note that `CallsiteRecorder` is limited to common recorder test cases (this will be probably be documented in future).

The code in [callsiterecorder](https://github.com/almondtools/testrecorder-examples/tree/master/src/main/java/com/almondtools/testrecorder/examples/callsiterecorder) will serve as example (class [Counter](https://github.com/almondtools/testrecorder-examples/blob/master/src/main/java/com/almondtools/testrecorder/examples/callsiterecorder/Counter.java)):

	public class Counter {
		private int counter;

		pulic int get() {
			return counter;
		}

		public Counter inc() {
			counter++;
			return this;
		}

		public Counter reset() {
			this.counter = 0;
			return this;
		}
	}

In the example we want to record the calls to `inc()` in the code lines:

    int doubleInc = exampleObject.inc().inc().get();
    
    int resetInc = exampleObject.reset().inc().get();

Now let us visit the recording code (found in [CounterMain](https://github.com/almondtools/testrecorder-examples/blob/master/src/main/java/com/almondtools/testrecorder/examples/callsiterecorder/CounterMain.java)):

    try (CallsiteRecorder recorder = new CallsiteRecorder(ExampleObject.class.getDeclaredMethod("inc"))) {
        printTest(recorder.record(() -> {
            int doubleInc = exampleObject.inc().inc().get();
            System.out.println(doubleInc);
        }));
        printTest(recorder.record(() -> {
            int resetInc = exampleObject.reset().inc().get();
            System.out.println(resetInc);
        }));
    }

First of all we need a `CallsiteRecorder`. It must be configured with each method that should be recorded (recorded methods may be specified by Reflection). `CallsiteRecorder` is `Autocloseable`, i.e. if you put it in a try-with-resources-block the teardown is called automatically.

The `printTest` method displays the recorded contents. For an example implementation look into the referenced source code.

`CallsiteRecorder.record` captures all interactions with the configured methods in the given lambda expression.