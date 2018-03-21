package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ProfileTest {

	@Test
	public void testStartTimeIsCurrentTimeMillis() throws Exception {
		long before = System.currentTimeMillis();

		Profile profile = Profile.start(Object.class);

		long after = System.currentTimeMillis();

		assertThat(profile.start).isBetween(before, after);
	}

	@Test
	public void testStopTimeIsCurrentTimeMillis() throws Exception {
		long before = System.currentTimeMillis();

		Profile profile = Profile.start(Object.class);
		profile.stop();

		long after = System.currentTimeMillis();

		assertThat(profile.duration).isLessThanOrEqualTo(after - before);
	}

	@Test
	public void testEquals() throws Exception {
		Profile profile = Profile.start(Object.class);

		assertThat(profile).satisfies(defaultEquality()
			.andEqualTo(Profile.start(Object.class))
			.andNotEqualTo(Profile.start(String.class))
			.conventions());
	}

	@Test
	public void testToStringNotStopped() throws Exception {
		Profile profile = Profile.start(Object.class);

		assertThat(profile.toString()).matches("java.lang.Object:timeout");
	}

	@Test
	public void testToStringStopped() throws Exception {
		Profile profile = Profile.start(Object.class);
		profile.stop();

		assertThat(profile.toString()).matches("java.lang.Object:\\d+");
	}

	@Test
	public void testCompareToComparesByDuration() throws Exception {
		List<Profile> list = new ArrayList<>();
		Profile shortduration = Profile.start(String.class);
		shortduration.duration = 1;
		list.add(shortduration);
		Profile longduration = Profile.start(Integer.class);
		longduration.duration = 100;
		list.add(longduration);

		Collections.sort(list);

		assertThat(list).containsExactlyInAnyOrder(longduration, shortduration);
	}

	@Test
	public void testCompareToComparesByStartOnEqualDuration() throws Exception {
		List<Profile> list = new ArrayList<>();
		Profile late = Profile.start(Integer.class);
		late.duration = 10;
		late.start = 10;
		list.add(late);
		Profile early = Profile.start(String.class);
		early.duration = 10;
		early.start = 1;
		list.add(early);
		
		Collections.sort(list);
		
		assertThat(list).containsExactlyInAnyOrder(early, late);
	}
	
}
