package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.PlayerStat;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
    public Optional<List<PlayerStat>> findAllByGameResult(String gameId, String gameCode) {
        if (gameCode.endsWith("BB")) {
            TypedQuery<PlayerStat> query = entityManager.createQuery("select b from BasketballPlayerStat b where b.gameResult=:gameId", PlayerStat.class);
            query.setParameter("gameId", gameId);

            return Optional.ofNullable(query.getResultList());
        } else if (gameCode.endsWith("VB")) {
            TypedQuery<PlayerStat> query = entityManager.createQuery("select b from VolleyballPlayerStat b where b.gameResult=:gameId", PlayerStat.class);
            query.setParameter("gameId", gameId);

            return Optional.ofNullable(query.getResultList());
        }

        return Optional.empty();
    }

    @Override
    public PlayerStat save(PlayerStat playerStat) {
//        if (playerStat instanceof BasketballPlayerStat) {
        return entityManager.merge(playerStat);
//        }
    }

    @PostConstruct
    public void postConstruct() {
        Objects.requireNonNull(entityManager);
    }
}
