[Testrecorder](http://testrecorder.amygdalum.net/)
============
[![Build Status](https://travis-ci.org/almondtools/testrecorder.svg?branch=master)](https://travis-ci.org/almondtools/testrecorder)
[![codecov](https://codecov.io/gh/almondtools/testrecorder/branch/master/graph/badge.svg)](https://codecov.io/gh/almondtools/testrecorder)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b988e1773ef042c5be578b49c8a432a0)](https://www.codacy.com/project/almondtools/testrecorder/dashboard?utm_source=github.com&utm_medium=referral&utm_content=almondtools/testrecorder&utm_campaign=Badge_Grade_Dashboard)

__Testrecorder__ provides and easy way to generate characterizing unit tests (not integration tests!) by just recording interactions of a running system.

For more detailed information visit the [Testrecorder](http://testrecorder.amygdalum.net/) project page.

__Some more Documentation (Work in Progress)__

* [An Introduction to the Testrecorder Architecture](doc/Architecture.md)
* [Tuning the Output of Testrecorder](doc/TuningOutput.md)
* [Excluding Objects from Recording](doc/ExcludingObjects.md)
* [Recording Input/Output](doc/RecordingIO.md)
* [Recording Enterprise Java](doc/RecordingEE.md)
* [Using the Testrecorder-API to serialize data and generate code](doc/API.md)
* [Extending Testrecorder with Custom Components](doc/Extending.md)
* [Creating a Custom Agent](doc/CreatingCustomAgents.md) 

Examples
========
Examples can be found at [testrecorder-examples](https://github.com/almondtools/testrecorder-examples)

Some additional notes ...
=========================
The objective of Testrecorder is to provide an interface that is powerful, clean and extensible. To achieve this we will provide more and more configuration settings to extend the core framework. The fact that tests are generated automatically might rise wrong expectations: Testrecorder will probably always be an experts tool, meaning strong programming and debug skills are recommended to find the correct configuration and the necessary custom extensions.

Testrecorder was not yet tested on a large set of code examples. Some classes are not as easy to serialize as others, so if you encounter problems, try to write an issue. Hopefully - most fixes to such problems should be solvable with custom serializers or custom deserializers.

Maven Dependency
================

```xml
<dependency>
    <groupId>net.amygdalum</groupId>
    <artifactId>testrecorder-annotations</artifactId>
    <version>0.10.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>net.amygdalum</groupId>
    <artifactId>testrecorder-agent</artifactId>
    <version>0.10.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>net.amygdalum</groupId>
    <artifactId>testrecorder-runtime</artifactId>
    <version>0.10.0</version>
</dependency>
```