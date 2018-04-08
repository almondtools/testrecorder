package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.runtime.EnumMatcher.matchingEnum;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.junit.Test;

import net.amygdalum.testrecorder.runtime.GenericMatcher;
import net.amygdalum.testrecorder.runtime.GenericObject;
import net.amygdalum.testrecorder.runtime.Wrapped;



@SuppressWarnings("unused")
public class NestedEnumsRecordedTest {



  @Test
  public void testInc0() throws Exception {

    //Arrange
    NestedEnums nestedEnums1 = new NestedEnums();
    NestedEnum nestedEnum2 = new GenericObject() {
    Enum<?> enumValue = (Enum<?>) Wrapped.enumType("net.amygdalum.testrecorder.scenarios.NestedEnum$Nested", "FIRST").value();
    int value = 0;
    }.as(NestedEnum.class);

    //Act
    nestedEnums1.inc(nestedEnum2);

    //Assert
    assertThat("expected no change, but was:", nestedEnums1, new GenericMatcher() {
    }.matching(NestedEnums.class));
    assertThat("expected change:", nestedEnum2, new GenericMatcher() {
    Matcher<?> enumValue = matchingEnum("FIRST");
    int value = 1;
    }.matching(NestedEnum.class));
  }

}