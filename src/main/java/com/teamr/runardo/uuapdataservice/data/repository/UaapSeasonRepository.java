package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.UaapSeason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UaapSeasonRepository extends JpaRepository<UaapSeason,Integer>, CustomUaapSeasonRepository {
}
