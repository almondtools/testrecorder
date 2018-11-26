package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;

public class ClassNodeManagerTest {

	private ClassLoader loader;
	private ClassNodeManager manager;
	
	@BeforeEach
	public void before() throws Exception {
		loader = ClassNodeManager.class.getClassLoader();
		manager = new ClassNodeManager();
	}
	
	@Test
		public void testReaderForetchInterface() throws Exception {
			assertThat(manager.fetch("java/util/concurrent/BlockingQueue", loader)).isNotNull();
		}

	@Test
		public void testReaderForetchClass() throws Exception {
			assertThat(manager.fetch("java/util/concurrent/LinkedBlockingQueue", loader)).isNotNull();
		}
	
	@Test
		public void testReaderForetchInterfaceMethod() throws Exception {
			ClassNode blockingQueue = manager.fetch("java/util/concurrent/BlockingQueue", loader);
			assertThat(manager.fetch(blockingQueue, "add", "(Ljava/lang/Object;)Z", loader)).isNotNull();
		}

	@Test
	public void testInterfaceExtendedMethod() throws Exception {
		ClassNode blockingQueue = manager.fetch("java/util/concurrent/BlockingQueue", loader);
		assertThat(manager.fetch(blockingQueue, "isEmpty", "()Z", loader)).isNotNull();
	}

}
