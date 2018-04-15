Troubleshooting Testrecorder
============================

Maybe you came into trouble with using testrecorder. Please open an [issue](https://github.com/almondtools/testrecorder/issues) - testrecorder is not failsafe yet, but we can only come to a better level if issues are reported.

However there are some known issues, that may be solved without a bugfix:

### Testrecorder timeouts or throws fatal errors

Recording complex objects could sometimes lead to

* timeouts (`java.util.concurrent.CompletionException`)
* internal errors (subclasses of `java.lang.VirtualMachineError`, e.g. `java.lang.OutOfMemoryError`, `java.lang.StackOverflowError`,`java.lang.InternalError`
* or fatal jvm shutdowns (typically dumping a `hs_err_pidXXXX.log`)

These are symptoms that testrecorder records a part of the runtime system (classes responsible for multithreading, system services, runtime services and reflection). In almost every case this should be prevented:

* because it consumes memory and processor time
* because is is generally not safe to read objects that manage "reading objects"

Here is a list of classes that indirectly reference a large part of the jvm runtime system:

* `java.lang.Thread` 
* `java.io.*`
* ...

To solve the problem, try to apply following strategies:

* exclude the critical classes from recording (this does only work when generated tests do not depend on them)
* write a [custom serializer/deserializer](Extending.md) for these classes
* make use of [Facades](facades.md) 