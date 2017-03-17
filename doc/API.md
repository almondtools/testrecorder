Using the Testrecorder API
==========================

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

    SerializationProfile profile = new DefaultSerializationProfile();
    SerializerFacade facade = new ConfigurableSerializerFacade(profile);
    DeserializerFactory factory = new ObjectToMatcherCode.Factory();
                    
    CodeSerializer codeSerializer = new CodeSerializer(facade, factory);
    String code = codeSerializer.serialize(exampleObject);

The string `code` will then contain:

    Matcher<ExampleObject> serializedObject1 = new GenericMatcher() {
        String name = "Testrecorder";
    }.matching(ExampleObject.class);

