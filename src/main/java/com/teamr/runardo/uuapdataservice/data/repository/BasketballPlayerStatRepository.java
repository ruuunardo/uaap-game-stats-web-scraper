package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.BasketballPlayerStat;
import com.teamr.runardo.uuapdataservice.data.entity.CompositeStatId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketballPlayerStatRepository extends JpaRepository<BasketballPlayerStat, CompositeStatId>{

//    @Query(
//            value = "SELECT b from BasketballPlayerStat b WHERE b.gameResult=:gameId"
//    )
//    Optional<List<PlayerStat>> findAllByGameResult(@Param("gameId") String gameId);
}
