package com.teamr.runardo.uuapdataservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UaapdataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UaapdataServiceApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner test(UaapDataService uaapDataService) {
//		return runner -> {
//			System.out.println("Hello!");
//
//			UaapSeason uaapSeason = uaapDataService.findUaapSeasonByIdSortedGames(38);
//			Optional<List<UaapGame>> uaapGameList = uaapDataService.findUaapGamesBySeasonId(uaapSeason.getId());
//			List<UaapGameDto> uaapGameDtos = uaapDataService.getUaapGameDtos(uaapGameList.get(), uaapSeason.getGameCode().getGameCode());
//			UaapSeasonDto uaapSeasonDto = UaapSeasonDto.convertToDto(uaapSeason);
//
////			File obj = new File()
//			File myObj = new File("filename.txt");
//			FileWriter myWriter = new FileWriter(myObj);
//			myWriter.write("THIS IS A FILE WRITER\n");
//			CsvGenerator csvGenerator = new CsvGenerator(myWriter);
//			csvGenerator.writeUaapGamesToCsv(uaapGameDtos, uaapSeasonDto, "this");
//			myWriter.close();
////			UaapSeason uaapSeason = new UaapSeason();
////			System.out.println(uaapSeason.getSeasonNumber());
//
//		};
//	}

}
