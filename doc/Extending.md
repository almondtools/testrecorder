Extending Testrecorder with Custom Components
=============================================

Testrecorder provides some common features in the base framework. However we are aware of many features that would fit certain real problems, that cannot be solved in a generic way. So we designed Testrecorder to be extendible.

The [Architecture](Architecture.md) of Testrecorder is extendible at certain points:
- Custom serializers allow you to simplify the model of a recorded object
- Custom setup generators allow you to adjust the way the model is transformed to test setup code
- Custom matcher generators allow you to adjust the way the model is transformed to matcher code
- Custom intializers are needed if some instrumentations are needed before executing the agent/the test

You can write such extensions in the workspace of your applications. Sometimes it seems better to bundle the extension to a common artifact that could be shared. There is also a section describing how to create a __testrecorder-jar-with-dependencies__ with all your custom components.

## Custom Serializers

Custom serializers allow you to collect information that is not available to the default serializers, or to aggregate information in a more succinct way.

For a custom serializer example we choose an object that is practically not serializable in a generic way, such as `Thread`. The internals of `Thread` are needed, but most users just initialize a thread and rely on the jvm to keep the correct state. With such knowledge in mind we can simplify the serialization process in certain scenarios. Have a look at this serializer (source code of this example could be found [here](https://github.com/almondtools/testrecorder-examples/tree/master/src/main/java/com/almondtools/testrecorder/examples/serializers/)):


    public class ThreadSerializer implements Serializer<SerializedObject> {
      
      private SerializerFacade facade;
      
      public ThreadSerializer(SerializerFacade facade) {
        this.facade = facade;
      }
      
      @Override
      public List<Class<?>> getMatchingClasses() {
        return asList(Thread.class);
      }
      
      @Override
      public SerializedObject generate(Type resultType, Type type) {
        return new SerializedObject(type).withResult(resultType);
      }
      
      @Override
      public void populate(SerializedObject serializedObject, Object object) {
        try {
          Thread thread = (Thread) object;
          Field field = Thread.class.getDeclaredField("target");
          Reflections.accessing(field).exec(()-> {
            Runnable runnable = (Runnable) field.get(thread);
            SerializedValue serializedRunnable = facade.serialize(runnable.getClass(), runnable);
            serializedObject.addField(new SerializedField(Thread.class, "target", Runnable.class, serializedRunnable));
          });
        } catch (ReflectiveOperationException | SecurityException e) {
          System.err.println(e.getMessage());
        }
      }
      
      public static class Factory implements SerializerFactory<SerializedObject> {
      
        @Override
        public ThreadSerializer newSerializer(SerializerFacade facade) {
          return new ThreadSerializer(facade);
        }
        
      }
    
    }

We will explain this class step by step. First the construction of the serializer:

      private SerializerFacade facade;
      
      public ThreadSerializer(SerializerFacade facade) {
        this.facade = facade;
      }
      
      ...

      public static class Factory implements SerializerFactory<SerializedObject> {
      
        @Override
        public ThreadSerializer newSerializer(SerializerFacade facade) {
          return new ThreadSerializer(facade);
        }
        
      }
    
The construction of the serializer requires a `SerializerFactory<T>` where `T` denotes the type that will be emitted and populated by the serializer. Such a factory has one mandatory method `newSerializer(SerializerFacade facade)`. It is on yours whether you want to store the facade in your serializer. Actually it is quite helpful to have this facade in your Serializer because you get access to all other serializers with this facade.

We could have chosen, any type from `net.amygdalum.testrecorder.values` as `T`. For this class we chose `SerializedObject`, because it allows us to flexibly add fields to the object.

Now the serializer needs to what which type it should apply to. This is done by `getMatchingClasses()`.  

      @Override
      public List<Class<?>> getMatchingClasses() {
        return asList(Thread.class);
      }

You may return a List of classes that must be handled by this serializer. Other than with deserializers, any matched class is than mapped strictly to this serializer. No other (fallback) serializer will jump in if the matching one fails. If you cannot decide statically which of the serializers should do the serialization, you can use the decorator or composite design pattern to combine multiple optional serializers in one.

The creation phase is started with the method `generate(Type resultType, Type type)`.

      @Override
      public SerializedObject generate(Type resultType, Type type) {
        return new SerializedObject(type).withResult(resultType);
      }

This method is only expected to return a reference to the serialized object that later would be populated. It is passed the actual type of of the object and also the result type which is the type of the variable the generated object will be assigned to. Throwing an exception will be caught and lead to the next best serializer to be considered.

The split between creation and population phase is because of some dependency management that the references are needed for. The population phase calls the method `populate(SerializedObject serializedObject, Object object)`:

      @Override
      public void populate(SerializedObject serializedObject, Object object) {
        try {
          Thread thread = (Thread) object;
          Field field = Thread.class.getDeclaredField("target");
          Reflections.accessing(field).exec(()-> {
            Runnable runnable = (Runnable) field.get(thread);
            SerializedValue serializedRunnable = facade.serialize(runnable.getClass(), runnable);
            serializedObject.addField(new SerializedField(Thread.class, "target", Runnable.class, serializedRunnable));
          });
        } catch (ReflectiveOperationException | SecurityException e) {
          throw new RuntimeException(e.getMessage());
        }
      }

This method knows that only objects of type `Thread` will be passed. So casting to `Thread` and extracting reflectively the field `target` (which contains the wrapped `Runnable`) could be done safely.

In a second step this method builds a `SerializedObject` and puts in the field `target` as field. Note that we have to make the field accessible with `Reflections.accessing` because private fields will otherwise throw exceptions.

In theory these operations can lead to `ReflectiveOperationExceptions` or `SecurityExceptions`, so we captured these exceptions and rethrew them. It is generally a good practice to capture all exceptions in a serializer, because an exception will leave the object partially initialized and testrecorder will continue with the next object.

Fortunately the serialized object created by this serializer can often serve as model for a the default setup generators and the default matcher generators. In other cases it might get necessary to provide also deserializers (setup generators, matcher generators), a subject which is covered in the following sections.

Yet writing the class `ThreadSerializer` is not sufficient. The written Serializer must be registered:

* create a directory `META-INF/services` in your class path
* create a file `net.amygdalum.testrecorder.SerializerFactory` in this directory
* put the full qualified class name of `ThreadSerializer$Factory` into this file


## Custom Setup Generators 

While Custom Serializers allow you to collect more detailed information, Custom Generators allow you to present gathered information in a more readable, more maintainable way. Testrecorder uses two types of code generators - generators for setup code (Setup Phase, Arrange Phase) and generators for matcher code (Verification Phase, Assert Phase).

TODO

## Custom Matcher Generators

TODO

## Custom Initializers

TODO (TestRecorderAgentInitializer, ServiceLoader)

## Bundling your custom components

TODO (Maven Shade Plugin)