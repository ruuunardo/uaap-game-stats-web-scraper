package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {
}
