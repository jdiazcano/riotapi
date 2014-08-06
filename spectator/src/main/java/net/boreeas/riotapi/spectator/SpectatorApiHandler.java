/*
 * Copyright 2014 Malte Schütze
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

package net.boreeas.riotapi.spectator;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.RequestException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created on 4/28/2014.
 */
public class SpectatorApiHandler {
    public static final DateFormat DATE_FMT = new SimpleDateFormat("MMM dd, YYYY hh:mm:ss a");
    private static final String TOKEN = "ritopls";

    private Gson gson = new Gson();
    private WebTarget defaultTarget;
    private WebTarget consumerTarget;

    public SpectatorApiHandler(Shard region) {
        Client c = ClientBuilder.newClient();
        defaultTarget = c.target(region.spectatorUrl);
        defaultTarget = defaultTarget.path("observer-mode").path("rest");
        consumerTarget = defaultTarget.path("consumer");
    }

    /**
     * Retrieves the current version of the spectator server
     * @return the current version as a string
     */
    public String getCurrentVersion() {
        return readAsString(consumerTarget.path("version"));
    }

    /**
     * Retrieves a list of featured games from the spectator server
     * @return A FeaturedGameList, containing a list of games as well as the refresh rate
     */
    public FeaturedGameList getFeaturedGameListDto() {
        WebTarget tgt = defaultTarget.path("featured");
        return gson.fromJson($(tgt), FeaturedGameList.class);
    }

    /**
     * Retrieves a list of featured games from the spectator server
     * @return A list of games
     */
    public List<FeaturedGame> getFeaturedGames() {
        return getFeaturedGameListDto().getGameList();
    }

    public GameMetaData getGameMetaData(Platform platform, long gameId) {
        WebTarget tgt = consumerTarget.path("getGameMetaData").path(platform.name).path("" + gameId).path("1").path(TOKEN);
        return gson.fromJson($(tgt), GameMetaData.class);
    }

    public ChunkInfo getLastChunkInfo(Platform platform, long gameId) {
        WebTarget tgt = defaultTarget.path("getLastChunkInfo").path(platform.name).path("" + gameId).path("1").path(TOKEN);
        return gson.fromJson($(tgt), ChunkInfo.class);
    }

    /**
     * Returns an encrypted and compressed chunk
     * @param platform The target platform
     * @param gameId The target game
     * @param chunkId The target chunk
     * @return The chunk, encrypted and zip-compressed
     */
    public byte[] getChunk(Platform platform, long gameId, int chunkId) {
        WebTarget tgt = consumerTarget.path("getGameDataChunk").path(platform.name).path(gameId + "/" + chunkId).path(TOKEN);
        return readAsByteArray(tgt);
    }

    /**
     * Returns an encrypted and compressed keyframe
     * @param platform The target platform
     * @param gameId The target game
     * @param keyframeId The target chunk
     * @return The chunk, encrypted and zip-compressed
     */
    public String getKeyframe(Platform platform, long gameId, int keyframeId) {
        WebTarget tgt = consumerTarget.path("getKeyFrame").path(platform.name).path(gameId + "/" + keyframeId).path(TOKEN);
        return readAsString(tgt);
    }


    /**
     * Open the request to the web target and returns an InputStreamReader for the message body
     * @param target the web target to access
     * @return the reader for the message body
     */
    private InputStreamReader $(WebTarget target) {

        Response response = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatus() != 200) {
            throw new RequestException(response.getStatus(), RequestException.ErrorType.getByCode(response.getStatus()));
        }

        return new InputStreamReader(getInputStream(response));
    }

    private InputStream getInputStream(Response response) {
        return (java.io.InputStream) response.getEntity();
    }

    @SneakyThrows
    private String readAsString(WebTarget tgt) {
        return new String(new BufferedReader($(tgt)).readLine());
    }

    private byte[] readAsByteArray(WebTarget tgt) {
        try (InputStream in = getInputStream(tgt.request().accept(MediaType.APPLICATION_OCTET_STREAM_TYPE).get())) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();


            byte[] buffer = new byte[1024];
            int read;
            do {
                read = in.read(buffer);
                out.write(buffer);
            } while (read == buffer.length);

            return out.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}