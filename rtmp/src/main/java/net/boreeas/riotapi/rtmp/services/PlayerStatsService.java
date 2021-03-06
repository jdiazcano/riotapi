/*
 * Copyright 2014 The LolDevs team (https://github.com/loldevs)
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

package net.boreeas.riotapi.rtmp.services;

import lombok.AllArgsConstructor;
import net.boreeas.riotapi.com.riotgames.platform.statistics.*;
import net.boreeas.riotapi.com.riotgames.platform.statistics.team.TeamAggregatedStats;
import net.boreeas.riotapi.com.riotgames.platform.summoner.SummonerSkillLevel;
import net.boreeas.riotapi.com.riotgames.team.TeamId;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.constants.Season;
import net.boreeas.riotapi.rtmp.RtmpClient;

import java.util.List;

/**
 * Created by malte on 7/18/2014.
 */
@AllArgsConstructor
public class PlayerStatsService {
    private static final String SERVICE = "playerStatsService";
    private RtmpClient client;

    public Object processEloQuestionaire(SummonerSkillLevel skill) {
        return client.sendRpcAndWait(SERVICE, "processEloQuestionaire", skill);
    }

    public PlayerLifetimeStats retrievePlayerStatsByAccountId(double accountId, Season season) {
        return client.sendRpcAndWait(SERVICE, "retrievePlayerStatsByAccountId", accountId, season);
    }

    public List<ChampionStatInfo> retrieveTopPlayedChampions(double accId, GameMode mode) {
        return client.sendRpcAndWait(SERVICE, "retrieveTopPlayedChampions", accId, mode);
    }

    public AggregatedStats getAggregatedStats(double id, GameMode gameMode, Season season) {
        return client.sendRpcAndWait(SERVICE, "getAggregatedStats", id, gameMode, season.numeric);
    }

    public RecentGames getRecentGames(double accId) {
        return client.sendRpcAndWait(SERVICE, "getRecentGames", accId);
    }

    public List<TeamAggregatedStats> getTeamAggregatedStats(TeamId teamId) {
        return client.sendRpcAndWait(SERVICE, "getTeamAggregatedStats", teamId);
    }

    public EndOfGameStats getTeamEndOfGameStats(TeamId teamId, double gameId) {
        return client.sendRpcAndWait(SERVICE, "getTeamEndOfGameStats", teamId, gameId);
    }
}