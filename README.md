testrecorder
============

Starting with legacy code (meaning "untested code") is usually hard and thankless. The last time I set together with some developers we agreed that the right way would be to fix the current behavior with automated integration tests and then refactor the system until it is understandable.

Yet writing integration tests is hard and thankless, too. And wouldn't it be amazing if a tool generates all tests for you. It is important to know that writing integration tests is not really complex if you start with common user scenarios. The tool would have to collect all input and all output of tested methods. An integration test will setup the input objects and assert that the output objects equal the collected output objects.

Our tool for this is **testrecorder**.



TODOs
=====
- agent instead of main class injection
- Other Serializers
- More readable generated Tests
- Less  warnings in generated Tests
- Other Profiles (than `DefaultSerializationProfile)` that can handle serialization of static or generated data
- Higher Test Coverage
- Tutorial
