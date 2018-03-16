Using the Testrecorder API
==========================

Besides from generating Tests from running code one may also utilize Testrecorder to serialize certain objects to code by API.

The advantage of using the API is to be more flexibel and more precise. The Testrecorder tool will capture almost all reachable state and serialize it to the test. There are certain situations where a small subset of the state is sufficient. And the API will give you the flexibility to serialize exactly those objects you want at the time you want.

The disadvantage is that many features the Testrecorder tool takes care of must be implemented manually. One will have to find the reachable state, the possible exceptions and the mocking of input and output.

Now find out how to generate setup or verification code manually:

## Runtime Object Serialization - the Basics

How can we generate builders and matchers for existing code, just by inserting API-call to testrecorder? The following code will serve as example:

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