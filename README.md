[Testrecorder](http://testrecorder.amygdalum.net/)
============
[![Build Status](https://travis-ci.org/almondtools/testrecorder.svg?branch=master)](https://travis-ci.org/almondtools/testrecorder)
[![codecov](https://codecov.io/gh/almondtools/testrecorder/branch/master/graph/badge.svg)](https://codecov.io/gh/almondtools/testrecorder)

__Testrecorder__ provides and easy way to generate characterizing unit tests (not integration tests!) by just recording interactions of a running system.

For more detailed information visit the [Testrecorder](http://testrecorder.amygdalum.net/) project page.

__Some more Documentation (Work in Progress)__

* [An Introduction to the Testrecorder Architecture](doc/Architecture.md)
* [Tuning the Output of Testrecorder](doc/TuningOutput.md)
* [Recordering Input/Output with Testrecorder](doc/RecordingIO.md)
* [Using the Testrecorder-API to serialize data and generate code](doc/API.md)
* [Extending Testrecorder with Custom Components](doc/Extending.md)

Examples
========
Examples can be found at [testrecorder-examples](https://github.com/almondtools/testrecorder-examples)

Some additional notes ...
=========================
The objective of Testrecorder is to provide an interface that is powerful, clean and extensible. To achieve this we will provide more and more configuration settings to extend the core framework. The fact that tests are generated automatically might rise wrong expectations: Testrecorder will probably always be an experts tool, meaning strong programming and debug skills are recommended to find the correct configuration and the necessary custom extensions.

Testrecorder was not yet tested on a large set of code examples. Some classes are not as easy to serialize as others, so if you encounter problems, try to write an issue. Hopefully - most fixes to such problems should be solvable with custom serializers or custom deserializers.