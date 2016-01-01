/**
 * Copyright 2016 Philipp Arndt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.rnd7.fixfilenames;

import java.io.File;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.TreeTraverser;

public class Main {
	private static final String BAD_CHAR = "\u0308";

	public static void main(final String[] args) {
		if (args.length != 1) {
			System.out.println("Expected one argument (folder)");
			return;
		}

		final Stream<File> stream = traverser(new File(args[0]));
		stream.forEach(file -> {
			String name = file.getName();
			if (name.contains(BAD_CHAR)) {
				name = name.replaceAll("a" + BAD_CHAR, "ä");
				name = name.replaceAll("o" + BAD_CHAR, "ö");
				name = name.replaceAll("u" + BAD_CHAR, "ü");
				name = name.replaceAll("A" + BAD_CHAR, "Ä");
				name = name.replaceAll("O" + BAD_CHAR, "Ö");
				name = name.replaceAll("U" + BAD_CHAR, "Ü");

				final String newName = formatNameForFile(name);

				final File newFile = new File(file.getParentFile(), newName);
				file.renameTo(newFile);

				System.out.println(String.format("%s -> %s", file.getName(), newFile.getName()));
			}
		});
	}

	private static String formatNameForFile(final String originalName) {
		String name = originalName.replaceAll(":", " - ");
		name = name.replaceAll("[\\/:*?\"<>|]", " ");
		name = name.replaceAll("  ", " ");
		name = name.replaceAll("ä", "ae");
		name = name.replaceAll("ö", "oe");
		name = name.replaceAll("ü", "ue");
		name = name.replaceAll("ß", "ss");
		name = name.replaceAll("Ä", "Ae");
		name = name.replaceAll("Ö", "Oe");
		name = name.replaceAll("Ü", "Ue");
		name = name.trim();
		return name;
	}

	public static Stream<File> traverser(final File rootFolder) {
		final TreeTraverser<File> traverser = new FileTraverser();
		final Stream<File> stream = StreamSupport.stream(traverser.postOrderTraversal(rootFolder).spliterator(), false);

		return stream.sorted(Comparator.comparing(File::getAbsolutePath));
	}
}
