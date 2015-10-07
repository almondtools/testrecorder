testrecorder
============

TODOs
=====
- BeanSerializer:
  - serializes to SerializedObject
  - additionally checks whether the object is completely serializable by its property methods (getters/setters)
  - and add this information to the SerializedObject
  - writing test code with `ObjectToSetupCode` and `ObjectToMatcherCode` can then rely on this knowledge and can generate more readable test code
- Other Serializers
- Other Profiles (than `DefaultSerializationProfile)` that can handle serialization of static or generated data
