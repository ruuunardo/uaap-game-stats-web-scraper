drop schema	 if exists uaap_data;
create schema uaap_data;

use uaap_data;

SET FOREIGN_KEY_CHECKS=0;

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

insert into users
values("ra", "{noop}123", 1)
,("re", "{noop}123", 1)
,("ru", "{noop}123", 1)
;

insert into roles
values("ra", "ROLE_USER")
,("re", "ROLE_USER")
,("ru", "ROLE_ADMIN")
,("ru", "ROLE_USER")
;


-- CREATE TABLE `volleyball_player_stats` (
--   `game_result_id` varchar(20) NOT NULL,
--   `player_id` varchar(20) NOT NULL,
--   `attack_attempt` int DEFAULT NULL,
--   `attack_made` int DEFAULT NULL,
--   `serve_attempt` int DEFAULT NULL,
--   `serve_made` int DEFAULT NULL,
--   `block_attempt` int DEFAULT NULL,
--   `block_made` int DEFAULT NULL,
--   `dig_attempt` int DEFAULT NULL,
--   `dig_made` int DEFAULT NULL,
--   `receive_attempt` int DEFAULT NULL,
--   `receive_made` int DEFAULT NULL,
--   `set_attempt` int DEFAULT NULL,
--   `set_made` int DEFAULT NULL,
--   PRIMARY KEY (`game_result_id`,`player_id`),
--   KEY `uaap_players_ibfk_2` (`player_id`),
--   CONSTRAINT `uaap_players_ibfk_2` FOREIGN KEY (`player_id`) REFERENCES `uaap_players` (`id`),
--   CONSTRAINT `volleyball_player_stats_ibfk_3` FOREIGN KEY (`game_result_id`) REFERENCES `uaap_game_results` (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
-- ;

-- insert into uaap_games(game_number, season_id)
-- select t.game_number, 1 from test.uaap_games t
-- where t.id > 100 and t.id < 106;

-- insert into uaap_game_results
-- select r.id, r.game_id, r.univ_id, r.team_tag, r.final_score from test.uaap_game_results r
-- join uaap_games g
-- on g.id = r.game_id;

-- pre-populate univ table
insert into uaap_univ(univ_code, name_univ)
values("FEU","Far Eastern University"),
("NU","National University"),
("UP","University of the Philippines"),
("UST","University of Santo Tomas"),
("ADU","Adamson University"),
("UE","University of the East"),
("ADMU","Ateneo de Manila University"),
("DLSU","De La Salle University")
;

SET FOREIGN_KEY_CHECKS=1;