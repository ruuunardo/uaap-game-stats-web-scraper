CREATE TABLE `uaap_game_codes` (
  `game_code` varchar(50) NOT NULL,
  `game_name` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`game_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

CREATE TABLE `uaap_seasons` (
  `id` int NOT NULL AUTO_INCREMENT,
  `game_code` varchar(20) NOT NULL,
  `season_number` int NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `is_url_working` tinyint(1) DEFAULT NULL,
  CONSTRAINT `fk_game_code` FOREIGN KEY (`game_code`) REFERENCES `uaap_game_codes` (`game_code`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

CREATE TABLE `uaap_games` (
  `id` int NOT NULL AUTO_INCREMENT,
  `game_number` int DEFAULT NULL,
  `game_sched` datetime DEFAULT NULL,
  `game_venue` varchar(266) DEFAULT NULL,
  `season_id` int,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_season` FOREIGN KEY (`season_id`) REFERENCES `uaap_seasons` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

CREATE TABLE `uaap_game_results` (
  `id` varchar(20) NOT NULL,
  `game_id` int DEFAULT NULL,
  `univ_id` int DEFAULT NULL,
  `team_tag` varchar(10) DEFAULT NULL,
  `final_score` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_game_id` (`game_id`),
  KEY `fk_uaap_school` (`univ_id`),
  CONSTRAINT `fk_game_id` FOREIGN KEY (`game_id`) REFERENCES `uaap_games` (`id`),
  CONSTRAINT `fk_uaap_school` FOREIGN KEY (`univ_id`) REFERENCES `uaap_univ` (`id`),
  CONSTRAINT `uaap_game_results_chk_1` CHECK ((`team_tag` in (_utf8mb4'HOME',_utf8mb4'AWAY')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

CREATE TABLE `uaap_univ` (
  `id` int NOT NULL AUTO_INCREMENT,
  `univ_code` varchar(20) DEFAULT NULL,
  `name_univ` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

CREATE TABLE `uaap_players` (
  `id` varchar(20) NOT NULL,    -- based on season number, gamecode, team, player number (e.g. 87-MBB-UP-20)
  `name` varchar(128) DEFAULT NULL,
  `univ_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_univ` (`univ_id`),
  CONSTRAINT `fk_univ` FOREIGN KEY (`univ_id`) REFERENCES `uaap_univ` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

CREATE TABLE `basketball_player_stats` (
  `game_result_id` varchar(20) NOT NULL,
  `player_id` varchar(20) NOT NULL,
  `min_played` time DEFAULT NULL,
  `field_goal_attempt` int DEFAULT NULL,
  `field_goal_made` int DEFAULT NULL,
  `two_points_attempt` int DEFAULT NULL,
  `two_points_made` int DEFAULT NULL,
  `three_points_attempt` int DEFAULT NULL,
  `three_points_made` int DEFAULT NULL,
  `free_throw_attempt` int DEFAULT NULL,
  `free_throw_made` int DEFAULT NULL,
  `rebound_or` int DEFAULT NULL,
  `rebound_dr` int DEFAULT NULL,
  `assist` int DEFAULT NULL,
  `turn_over` int DEFAULT NULL,
  `steal` int DEFAULT NULL,
  `block` int DEFAULT NULL,
  `foul_pf` int DEFAULT NULL,
  `foul_fd` int DEFAULT NULL,
  `efficiency` int DEFAULT NULL,
  `points` int DEFAULT NULL,
  `is_first_five` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`game_result_id`,`player_id`),
  KEY `uaap_players_ibfk_1` (`player_id`),
  CONSTRAINT `basketball_player_stats_ibfk_2` FOREIGN KEY (`game_result_id`) REFERENCES `uaap_game_results` (`id`),
  CONSTRAINT `uaap_players_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `uaap_players` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

CREATE TABLE `users` (
  `username` varchar(256) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `active` boolean DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

CREATE TABLE `roles` (
  `username` varchar(256) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  UNIQUE KEY `username` (`username`,`role`),
  CONSTRAINT `fk_user` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;