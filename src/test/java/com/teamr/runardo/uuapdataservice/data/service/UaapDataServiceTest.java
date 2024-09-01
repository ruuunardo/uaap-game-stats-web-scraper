package com.teamr.runardo.uuapdataservice.data.service;

import com.teamr.runardo.uuapdataservice.data.repository.UaapSeasonRepository;
import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UaapDataServiceTest {

    private UaapSeasonRepository uaapDataDAO;
    private UaapDataService uaapDataService;

//    @BeforeEach
//    void setup() {
//        uaapDataDAO = Mockito.mock(UaapDataDAO.class);
//        uaapDataService = new UaapDataService(uaapDataDAO);
//    }

    @Test
    void findAllUaapSeason() {
        UaapSeason uaapSeason = new UaapSeason();

//        when(uaapDataDAO.findUaapSeasonById(anyInt())).thenReturn(null);
//        UaapSeason returnedSeason = this.uaapDataService.findUaapSeasonById(1);
//
//        assertNull(returnedSeason);
//        verify(this.uaapDataDAO).findUaapSeasonById(1);
    }
}