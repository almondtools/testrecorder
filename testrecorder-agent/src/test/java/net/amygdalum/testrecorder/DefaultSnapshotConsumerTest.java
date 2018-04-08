package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.Test;

public class DefaultSnapshotConsumerTest {

	@Test
	public void testAccept() throws Exception {
		DefaultSnapshotConsumer consumer = new DefaultSnapshotConsumer();
		
		assertThatCode(() -> consumer.accept(null)).doesNotThrowAnyException();
	}

}
