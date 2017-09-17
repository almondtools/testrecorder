package net.amygdalum.testrecorder.scenarios;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.CombinableMatcher;
import org.junit.Test;

import net.amygdalum.testrecorder.runtime.GenericMatcher;
import net.amygdalum.testrecorder.runtime.OutputDecorator;

public class OutputsRecordedDemoTest {

	@Test
	public void testRecorded0() throws Exception {

		//Arrange
	    Outputs outputs1 = new Outputs();
	    Outputs outputs2 = new OutputDecorator<>(outputs1)
	    	.expect("conditionalReturnOutput", new Class[]{char.class}, true, equalTo('a'))
	    	.expect("conditionalReturnOutput", new Class[]{char.class}, true, equalTo(','))
	    	.expect("conditionalReturnOutput", new Class[]{char.class}, false, equalTo(' '))
	    	.expect("conditionalReturnOutput", new Class[]{char.class}, true, equalTo('b'))
	    	.expect("conditionalReturnOutput", new Class[]{char.class}, false, equalTo('\n'))
	    	.end();

	    //Act
	    outputs2.recordedWithConditionalReturn();

	    //Assert
	    assertThat("expected no change, but was:", outputs2, CombinableMatcher.both(new GenericMatcher() {
	    }.matching(Outputs.class)).and(OutputDecorator.verifies()));
	}
}