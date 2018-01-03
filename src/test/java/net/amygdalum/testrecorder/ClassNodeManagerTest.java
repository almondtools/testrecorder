package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;

public class ClassNodeManagerTest {

	private ClassNodeManager manager;
	
	@BeforeEach
	public void before() throws Exception {
		manager = new ClassNodeManager();
	}
	
	@Test
	public void testFetchInterface() throws Exception {
		assertThat(manager.fetch("java/util/concurrent/BlockingQueue")).isNotNull();
	}

	@Test
	public void testFetchClass() throws Exception {
		assertThat(manager.fetch("java/util/concurrent/LinkedBlockingQueue")).isNotNull();
	}
	
	@Test
	public void testFetchInterfaceMethod() throws Exception {
		ClassNode blockingQueue = manager.fetch("java/util/concurrent/BlockingQueue");
		assertThat(manager.fetch(blockingQueue, "add", "(Ljava/lang/Object;)Z")).isNotNull();
	}

	@Test
	public void testInterfaceExtendedMethod() throws Exception {
		ClassNode blockingQueue = manager.fetch("java/util/concurrent/BlockingQueue");
		assertThat(manager.fetch(blockingQueue, "isEmpty", "()Z")).isNotNull();
	}

}
