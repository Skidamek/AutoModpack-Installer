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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mjson.Json;

public class ModrinthAPI {
	private static final String BASE_URL = "https://api.modrinth.com/v2/project/k68glP2e/version?loaders=[\"fabric\"]";
	private static final List<String> gameVersionsFinal = new ArrayList<>();

	public static String getLatestDownloadUrl(String gameVersion) {
		String apiUrl = buildApiUrl(gameVersion);
		Json json = fetchJson(apiUrl);

		if (json == null) {
			return null;
		}

		return extractDownloadUrl(json);
	}

	public static List<String> getSupportedMinecraftVersions() {
		if (!gameVersionsFinal.isEmpty()) {
			return gameVersionsFinal;
		}

		String apiUrl = BASE_URL.replaceAll("\"", "%22");
		Json json = fetchJson(apiUrl);

		if (json == null) {
			return null;
		}

		populateGameVersions(json);
		return gameVersionsFinal;
	}

	private static String buildApiUrl(String gameVersion) {
		String apiUrl = BASE_URL + "&game_versions=[\"" + gameVersion + "\"]";
		return apiUrl.replaceAll("\"", "%22");
	}

	private static String getApiResponse(String apiUrl) throws IOException {
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Skidamek/AutoModpack-Installer/1.0");

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			StringBuilder response = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			return response.toString();
		}
	}

	private static String extractDownloadUrl(Json json) {
		Json version = json.at(0);
		return version.at("files").at(0).at("url").asString();
	}

	private static Json fetchJson(String apiUrl) {
		try {
			String response = getApiResponse(apiUrl);
			return Json.read(response);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void populateGameVersions(Json json) {
		for (int i = 0; i < json.asJsonList().size(); i++) {
			Json version = json.at(i);
			List<Object> gameVersions = version.at("game_versions").asList();

			for (Object gameVersion : gameVersions) {
				String gameVersionString = Objects.toString(gameVersion, null);

				if (gameVersionString == null) {
					continue;
				}

				if (!gameVersionsFinal.contains(gameVersionString)) {
					gameVersionsFinal.add(gameVersionString);
				}
			}
		}
	}
}
