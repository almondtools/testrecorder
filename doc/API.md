Using the Testrecorder API
==========================

Besides from generating Tests from running code one may also utilize Testrecorder to serialize certain objects to code by API.

The advantage of using the API is to be more flexibel and more precise. The Testrecorder tool will capture almost all reachable state and serialize it to the test. There are certain situations where a small subset of the state is sufficient. And the API will give you the flexibility to serialize exactly those objects you want at the time you want.

The disadvantage is that many features the Testrecorder tool takes care of must be implemented manually. One will have to find the reachable state, the possible exceptions and the mocking of input and output.

Now find out how to generate setup or verification code manually:

## Runtime Object Serialization with CodeSerializer

How can we generate builders and matchers for existing code, just by inserting API-call to testrecorder? The [following code](https://github.com/almondtools/testrecorder-examples/tree/master/src/main/java/com/almondtools/testrecorder/examples/codeserializer) will serve as example:

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

The fact that testrecorder requires your program to be startet with an agent is somewhat distracting. You may consider  `CallsiteRecorder` to overcome this requirement. The [following code](https://github.com/almondtools/testrecorder-examples/tree/master/src/main/java/com/almondtools/testrecorder/examples/callsiterecorder) will serve as example:

    public class ExampleObject {
        private int counter;

        public int inc() {
            counter++;
            return counter;
        }
    
        public ExampleObject reset() {
            this.counter = 0;
            return this;
        }
    }

To record a callsite you must specify:

* all methods you want to record
* the scenario you want to record

In the example we want to record the calls to `inc()` in the code lines:

    int doubleInc = exampleObject.inc().inc().get();
    
    int resetInc = exampleObject.reset().inc().get();

We first start to explain how to record with a given `CallsiteRecorder` and then explain the recommended way how to get the callsite recorder. So if we start with a recorder, we can pass `Runnables` or `Callables<T>` to its `record()` method, e.g.

    recordings = recorder.record(() -> {
        int doubleInc = exampleObject.inc().inc().get();
        System.out.println(doubleInc);
    });

or 

    recordings = recorder.record(() -> {
        int resetInc = exampleObject.reset().inc().get();
        System.out.println(resetInc);
    });

The result `recordings` is is a `CompletableFuture<List<ContextSnapshot>>`, i.e. a delayed list of context snapshots (recorded serialized state before and after the method call). In the example we delegate this result to a method `printTest(CompletableFuture<List<ContextSnapshot>>)` which generates a test from each snapshot, but you are free to handle these snapshots in the way you want. This is the code with delegations to `printTest(CompletableFuture<List<ContextSnapshot>>)` (lookup the code of printTest in the example code):

    printTest(recorder.record(() -> {
        int doubleInc = exampleObject.inc().inc().get();
        System.out.println(doubleInc);
    }));
    printTest(recorder.record(() -> {
        int resetInc = exampleObject.reset().inc().get();
        System.out.println(resetInc);
    })); 

Now that we know how to generate tests, we only need to know how to get the `recorder`. Note that the recorder internally uses the java agent (as the commandline tool), so we should ensure that the code instrumentation is limited to our recordings and does not escape to other program parts (making them slower and sometimes even unpredictable). `CallsiteRecorder` is `Autoclosable` so we can use it in a `try-with-resources` block, like this:

 
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