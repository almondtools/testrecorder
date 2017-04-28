package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.Static;

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
    public void testInherit() throws Exception {
        assertThat(extendingSerializationProfile.inherit(), is(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetFieldExclusions() throws Exception {
        Predicate<Field> p1 = f -> f.getName().equals("xy");
        Predicate<Field> p2 = f -> f.getName().equals("yx");
        when(defaultProfile.getFieldExclusions()).thenReturn(asList(p1));
        when(profile.getFieldExclusions()).thenReturn(asList(p2));

        assertThat(extendingSerializationProfile.getFieldExclusions(), containsInAnyOrder(p1, p2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetFieldExclusionsDefault() throws Exception {
        Predicate<Field> p1 = f -> f.getName().equals("xy");
        when(defaultProfile.getFieldExclusions()).thenReturn(asList(p1));
        when(profile.getFieldExclusions()).thenReturn(null);

        assertThat(extendingSerializationProfile.getFieldExclusions(), contains(p1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetFieldExclusionsNoDefault() throws Exception {
        Predicate<Field> p1 = f -> f.getName().equals("xy");
        when(defaultProfile.getFieldExclusions()).thenReturn(null);
        when(profile.getFieldExclusions()).thenReturn(asList(p1));

        assertThat(extendingSerializationProfile.getFieldExclusions(), contains(p1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetClassExclusions() throws Exception {
        Predicate<Class<?>> p1 = f -> f.getName().equals("xy");
        Predicate<Class<?>> p2 = f -> f.getName().equals("yx");
        when(defaultProfile.getClassExclusions()).thenReturn(asList(p1));
        when(profile.getClassExclusions()).thenReturn(asList(p2));

        assertThat(extendingSerializationProfile.getClassExclusions(), containsInAnyOrder(p1, p2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetClassExclusionsDefault() throws Exception {
        Predicate<Class<?>> p1 = f -> f.getName().equals("xy");
        when(defaultProfile.getClassExclusions()).thenReturn(asList(p1));
        when(profile.getClassExclusions()).thenReturn(null);

        assertThat(extendingSerializationProfile.getClassExclusions(), contains(p1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetClassExclusionsNoDefault() throws Exception {
        Predicate<Class<?>> p2 = f -> f.getName().equals("yx");
        when(defaultProfile.getClassExclusions()).thenReturn(null);
        when(profile.getClassExclusions()).thenReturn(asList(p2));

        assertThat(extendingSerializationProfile.getClassExclusions(), contains(p2));
    }

    @Test
    public void testGetGlobalFields() throws Exception {
        when(defaultProfile.getGlobalFields()).thenReturn(asList(Simple.class.getDeclaredField("str")));
        when(profile.getGlobalFields()).thenReturn(asList(Static.class.getDeclaredField("global")));

        assertThat(extendingSerializationProfile.getGlobalFields(), containsInAnyOrder(Simple.class.getDeclaredField("str"),Static.class.getDeclaredField("global")));
    }

    @Test
    public void testGetGlobalFieldsDefault() throws Exception {
        when(defaultProfile.getGlobalFields()).thenReturn(asList(Simple.class.getDeclaredField("str")));
        when(profile.getGlobalFields()).thenReturn(null);

        assertThat(extendingSerializationProfile.getGlobalFields(), contains(Simple.class.getDeclaredField("str")));
    }

    @Test
    public void testGetGlobalFieldsNoDefault() throws Exception {
        when(defaultProfile.getGlobalFields()).thenReturn(null);
        when(profile.getGlobalFields()).thenReturn(asList(Static.class.getDeclaredField("global")));

        assertThat(extendingSerializationProfile.getGlobalFields(), contains(Static.class.getDeclaredField("global")));
    }

}
