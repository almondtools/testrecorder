package net.amygdalum.testrecorder.scenarios;

import org.junit.Test;
import org.junit.runner.RunWith;
import net.amygdalum.testrecorder.util.RecordInput;
import net.amygdalum.testrecorder.util.SetupInput;
import net.amygdalum.testrecorder.util.IORecorder;
import net.amygdalum.testrecorder.runtime.GenericMatcher;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;



@SuppressWarnings("unused")
@RunWith(IORecorder.class)
@RecordInput(value={"net.amygdalum.testrecorder.scenarios.SystemInput"}, signatures={"net.amygdalum.testrecorder.scenarios.SystemInput.currentTimeMillis()"})
public class SystemInputRecordedTest {

  public SetupInput setupInput;

  @Before
  public void before() throws Exception {
  }


  @Test
  public void testGetTimestamp0() throws Exception {

    //Arrange
    setupInput.provide(SystemInput.class, "currentTimeMillis", 1504539038879l);
    SystemInput systemInput1 = new SystemInput();

    //Act
    long long1 = systemInput1.getTimestamp();

    //Assert
    assertThat(long1, equalTo(1504539038879l));
    assertThat(systemInput1, new GenericMatcher() {
    long timestamp = 1504539038879l;
    }.matching(SystemInput.class));
  }

}