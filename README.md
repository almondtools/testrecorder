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

At this time there are multiple APIs that could be used to support you in your projects. Not every API is well-documented and not every API is stable. If you are uncertain how to start, __open an issue__ and describe the problem you want to solve. Even if the tool does not support your problem this could give us valuable insight on future features.   

Examples
--------
Examples can be found at [testrecorder-examples](https://github.com/almondtools/testrecorder-examples)

Some additional notes ...
-------------------------
The objective of Testrecorder is to provide an interface that is powerful, clean and extensible. To achieve this we will provide more and more configuration settings to extend the core framework. The fact that tests are generated automatically might rise wrong expectations: Testrecorder will probably always be an experts tool, meaning strong programming and debug skills are recommended to find the correct configuration and the necessary custom extensions.

Testrecorder was not yet tested on a large set of code examples. Some classes are not as easy to serialize as others, so if you encounter problems, try to write an issue. Hopefully - most fixes to such problems should be solvable with custom serializers or custom deserializers.

Feature Requests and Contribution
---------------------------------
At this time I do not use Testrecorder in my own daily work (pretty good tested non-legacy code). And this means that Testrecorder updates are not driven by my own feature requests.

Yet this project is not abandoned. So if there are questions on using Testrecorder (frankly the documentation is quite short) please feel free to open an [issue](https://github.com/almondtools/testrecorder/issues). Of course you can go even further and propose improvements to usability.

The API of Testrecorder is not stable at this time. This means less comfort for users, but more flexibility for contributors. I am open to fundamental redesign of Testrecorder parts. But please do not write an expensive source code contribution (pull request) before discussion the consequences (in an issue) with me. 


Maven Dependency
----------------

```xml
<dependency>
    <groupId>net.amygdalum</groupId>
    <artifactId>testrecorder-annotations</artifactId>
    <version>0.10.3</version>
</dependency>
```

```xml
<dependency>
    <groupId>net.amygdalum</groupId>
    <artifactId>testrecorder-agent</artifactId>
    <version>0.10.3</version>
</dependency>
```

```xml
<dependency>
    <groupId>net.amygdalum</groupId>
    <artifactId>testrecorder-runtime</artifactId>
    <version>0.10.3</version>
</dependency>
```