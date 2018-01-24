Recording IO with Testrecorder
==============================

Before discussing how IO could be captured with Testrecorder, let us have a look on typical IO, to understand why IO cannot be captured in the same way as common variable state.

Typical sources of input are:
* reading from console
* reading from a file
* reading system date/time
* user interactions with a gui
* computing a random number (using system time as seed)

Typical outputs are
* writing to console
* writing to a file
* displaying data in the gui

From these examples we derive:
* input is almost never part of the state before a method call, it is received while executing the method
* output is almost never part of the state after a method call, it is generated while executing the method

And further:
* input is originating from outside the java program, from a source that is not reproducible programmatically
* output is only verifiable outside the java program, consumed from an entity that is not reproducible

Testrecorder follows a well known paradigm to simulate output/input - verifying and mocking. In the following sections we will show how we can mock input and capture output. However mocking/verifying with Testrecorder is not always applicable without modification of the code under test. The last section will give some hints how to change the code such that mocking is applicable.

## Input

To capture the input, for mocking it in the later test cases, there are two variants:
* mark the method that produces input with the `@SerializationProfile.Input` annotation
* Use a Testrecorder configuration implementing the method `getInputs()` and return the method that produces input

Whatever variant you choose, the result and the arguments of the method will be interpreted as input data.

Note that both variants do not work on classes/methods of the java standard api (packages beginning with "java"). These classes are protected from mocking, so you will have to use the workaround which is explained below.

## Output

To capture output, for verifying it in the later test cases, there are two variants:
* mark the method that produces input with the `@SerializationProfile.Output` annotation
* Use a Testrecorder configuration implementing the method `getOutputs()` and return the method that transfers output

Whatever variant you choose, the result and the arguments of the method will be interpreted as input data.

Note that both variants do not work on classes/methods of the java standard api (packages beginning with `java`). These classes are protected from mocking, so you will have to use the workaround which is explained below.

## Managing Input/Output in protected packages

As explained above: Input/Output capturing is not possible for classes in the java standard api. This is quite hard because most IO is probably done with classes in the standard api.

Yet the best solution to this problem known to me, is to wrap the input/output call in another method (which is not in a package starting with `java`) and to annotate/configure this method to be input/output.
