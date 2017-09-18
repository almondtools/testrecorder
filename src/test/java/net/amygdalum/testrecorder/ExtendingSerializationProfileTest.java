package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class ExtendingSerializationProfileTest {

    private SerializationProfile defaultProfile;
    private SerializationProfile profile;
    private ExtendingSerializationProfile extendingSerializationProfile;

    @Before
    public void before() throws Exception {
        defaultProfile = mock(SerializationProfile.class);
        profile = mock(SerializationProfile.class);
        extendingSerializationProfile = new ExtendingSerializationProfile(profile, defaultProfile);
    }

    @Test
    public void testGetFieldExclusions() throws Exception {
        Fields p1 = Fields.byName("xy");
        Fields p2 = Fields.byName("yx");
        when(defaultProfile.getFieldExclusions()).thenReturn(asList(p1));
        when(profile.getFieldExclusions()).thenReturn(asList(p2));

        assertThat(extendingSerializationProfile.getFieldExclusions(), containsInAnyOrder(p1, p2));
    }

    @Test
    public void testGetFieldExclusionsDefault() throws Exception {
        Fields p1 = Fields.byName("xy");
        when(defaultProfile.getFieldExclusions()).thenReturn(asList(p1));
        when(profile.getFieldExclusions()).thenReturn(null);

        assertThat(extendingSerializationProfile.getFieldExclusions(), contains(p1));
    }

    @Test
    public void testGetFieldExclusionsNoDefault() throws Exception {
        Fields p1 = Fields.byName("xy");
        when(defaultProfile.getFieldExclusions()).thenReturn(null);
        when(profile.getFieldExclusions()).thenReturn(asList(p1));

        assertThat(extendingSerializationProfile.getFieldExclusions(), contains(p1));
    }

    @Test
    public void testGetClassExclusions() throws Exception {
        Classes p1 = Classes.byName("xy");
        Classes p2 = Classes.byName("yx");
        when(defaultProfile.getClassExclusions()).thenReturn(asList(p1));
        when(profile.getClassExclusions()).thenReturn(asList(p2));

        assertThat(extendingSerializationProfile.getClassExclusions(), containsInAnyOrder(p1, p2));
    }

    @Test
    public void testGetClassExclusionsDefault() throws Exception {
        Classes p1 = Classes.byName("xy");
        when(defaultProfile.getClassExclusions()).thenReturn(asList(p1));
        when(profile.getClassExclusions()).thenReturn(null);

        assertThat(extendingSerializationProfile.getClassExclusions(), contains(p1));
    }

    @Test
    public void testGetClassExclusionsNoDefault() throws Exception {
        Classes p2 = Classes.byName("yx");
        when(defaultProfile.getClassExclusions()).thenReturn(null);
        when(profile.getClassExclusions()).thenReturn(asList(p2));

        assertThat(extendingSerializationProfile.getClassExclusions(), contains(p2));
    }

    @Test
    public void testGetGlobalFields() throws Exception {
        Fields defaultField = Fields.byName("str");
		when(defaultProfile.getGlobalFields()).thenReturn(asList(defaultField));
        Fields valueField = Fields.byName("global");
		when(profile.getGlobalFields()).thenReturn(asList(valueField));

        assertThat(extendingSerializationProfile.getGlobalFields(), containsInAnyOrder(defaultField,valueField));
    }

    @Test
    public void testGetGlobalFieldsDefault() throws Exception {
        Fields defaultField = Fields.byName("str");
		when(defaultProfile.getGlobalFields()).thenReturn(asList(defaultField));
        when(profile.getGlobalFields()).thenReturn(null);

        assertThat(extendingSerializationProfile.getGlobalFields(), contains(defaultField));
    }

    @Test
    public void testGetGlobalFieldsNoDefault() throws Exception {
        when(defaultProfile.getGlobalFields()).thenReturn(null);
        Fields valueField = Fields.byName("global");
		when(profile.getGlobalFields()).thenReturn(asList(valueField));

        assertThat(extendingSerializationProfile.getGlobalFields(), contains(valueField));
    }

}
