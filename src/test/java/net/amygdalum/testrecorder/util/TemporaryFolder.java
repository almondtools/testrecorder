package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

public class TemporaryFolder {

	private Path root;
	private Set<Path> files;

	public TemporaryFolder() {
		this.files = new HashSet<>();
	}

	public Path getRoot() {
		return root;
	}

	public Path resolve(String path) {
		return root.resolve(path);
	}

	public List<String> fileNames(String... directories) {
		Stream<Path> filter = files(directories);
		return filter
			.map(p -> p.getFileName().toString())
			.collect(toList());
	}

	public Stream<Path> files(String... directories) {
		try {
			Path path = root;
			for (String directory : directories) {
				path = path.resolve(directory);
			}
			return Files.walk(path)
				.filter(p -> Files.isRegularFile(p));
		} catch (IOException e) {
			return Stream.empty();
		}
	}

	public Path provideFolder(String directoryName) throws IOException {
		Path dir = Files.createDirectories(root.resolve(directoryName));
		dir.toFile().deleteOnExit();
		files.add(dir);
		return dir;
	}

	public Path provideFile(String fileName, byte[] bytes) throws IOException {
		Path file = Files.write(root.resolve(fileName), bytes, StandardOpenOption.CREATE);
		file.toFile().deleteOnExit();
		files.add(file);
		return file;
	}

	public void prepare() throws IOException {
		root = File.createTempFile("temporary", "", null).toPath();
		Files.deleteIfExists(root);
		Files.createDirectories(root);
	}

	public void close() throws IOException {
		List<Path> allfiles = files.stream()
			.flatMap(path -> {
				try {
					return Files.walk(path)
						.filter(Files::exists)
						.filter(Files::isRegularFile);
				} catch (IOException e) {
					return Stream.empty();
				}
			})
			.filter(Files::exists)
			.map(Path::toAbsolutePath)
			.sorted(Comparator.reverseOrder())
			.collect(toList());

		Queue<Path> rejected = new LinkedList<>();
		for (Path file : allfiles) {
			try {
				Files.deleteIfExists(file);
			} catch (IOException e) {
				rejected.add(file);
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		for (Path file : rejected) {
			Files.deleteIfExists(file);
		}
	}

}
