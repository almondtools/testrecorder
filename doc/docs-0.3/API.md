Using the Testrecorder API
==========================

Besides from generating Tests from running code one may also utilize Testrecorder to serialize certain objects to code by API.

The advantage of using the API is to be more flexibel and more precise. The Testrecorder tool will capture almost all reachable state and serialize it to the test. There are certain situations where a small subset of the state is sufficient. And the API will give you the flexibility to serialize exacly those objects you want at the time you want.

The disadvantage is that many features the Testrecorder tool takes care of must be implemented by hand, such as:

- find the reachable state
- handle exceptions
- handle IO

The next sections will give you an overview how to do Object Serialization with the Testrecorder API.

## Runtime Object Serialization - the Basics

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

### Serializing any Object as Java Code

Serializing an object to code is done like this:

    CodeSerializer codeSerializer = new CodeSerializer();
    String code = codeSerializer.serialize(exampleObject);

The string `code` will then contain:

    ExampleObject exampleObject1 = new ExampleObject();
    exampleObject1.setName("Testrecorder");
    ExampleObject serializedObject1 = exampleObject1;


### Serializing any Object as Hamcrest Matcher Code

Serializing an object to matcher code  is done like this:

    SerializationProfile profile = new DefaultTestRecorderAgentConfig();
    SerializerFacade facade = new ConfigurableSerializerFacade(profile);
    DeserializerFactory factory = new MatcherGenerators.Factory();

    CodeSerializer codeSerializer = new CodeSerializer("", facade, factory);
    codeSerializer.getTypes().registerTypes(Matcher.class, ExampleObject.class); // optional
    String code = codeSerializer.serialize(exampleObject);

The string `code` will then contain:

    Matcher<ExampleObject> serializedObject1 = new GenericMatcher() {
        String name = "Testrecorder";
    }.matching(ExampleObject.class);

