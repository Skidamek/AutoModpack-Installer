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
