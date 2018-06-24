Extending Testrecorder with Custom Components
=============================================

Testrecorder provides some common features in the base framework. However we are aware of many features that would fit certain real problems, that cannot be solved in a generic way. So we designed Testrecorder to be extensible.

The [Architecture](Architecture.md) of Testrecorder is extensible at certain points:
- Custom serializers allow you to simplify the model of a recorded object
- Custom setup generators allow you to adjust the way the model is transformed to test setup code
- Custom matcher generators allow you to adjust the way the model is transformed to matcher code
- Custom initializers are needed if some instrumentations are needed before executing the agent/the test

You can write such extensions in the workspace of your applications. You can even write testrecorder extension libraries, including your extensions.

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
        public SerializedObject generate(Type type) {
            return new SerializedObject(type);
        }
    
        @Override
        public void populate(SerializedObject serializedObject, Object object) {
            try {
                Thread thread = (Thread) object;
                Field field = Thread.class.getDeclaredField("target");
                Reflections.accessing(field).exec(f -> {
                    Runnable runnable = (Runnable) f.get(thread);
                    SerializedValue serializedRunnable = facade.serialize(runnable.getClass(), runnable);
                    serializedObject.addField(new SerializedField(Thread.class, "target", Runnable.class, serializedRunnable));
                });
            } catch (ReflectiveOperationException | SecurityException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    
    }

We will explain this class step by step. Consider the class definition:

    public class ThreadSerializer implements Serializer<SerializedObject>

A serializer must implement `net.amygdalum.testrecorder.types.Serializer` and should expose a generic type parameter that is emitted as model. `SerializedObject` is quite convenient, yet one could even emit custom types that are derived from `SerializedValue`.

Each serializer needs a constructor:

      private SerializerFacade facade;
      
      public ThreadSerializer(SerializerFacade facade) {
        this.facade = facade;
      }
      
The constructor must accept `SerializerFacade`, yet if it is not needed it is not necessary to store it. We store it in a field `facade` because our methods will make use of it.       
    
In order to dispatch the correct objects to the new serializer we have to implement `getMatchingClasses()`.  

      @Override
      public List<Class<?>> getMatchingClasses() {
        return asList(Thread.class);
      }

This gives the dispatching serialization process a hint that only objects of type `Thread` should be passed to this serializer. Note that the serialization process does not support types associated to multiple serializers (other than deserializers).

The creation phase is started with the method `generate(Type type)`.

      @Override
      public SerializedObject generate(Type type) {
        return new SerializedObject(type);
      }

This method is only expected to return a reference to the serialized object that later would be populated. It is passed the actual type of of the object. Throwing an exception will be caught and lead to the next best serializer to be considered.

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

To register your `ThreadSerializer` for serialization dispatch you will have to:

* create a directory `agentconfig` in your class path
* create a file `net.amygdalum.testrecorder.types.Serializer` in this directory
* put the full qualified class name `com.almondtools.testrecorder.examples.serializers.ThreadSerializer` into this file

## Custom Deserializers

While Custom Serializers allow you to collect more detailed information, Custom Deserializers (Generators) allow you to present gathered information in a more readable, more maintainable way. Testrecorder uses two types of code generators - generators for setup code (Setup Phase, Arrange Phase) and generators for matcher code (Verification Phase, Assert Phase).

You can extend both phases with custom generators. Let us have a look at an example (source code of this example could be found [here](https://github.com/almondtools/testrecorder-examples/tree/master/src/main/java/com/almondtools/testrecorder/examples/deserializers/)). It consists of:
- a simple serializer (the default serializer for date maintains a really complicated model, so we decided to serialize it in a custom way)
- a setup generator (discussed in a following section)
- a matcher generator (discussed in a following section)
- a date matcher (the architecture of testrecorder enforces that results must be matched in a single step, so we need a matcher for date, not for the date components). 

### Custom Setup Generators 

The example setup generator for our example looks like this:

    public class DateSetupGenerator extends DefaultSetupGenerator<SerializedImmutable<Date>> implements Adaptor<SerializedImmutable<Date>, SetupGenerators> {
    
        @Override
        public Class<SerializedImmutable> getAdaptedClass() {
            return SerializedImmutable.class;
        }
    
        @Override
        public boolean matches(Type type) {
            return type.equals(Date.class);
        }
    
        @Override
        public Computation tryDeserialize(SerializedImmutable<Date> value, SetupGenerators generator, DeserializerContext context) throws DeserializationException {
            TypeManager types = context.getTypes();
            types.registerTypes(Date.class, Calendar.class);
    
            Date date = value.getValue();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            
            List<String> statements = new ArrayList<>();
            String calexpression = context.newLocal("cal");
            statements.add(assignLocalVariableStatement(types.getVariableTypeName(Calendar.class), calexpression, callMethod(types.getRawTypeName(Calendar.class), "getInstance")));
            statements.add(callMethodStatement(calexpression, "set", "Calendar.DAY_OF_MONTH", String.valueOf(day)));
            statements.add(callMethodStatement(calexpression, "set", "Calendar.MONTH", String.valueOf(month)));
            statements.add(callMethodStatement(calexpression, "set", "Calendar.YEAR", String.valueOf(year)));
            
            String expression = callMethod(calexpression, "getTime");
            return Computation.expression(expression, mostSpecialOf(value.getUsedTypes()).orElse(Object.class), statements);
        }
    
    }

We explain this setup generator step by step. We begin with the specification of the adapted class `getAdaptedClass()`

      @Override
      public Class<SerializedImmutable> getAdaptedClass() {
        return SerializedImmutable.class;
      }
      
This method specifies the type of the serialized value which is handled by this generator. It must return a subclass of `SerializedValue`. For the example we chose to adapt the class `SerializedImmutable`, with respect to our serializer, which produces a `SerializedImmutable<Date>`.

The next method `matches(Type type)` defines another property of the serialized value that can be handled:

      @Override
      public boolean matches(Type type) {
        return type.equals(Date.class);
      }

Where `getAdaptedClass()` defines the type of the serialized value, `matches(Type type)` constrains the type of the real value. This method allows to handle multiple types (not only one as in the serializer). For each supported type this method should return `true`. Since we want our example generator to handle `Date` we return `true` for `Date.class`.

Note that matching `getAdaptedClass()` and `matches(Type type)` is not sufficient to guarantee that this Class will generate code. A model object class can be adapted by more than one `SetupGenerator`, the first matching is chosen for generation, and its result is committed if the generation process did not fail. If the first `SetupGenerator` fails the next is considered (and so on).

Now let us examine the method `tryDeserialize(SerializedImmutable<Date> value, SetupGenerators generator, DeserializerContext context)`:

      @Override
      public Computation tryDeserialize(SerializedImmutable<Date> value, SetupGenerators generator, DeserializerContext context) throws DeserializationException {
        TypeManager types = generator.getTypes();
        types.registerTypes(Date.class, Calendar.class);
        
        Date date = value.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        
        List<String> statements = new ArrayList<>();
        String calexpression = generator.newLocal("cal");
        statements.add(assignLocalVariableStatement(types.getBestName(Calendar.class), calexpression, callMethod(types.getBestName(Calendar.class), "getInstance")));
        statements.add(callMethodStatement(calexpression, "set", "Calendar.DAY_OF_MONTH", String.valueOf(day)));
        statements.add(callMethodStatement(calexpression, "set", "Calendar.MONTH", String.valueOf(month)));
        statements.add(callMethodStatement(calexpression, "set", "Calendar.YEAR", String.valueOf(year)));
        
        String expression = callMethod(calexpression, "getTime");
        return new Computation(expression, value.getResultType(), statements);
      }

It is passed the value to generate and an object of type SetupGenerators, which is a Facade to other generators. The first thing we should do in such a setup generator is registering the types that should be imported (we will need `Calendar` and `Date`):

        TypeManager types = generator.getTypes();
        types.registerTypes(Date.class, Calendar.class);

Then we extract the data to match from the value, meaning the day, the month and the year:

        Date date = value.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

Then we will build the statements for the setup section:

        List<String> statements = new ArrayList<>();
        String calexpression = generator.newLocal("cal");
        statements.add(assignLocalVariableStatement(types.getBestName(Calendar.class), calexpression, callMethod(types.getBestName(Calendar.class), "getInstance")));
        statements.add(callMethodStatement(calexpression, "set", "Calendar.DAY_OF_MONTH", String.valueOf(day)));
        statements.add(callMethodStatement(calexpression, "set", "Calendar.MONTH", String.valueOf(month)));
        statements.add(callMethodStatement(calexpression, "set", "Calendar.YEAR", String.valueOf(year)));
        
The upper block generates the following lines (given a date 2012-12-24):
        
    Calendar cal1 = Calendar.getInstance();
    cal1.set(Calendar.DAY_OF_MONTH, 24);
    cal1.set(Calendar.MONTH, 11);
    cal1.set(Calendar.YEAR, 2012);

The last step is the returning of the computation. A `Computation` should contain a value, a type and supplementary statements. The statements were computed in the former steps. The type is `Date` and the expression is the `Calendar` converted to a date with `getTime()`.

        String expression = callMethod(calexpression, "getTime");
        return Computation.expression(expression, mostSpecialOf(value.getUsedTypes()).orElse(Object.class), statements);

The `expression` which gets the value of the `Computation` will be the setup value for the test. The value that is constructed in the setup phase of the test would then be `cal1.getTime()`.

To register your `DateSetupGenerator` for deserialization dispatch you will have to:

* create a directory `agentconfig` in your class path
* create a file `net.amygdalum.testrecorder.deserializers.builder.SetupGenerator` in this directory
* put the full qualified class name `com.almondtools.testrecorder.examples.deserializers.DateSetupGenerator` into this file

        
### Custom Matcher Generators

The example matcher generator looks like this:

    public class DateMatcherGenerator extends DefaultMatcherGenerator<SerializedImmutable<Date>> implements Adaptor<SerializedImmutable<Date>, MatcherGenerators> {
    
        @Override
        public Class<SerializedImmutable> getAdaptedClass() {
            return SerializedImmutable.class;
        }
    
        @Override
        public boolean matches(Type type) {
            return type.equals(Date.class);
        }
    
        @Override
        public Computation tryDeserialize(SerializedImmutable<Date> value, MatcherGenerators generator, DeserializerContext context) throws DeserializationException {
            Date date = value.getValue();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            
            TypeManager types = context.getTypes();
            types.registerType(DateMatcher.class);
            
            String expression = callMethod(types.getRawTypeName(DateMatcher.class), "matchesDate");
            expression = callMethod(expression, "withDay", valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            expression = callMethod(expression, "withMonth", valueOf(cal.get(Calendar.MONTH)));
            expression = callMethod(expression, "withYear", valueOf(cal.get(Calendar.YEAR)));
            
            return Computation.expression(expression, Matcher.class);
        }
    }

The matcher generator is similar to the setup generator. First you have to define a method `getAdaptedClass()`:

      @Override
      public Class<SerializedImmutable> getAdaptedClass() {
        return SerializedImmutable.class;
      }

This method specifies the type of the serialized value which is handled by this generator. It must return a subclass of `SerializedValue`. As in the setup generator we chose to adapt the class `SerializedImmutable`.
      
The next method `matches(Type type)` defines another property of the serialized value that can be handled:


      @Override
      public boolean matches(Type type) {
        return type.equals(Date.class);
      }

Where `getAdaptedClass()` defines the type of the serialized value, `matches(Type type)` constrains the type of the real value. This method allows to handle multiple types (not only one as in the serializer). For each supported type this method should return `true`. Since we want our example generator to handle `Date` we return `true` for `Date.class`.

As in the `SetupGenerator` both methods `getAdaptedClass()` and `matches(Type type)` only specify which types could be handled. Since multiple `MatcherGenerator` can adapt the same serialized value type and even the same real type, there is a conflict resolution which will prefer the first generator that can successfully generate a matcher (using a sequence defined by the generator hierarchy).

Now let us examine the method `tryDeserialize(SerializedImmutable<Date> value, MatcherGenerators generator, DeserializerContext context)`:

      @Override
      public Computation tryDeserialize(SerializedImmutable<Date> value, MatcherGenerators generator, DeserializerContext context) throws DeserializationException {
        Date date = value.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        TypeManager types = generator.getTypes();
        types.registerType(DateMatcher.class);
        
        String expression = callMethod(types.getBestName(DateMatcher.class), "matchesDate");
        expression = callMethod(expression, "withDay", valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        expression = callMethod(expression, "withMonth", valueOf(cal.get(Calendar.MONTH)));
        expression = callMethod(expression, "withYear", valueOf(cal.get(Calendar.YEAR)));
        
        return new Computation(expression, Matcher.class);
      }

At first we start unwrapping the value which should be matched. Since `Date` is a little bit tricky we first extract a `Calendar` from the date:      

        Date date = value.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

Next we do the necessary imports. We want to match a `Date` and we therefore use a `DateMatcher` which could be found aside the example source code. We aim to produce code that is similar to the following lines (given a date 2012-12-31):
        
    DateMatcher.matchesDate()
        .withDay(31)
        .withMonth(11)
        .withYear(2017);

We prepare this statement step by step:

        String expression = callMethod(types.getBestName(DateMatcher.class), "matchesDate");

prepares the first line.
        
        expression = callMethod(expression, "withDay", valueOf(cal.get(Calendar.DAY_OF_MONTH)));

prepares the line matching the day of month.

        expression = callMethod(expression, "withMonth", valueOf(cal.get(Calendar.MONTH)));

prepares the line matching the month. Note that months in Java are zero-based, the december will be mapped to 11. 

        expression = callMethod(expression, "withYear", valueOf(cal.get(Calendar.YEAR)));

prepares the line for year.

        return new Computation(expression, Matcher.class);
        
returns the whole expression with a type `Matcher`. Testrecorder is yet not really mature in its type inference. It is not guaranteed that using another type than the raw `Matcher` would generate compiling test code. You can try out, but we do not know whether this will be eventually supported.  

To register your `DateMatcherGenerator` for deserialization dispatch you will have to:

* create a directory `agentconfig` in your class path
* create a file `net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator` in this directory
* put the full qualified class name `com.almondtools.testrecorder.examples.deserializers.DateMatcherGenerator` into this file

## Custom Initializers

TODO (TestRecorderAgentInitializer)

## Bundling your custom components

TODO