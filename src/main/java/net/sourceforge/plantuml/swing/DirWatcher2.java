/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 * 
 */
package net.sourceforge.plantuml.swing;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.cli.CliOptions;
import net.sourceforge.plantuml.file.FileWatcher;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.preproc.FileWithSuffix;

public class DirWatcher2 {
	// ::remove file when __CORE__

	final private File dir;
	final private CliOptions option;
	final private String pattern;

	final private Map<File, FileWatcher> modifieds = new ConcurrentHashMap<File, FileWatcher>();
	final private ExecutorService executorService;

	public DirWatcher2(File dir, CliOptions option, String pattern) {
		this.dir = dir;
		this.option = option;
		this.pattern = pattern;
		final int nb = CliOptions.defaultNbThreads();
		this.executorService = Executors.newFixedThreadPool(nb);

	}

	public Map<File, Future<List<GeneratedImage>>> buildCreatedFiles() throws IOException, InterruptedException {
		final Map<File, Future<List<GeneratedImage>>> result = new TreeMap<File, Future<List<GeneratedImage>>>();
		if (dir.listFiles() != null)
			for (final File f : dir.listFiles()) {
				if (f.isFile() == false)
					continue;

				if (fileToProcess(f.getName()) == false)
					continue;

				final FileWatcher watcher = modifieds.get(f);

				if (watcher == null || watcher.hasChanged()) {
					final SourceFileReader sourceFileReader = new SourceFileReader(option.getDefaultDefines(f), f,
							option.getOutputDir(), option.getConfig(), option.getCharset(),
							option.getFileFormatOption());
					modifieds.put(f, new FileWatcher(Collections.singleton(f)));
					final Future<List<GeneratedImage>> value = executorService
							.submit(new Callable<List<GeneratedImage>>() {
								public List<GeneratedImage> call() throws Exception {
									try {
										final List<GeneratedImage> generatedImages = sourceFileReader
												.getGeneratedImages();
										final Set<File> files = FileWithSuffix
												.convert(sourceFileReader.getIncludedFiles());
										files.add(f);
										modifieds.put(f, new FileWatcher(files));
										return Collections.unmodifiableList(generatedImages);
									} catch (Exception e) {
										Logme.error(e);
										return Collections.emptyList();
									}
								}
							});
					result.put(f, value);
				}
			}
		return Collections.unmodifiableMap(result);
	}

	private boolean fileToProcess(String name) {
		return name.matches(pattern);
	}

	public final File getDir() {
		return dir;
	}

	public void cancel() {
		executorService.shutdownNow();
	}

	public void waitEnd() throws InterruptedException {
		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

}
