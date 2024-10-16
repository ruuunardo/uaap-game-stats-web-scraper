package com.teamr.runardo.uuapdataservice.data.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UaapSeasonTest {

    @Test
    void parse() {
        UaapSeason uaapSeason = UaapSeason.parse("MBB,Men's Basketball,85,https://uaap.livestats.ph/tournaments/uaap-season-85-men-s?game_id=:id");
        System.out.println(uaapSeason.getGameCode());
    }
}