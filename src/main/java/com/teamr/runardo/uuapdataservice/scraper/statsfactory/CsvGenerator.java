package com.teamr.runardo.uuapdataservice.scraper.statsfactory;

import com.teamr.runardo.uuapdataservice.scraper.dto.GameResultDto;
import com.teamr.runardo.uuapdataservice.scraper.dto.UaapGameDto;
import com.teamr.runardo.uuapdataservice.data.entity.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CsvGenerator {

    private final CSVPrinter printer;

    public CsvGenerator(Writer writer) throws IOException {
        this.printer = new CSVPrinter(writer, CSVFormat.EXCEL);
    }

    public void writeUaapGamesToCsv(List<UaapGameDto> uaapGames, UaapSeason uaapSeason) throws IOException {
        printer.printRecord(generateGameHeader());
        for (UaapGameDto gr : uaapGames) {
            generateGameData(gr, uaapSeason);
        }
    }


    private List<String> generateGameHeader() {
        return List.of("SEASON_NUM", "GAME_CODE", "GAME_NUM", "TEAM_TAG", "TEAM_NAME", "SCORE", "GAME_RESULT_ID", "OPPONENT", "OPPONENT_TEAM_SCORE");
    }

    private void generateGameData(UaapGameDto game, UaapSeason uaapSeason) throws IOException {
        int i = 0;
        for (GameResultDto gameResult : game.getGameResults()) {
            GameResultDto oppGameResult = i == 0 ? game.getGameResults().get(1) : game.getGameResults().get(0);
            printer.printRecord(uaapSeason.getSeasonNumber(), uaapSeason.getGameCode().getGameCode(), game.getGameNumber(), gameResult.getTeamTag(), gameResult.getUniv().getUnivCode(), gameResult.getFinalScore(), gameResult.getId(), oppGameResult.getUniv().getUnivCode(), oppGameResult.getFinalScore());
            ++i;
        }
    }

//    ---------------------------------------------------------------
    public void writeUaapGamesToCsv(List<UaapGameDto> uaapGames, UaapSeason season, Optional<List<Integer>> selections) throws IOException {
        String gameCode = season.getGameCode().getGameCode();
        printer.printRecord(generateStatsHeader(gameCode));
        for (UaapGameDto g : uaapGames) {
            for (GameResultDto gr : g.getGameResults()) {
                for (PlayerStat p : gr.getPlayerStats()) {
                    printer.printRecord(generateStatsData(gameCode, p));
                }
            }
        }
    }

    private List<String> generateStatsHeader(String gameCode) {
        List<String> header = new ArrayList<>();

        if (gameCode.endsWith("BB")) {
            header = List.of("GAME_RESULT_ID", "PLAYER_ID", "PLAYER_NAME"
                    , "MIN_PLAYED", "FIELD_GOAL_ATTEMPTS", "FIELD_GOAL_MADE", "TWO_POINTS_ATTEMPTS", "TWO_POINTS_MADE", "THREE_POINTS_ATTEMPTS", "THREE_POINTS_MADE", "FREE_THROW_ATTEMPTS", "FREE_THROW_MADE"
                    , "REBOUNDS_OR", "REBOUNDS_DR", "ASSIST", "TURN_OVER", "STEAL", "BLOCK", "FOULS_PF", "FOULS_FD", "EFFICIENCY", "POINTS", "IS_FIRST_FIVE");
        } else if (gameCode.endsWith("VB")) {
            header = List.of("GAME_RESULT_ID", "PLAYER_ID", "PLAYER_NAME"
                    , "ATTACK_ATTEMPT", "ATTACK_MADE", "SERVE_ATTEMPT", "SERVE_MADE", "BLOCK_ATTEMPT", "BLOCK_MADE", "DIG_ATTEMPT", "DIG_MADE", "RECEIVE_ATTEMPT", "RECEIVE_MADE", "SET_ATTEMPT", "SET_MADE");
        }
        return header;
    }

    public List<String> generateStatsData(String gameCode, PlayerStat playerStat) {
        List<String> data = new ArrayList<>();

        if (gameCode.endsWith("BB")) {
            BasketballPlayerStat stat = (BasketballPlayerStat) playerStat;
            data.add(stat.getGameResult());
            data.add(stat.getPlayer().getId());
            data.add(stat.getPlayer().getName());
            data.add(String.valueOf(stat.getPoints()));

        } else if (gameCode.endsWith("VB")) {
            VolleyballPlayerStat stat = (VolleyballPlayerStat) playerStat;
            data.add(stat.getGameResult());
            data.add(stat.getPlayer().getId());
            data.add(stat.getPlayer().getName());
            data.add(String.valueOf(stat.getAttackMade()));
            data.add(String.valueOf(stat.getAttackAttempt()));
        }
        return data;
    }


}
