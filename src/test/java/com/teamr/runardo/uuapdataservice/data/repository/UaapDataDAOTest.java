//package com.teamr.runardo.uuapdataservice.data.dao;
//
//import com.teamr.runardo.uuapdataservice.data.entity.UaapGameCode;
//import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
//import com.teamr.runardo.uuapdataservice.data.service.UaapDataService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
////@DataJpaTest
////@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@SpringBootTest(classes = UaapDataService.class)
//class UaapDataDAOTest {
//
//    @Autowired
//    UaapSeasonRepository uaapSeasonRepository;
//
//    @Autowired
//    UaapGameCodeRepository uaapGameCodeRepository;
//
//
//    @Test
//    void injectedComponentNotNull() {
//        UaapSeason uaapSeason = new UaapSeason();
//        uaapSeason.setSeasonNumber(86);
//        uaapSeason.setUrl("test.url");
//
//        Optional<UaapSeason> byId = uaapSeasonRepository.findById(1);
//        System.out.println(byId.get().getUaapGames().size());
//    }
//
//    @Test
//    void addUaapSeason() {
//        UaapGameCode gameCode = new UaapGameCode("Test1", "Test1");
//        UaapSeason uaapSeason = UaapSeason.builder()
//                .url("test.url")
//                .seasonNumber(99)
//                .isUrlWorking(true)
//                .gameCode(gameCode)
//                .build();
//
//        UaapSeason savedGame = uaapSeasonRepository.customSaveGame(uaapSeason);
//
////        UaapSeason savedGame = uaapSeasonRepository.save(uaapSeason);
//        uaapSeason.setId(savedGame.getId());
//        System.out.println(savedGame.getGameCode());
////
//        System.out.println(uaapGameCodeRepository.findById("Test1"));
//        assertEquals(savedGame,uaapSeason);
//
//    }
//
//}