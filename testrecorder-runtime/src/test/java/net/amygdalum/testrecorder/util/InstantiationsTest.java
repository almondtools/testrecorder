package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;

import net.amygdalum.xrayinterface.GetProperty;
import net.amygdalum.xrayinterface.XRayInterface;

public class InstantiationsTest {

	@BeforeEach
	public void before() throws Exception {
		Instantiations.resetInstatiations();
	}
	
	@Test
	public void testInstantiations() throws Exception {
		assertThat(Instantiations.class).satisfies(utilityClass().conventions());
	}

	@Test
	void testGetObjenesis() throws Exception {
		assertThat(XRayInterface.xray(Instantiations.class).to(OpenInstantiations.class).getObjenesis()).isNull();

		assertThat(Instantiations.newInstance(Object.class)).isInstanceOf(Object.class);
		
		assertThat(XRayInterface.xray(Instantiations.class).to(OpenInstantiations.class).getObjenesis()).isNotNull();
	}

	@Test
	void testResetObjenesis() throws Exception {
		assertThat(XRayInterface.xray(Instantiations.class).to(OpenInstantiations.class).getObjenesis()).isNull();

		Instantiations.newInstance(Object.class);
		
		assertThat(XRayInterface.xray(Instantiations.class).to(OpenInstantiations.class).getObjenesis()).isNotNull();
		
		Instantiations.resetInstatiations();
		
		assertThat(XRayInterface.xray(Instantiations.class).to(OpenInstantiations.class).getObjenesis()).isNull();
	}
	
	interface OpenInstantiations {

		@GetProperty
		Objenesis getObjenesis();
		
	}
}
