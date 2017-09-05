package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class DefaultingSerializationProfileTest {

    private SerializationProfile defaultProfile;
    private SerializationProfile profile;
    private DefaultingSerializationProfile defaultingSerializationProfile;

    @Before
    public void before() throws Exception {
        defaultProfile = new DefaultTestRecorderAgentConfig();
        profile = mock(SerializationProfile.class);
        defaultingSerializationProfile = new DefaultingSerializationProfile(profile, defaultProfile);
    }

    @Test
    public void testInherit() throws Exception {
        assertThat(defaultingSerializationProfile.inherit(), is(false));
    }

    @Test
    public void testGetFieldExclusions() throws Exception {
        Fields p1 = Fields.byName("xy");
        Fields p2 = Fields.byName("yx");
        when(profile.getFieldExclusions()).thenReturn(asList(p1, p2));
        
        assertThat(defaultingSerializationProfile.getFieldExclusions(), contains(p1, p2));
    }

    @Test
    public void testGetFieldExclusionsDefault() throws Exception {
        when(profile.getFieldExclusions()).thenReturn(null);
        
        assertThat(defaultingSerializationProfile.getFieldExclusions(), equalTo(defaultProfile.getFieldExclusions()));
    }

    @Test
    public void testGetClassExclusions() throws Exception {
        Classes p1 = Classes.byName("xy");
        Classes p2 = Classes.byName("yx");
        when(profile.getClassExclusions()).thenReturn(asList(p1, p2));
        
        assertThat(defaultingSerializationProfile.getClassExclusions(), contains(p1, p2));
    }
    
    @Test
    public void testGetClassExclusionsDefault() throws Exception {
        when(profile.getClassExclusions()).thenReturn(null);
        
        assertThat(defaultingSerializationProfile.getClassExclusions(), equalTo(defaultProfile.getClassExclusions()));
    }

    @Test
    public void testGetGlobalFields() throws Exception {
        Fields globalField = Fields.byName("global");
		when(profile.getGlobalFields()).thenReturn(asList(globalField));

        assertThat(defaultingSerializationProfile.getGlobalFields(), contains(globalField));
    }

    @Test
    public void testGetGlobalFieldsDefault() throws Exception {
        when(profile.getGlobalFields()).thenReturn(null);

        assertThat(defaultingSerializationProfile.getGlobalFields(), equalTo(defaultProfile.getGlobalFields()));
    }

}
