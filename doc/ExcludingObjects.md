Exclusions from Recording
=========================

Test recording works quite easy for simple cases. Yet some cases cause trouble because the recorded state of some object is occassionally huge. In many cases these objects do not contribute to the tests, so it would be quite fine to exclude the recording of such objects.

Exclusions often cause problems if applied to recordings of large, heavily integrated methods. In this case it is probably best not to exclude the type but to write a [custom serializer/deserializer](Extending.md). 

## Excluded

To exclude a type/a field from being recorded, there are two variants:

* Mark the type/field to be excluded with the `Excluded` annotation
* Use a `SerializationProfile` implementing the method `getClassExclusions()`/`getFieldExclusions()` and return the type/field that should be excluded

Whatever variant you choose, each type matching the above specification will not be recorded.
