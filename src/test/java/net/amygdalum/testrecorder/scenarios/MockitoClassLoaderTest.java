package net.amygdalum.testrecorder.scenarios;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	HiddenInputTest.class,
	InputDecoratorTest.class,
	HiddenOutputTest.class,
	OutputDecoratorTest.class
})
public class MockitoClassLoaderTest {
}