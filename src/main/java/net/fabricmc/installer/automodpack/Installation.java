/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.installer.automodpack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Installation {
	public static void installAutomodpack(String gameVersion, Path mcPath) {
		String url = ModrinthAPI.getLatestDownloadUrl(gameVersion);

		Path targetDirectory = mcPath.resolve("mods");

		try {
			if (!Files.exists(targetDirectory)) {
				Files.createDirectories(targetDirectory);
			}

			URL downloadUrl = new URL(url);
			InputStream inputStream = null;

			try {
				inputStream = downloadUrl.openStream();
				String fileName = downloadUrl.getFile();
				Path targetFile = targetDirectory.resolve(fileName.substring(fileName.lastIndexOf('/') + 1)); // Ottieni solo il nome del file dal percorso completo
				Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
