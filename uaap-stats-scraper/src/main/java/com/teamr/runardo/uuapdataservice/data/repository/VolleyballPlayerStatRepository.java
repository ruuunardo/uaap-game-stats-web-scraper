package com.teamr.runardo.uuapdataservice.data.repository;

import com.teamr.runardo.uuapdataservice.data.entity.CompositeStatId;
import com.teamr.runardo.uuapdataservice.data.entity.VolleyballPlayerStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolleyballPlayerStatRepository extends JpaRepository<VolleyballPlayerStat, CompositeStatId> {

}
