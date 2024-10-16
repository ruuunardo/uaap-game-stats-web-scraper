package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.UaapGame;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UaapGameRepository extends JpaRepository<UaapGame, Integer> {

    @Query(
            value = "SELECT g from UaapGame g WHERE g.seasonId=:seasonId"
    )
    Optional<List<UaapGame>> findAllBySeasonId(@Param("seasonId") int seasonNumber);

    Optional<UaapGame> findAllBySeasonIdAndGameNumber(int seasonId, int gameNumber);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "delete UaapGame g where g.gameNumber=:gameNum and g.seasonId=:seasonId"
    )
    int deleteByGameNumAndSeason(@Param("gameNum") int gameNum, @Param("seasonId") int seasonNumber);

}
