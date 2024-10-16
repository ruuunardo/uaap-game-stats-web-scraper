package com.teamr.runardo.uuapdataservice.data;

import com.teamr.runardo.uuapdataservice.data.entity.*;
import com.teamr.runardo.uuapdataservice.data.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {UaapDataApplication.class})
@TestPropertySource("/test.properties")
@Transactional
class UaapDataApplicationTest {
    @Autowired
    UaapSeasonRepository uaapSeasonRepository;

    @Autowired
    UaapGameCodeRepository uaapGameCodeRepository;

    @Autowired
    UaapGameRepository uaapGameRepository;

    @Autowired
    BasketballPlayerStatRepository basketballPlayerStatRepository;

    @Autowired
    VolleyballPlayerStatRepository volleyballPlayerStatRepository;

    @Autowired
    PlayerStatRepository playerStatRepository;

    @Autowired
    PlayerRepository playerRepository;

    private UaapSeason uaapSeason;

    private PlayerStat basketballPlayerStat;

    @BeforeEach
    void setUpTest() {
        UaapGameCode gameCode = new UaapGameCode("Men's", "MBB");
        UaapGame uaapGame = UaapGame.builder()
                .gameNumber(1)
                .gameSched(LocalDateTime.now())
                .build();

        uaapSeason = UaapSeason.builder()
                .url("test.url")
                .seasonNumber(99)
                .urlWork(true)
                .gameCode(gameCode)
                .build();

        uaapSeason.setUaapGames(List.of(uaapGame));

        Player p = new Player("T1", "Test Player1", 1);
        basketballPlayerStat = BasketballPlayerStat.builder()
                .gameResult("85-MBB-DLSU-2")
                .player(p)
                .points(21)
                .isFirstFive(1)
                .build();

    }

    @Test
    void addUaapSeason() {
        UaapSeason savedGame = uaapSeasonRepository.customSaveGame(uaapSeason);
        uaapSeason.setId(savedGame.getId());
        assertEquals(savedGame, uaapSeason);
    }

    @Test
    void deleteUaapSeasonAndGameCodeStays() {
        UaapSeason savedGame = uaapSeasonRepository.customSaveGame(uaapSeason);
        UaapGameCode uaapGameCode = savedGame.getGameCode();

        int toDeleteId = savedGame.getId();
        uaapSeasonRepository.deleteById(toDeleteId);
        Optional<UaapSeason> retrieved = uaapSeasonRepository.findById(toDeleteId);

        //deleted uaap season
        assertTrue(retrieved.isEmpty());
        //but game code stays
        assertTrue(uaapGameCodeRepository.findById(uaapGameCode.getGameCode()).isPresent());
    }

    @Test
    void deleteUaapGameBySeasonAndGameNum() {
        UaapSeason savedGame = uaapSeasonRepository.customSaveGame(uaapSeason);

        UaapGame uaapGame = uaapSeason.getUaapGames().get(0);
        uaapGame.setSeasonId(savedGame.getId());

        System.out.println(uaapGame);
        uaapGameRepository.save(uaapGame);
        System.out.println(uaapGame);

        int i = uaapGameRepository.deleteByGameNumAndSeason(uaapGame.getGameNumber(), uaapGame.getSeasonId());
        System.out.println("Delete game:" + i);
        Optional<UaapGame> gameRetrieved = uaapGameRepository.findById(uaapGame.getId());
        assertTrue(gameRetrieved.isEmpty());
    }

    @Test
    void addUaapGame() {
        UaapSeason savedGame = uaapSeasonRepository.customSaveGame(uaapSeason);

//        link to uaapSeason
        UaapGame uaapGame = uaapSeason.getUaapGames().get(0);
        uaapGame.setSeasonId(savedGame.getId());

        UaapGame game = uaapGameRepository.save(uaapGame);
        int id = game.getId();

        Optional<UaapGame> games = uaapGameRepository.findAllBySeasonIdAndGameNumber(game.getSeasonId(), game.getGameNumber());
        System.out.println(games);

        assertFalse(uaapGameRepository.findById(id).isEmpty());
    }

    @Test
    void deleteUaapGames() {
        UaapSeason savedGame = uaapSeasonRepository.customSaveGame(uaapSeason);

//        link to uaapSeason
        UaapGame uaapGame = uaapSeason.getUaapGames().get(0);
        uaapGame.setSeasonId(savedGame.getId());

        UaapGame game = uaapGameRepository.save(uaapGame);
        int id = game.getId();

        uaapGameRepository.deleteAllById(List.of(id));
        assertTrue(uaapGameRepository.findById(id).isEmpty());
    }

    @Test
    void deleteTwoUaapGames() {
        UaapSeason savedGame = uaapSeasonRepository.customSaveGame(uaapSeason);

//        link to uaapSeason
        UaapGame uaapGame = uaapSeason.getUaapGames().get(0);
        uaapGame.setSeasonId(savedGame.getId());

        UaapGame uaapGame2 = UaapGame.builder()
                .gameNumber(2)
                .gameSched(LocalDateTime.now())
                .build();

        uaapGame2.setSeasonId(savedGame.getId());

        UaapGame game = uaapGameRepository.save(uaapGame);
        UaapGame game2 = uaapGameRepository.save(uaapGame2);
        int id = game.getId();
        int id2 = game2.getId();

        uaapGameRepository.deleteAllById(List.of(id, id2));
        assertTrue(uaapGameRepository.findAllById(List.of(id, id2)).isEmpty());
    }

    @Test
    void insertBBallStat() {
        BasketballPlayerStat saved = (BasketballPlayerStat) playerStatRepository.save(basketballPlayerStat);
        assertEquals(((BasketballPlayerStat) basketballPlayerStat).getPoints(), saved.getPoints());
    }

    @Test
    void addVballStat() {
        Player player = playerRepository.findById("T1").get();
        CompositeStatId statId = new CompositeStatId(player, "85-MBB-ADU-1");

        VolleyballPlayerStat volleyballPlayerStat = VolleyballPlayerStat.builder()
                                            .gameResult("85-MBB-DLSU-2")
                                            .attackAttempt(1)
                                            .player(player)
                                            .attackMade(2)
                                            .build();

        playerStatRepository.save(volleyballPlayerStat);
        Optional<List<PlayerStat>> allByGameResult = playerStatRepository.findAllByGameResult("85-MBB-DLSU-2", "VB");

        System.out.println(allByGameResult);
        assertTrue(allByGameResult.get().get(0).getClass() == VolleyballPlayerStat.class);

        int i = playerStatRepository.deleteAllStatsByGameResultId("85-MBB-DLSU-2", "MVB");
        assertEquals(1, i);
    }

}