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

package net.fabricmc.installer.modrinthAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mjson.Json;

public class ModrinthAPI {
	public static String getLatestDownloadUrl(String gameVersion) throws IOException {
		String apiUrl = "https://api.modrinth.com/v2/project/k68glP2e/version?game_versions=[\"" + gameVersion + "\"]&loaders=[\"fabric\"]";
		apiUrl = apiUrl.replaceAll("\"", "%22"); // so important!

		// Effettua la richiesta GET all'API di Modrinth
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		// Imposta l'header User-Agent
		connection.setRequestProperty("User-Agent", "alepagliaccio/automodpack/1.0 (alessandropiccin.com)");

		// Legge la risposta JSON
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuilder response = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			response.append(line);
		}

		reader.close();

		// Analizza la risposta JSON
		Json json = Json.read(response.toString());
		Json version = json.at(0);
		String downloadUrl = version.at("files").at(0).at("url").asString();

		return downloadUrl;
	}
}
