package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.BasketballPlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.CompositeStatId;
import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BasketballPlayerStatRepository extends JpaRepository<BasketballPlayerStat, CompositeStatId> {

    @Query(
            value = "SELECT b from BasketballPlayerStat b WHERE b.gameResult=:gameId"
    )
    Optional<List<PlayerStat>> findAllByGameResult(@Param("gameId") String gameId);
}
