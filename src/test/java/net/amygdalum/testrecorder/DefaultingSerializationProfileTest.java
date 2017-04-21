package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.util.testobjects.Static;

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

    @SuppressWarnings("unchecked")
    @Test
    public void testGetFieldExclusions() throws Exception {
        Predicate<Field> p1 = f -> f.getName().equals("xy");
        Predicate<Field> p2 = f -> f.getName().equals("yx");
        when(profile.getFieldExclusions()).thenReturn(asList(p1, p2));
        
        assertThat(defaultingSerializationProfile.getFieldExclusions(), contains(p1, p2));
    }

    @Test
    public void testGetFieldExclusionsDefault() throws Exception {
        when(profile.getFieldExclusions()).thenReturn(null);
        
        assertThat(defaultingSerializationProfile.getFieldExclusions(), equalTo(defaultProfile.getFieldExclusions()));
    }
    @SuppressWarnings("unchecked")
    @Test
    public void testGetClassExclusions() throws Exception {
        Predicate<Class<?>> p1 = f -> f.getName().equals("xy");
        Predicate<Class<?>> p2 = f -> f.getName().equals("yx");
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
        when(profile.getGlobalFields()).thenReturn(asList(Static.class.getDeclaredField("global")));

        assertThat(defaultingSerializationProfile.getGlobalFields(), contains(Static.class.getDeclaredField("global")));
    }

    @Test
    public void testGetGlobalFieldsDefault() throws Exception {
        when(profile.getGlobalFields()).thenReturn(null);

        assertThat(defaultingSerializationProfile.getGlobalFields(), equalTo(defaultProfile.getGlobalFields()));
    }

}
