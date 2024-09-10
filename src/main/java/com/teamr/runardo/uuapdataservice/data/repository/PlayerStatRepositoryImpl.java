package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PlayerStatRepositoryImpl implements PlayerStatRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<List<PlayerStat>> findAllByGameResult(String gameResultId, String gameCode) {
        TypedQuery<PlayerStat> query = null;
        if (gameCode.endsWith("BB")) {
            query = entityManager.createQuery("select b from BasketballPlayerStat b where b.gameResult=:gameId", PlayerStat.class);
        } else if (gameCode.endsWith("VB")) {
            query = entityManager.createQuery("select b from VolleyballPlayerStat b where b.gameResult=:gameId", PlayerStat.class);
        } else {
            throw new RuntimeException("Game code invalid: " + gameCode);
        }
        query.setParameter("gameId", gameResultId);
        return Optional.ofNullable(query.getResultList());
    }

    @Transactional
    @Override
    public int deleteAllStatsByGameResultId(String gameResultId, String gameCode) {
        Query query;
        if (gameCode.endsWith("VB")) {
            query = entityManager.createQuery("delete from VolleyballPlayerStat s where s.gameResult=:gameId");
        } else if (gameCode.endsWith("BB")) {
            query = entityManager.createQuery("delete from BasketballPlayerStat b where b.gameResult=:gameId");
        } else {
            throw new RuntimeException("Game code invalid: " + gameCode);
        }
        query.setParameter("gameId", gameResultId);
        int i = query.executeUpdate();
        return i;
    }

    @Transactional
    @Override
    public PlayerStat save(PlayerStat playerStat) {
        return entityManager.merge(playerStat);
    }

    @PostConstruct
    public void postConstruct() {
        Objects.requireNonNull(entityManager);
    }
}
