package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;

import java.util.List;
import java.util.Optional;

public interface PlayerStatRepository {

    PlayerStat save(PlayerStat playerStat);

    Optional<List<PlayerStat>> findAllByGameResult(String gameId, String gameCode);

    int deleteAllStatsByGameResultId(String gameResultId, String gameCode);
}
