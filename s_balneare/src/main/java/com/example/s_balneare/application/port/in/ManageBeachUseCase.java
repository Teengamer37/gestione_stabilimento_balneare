package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.layout.Zone;

import java.util.List;
import java.util.Optional;

public interface ManageBeachUseCase {
    //updates a Beach
    void updateGeneralInfo(Integer id, BeachGeneral newGeneral);
    void updateInventory(Integer id, BeachInventory newInventory);
    void updateServices(Integer id, BeachServices newServices);
    void updateParking(Integer id, Parking newParking);
    void updateExtraInfo(Integer id, String extraInfo);

    //update active di Beach
    void setBeachActive(Integer id, boolean active);

    //elementi Season (solo con add, business logic)
    void addSeason(Integer id, Season season);
    void addSeasons(Integer id, List<Season> seasons);

    //elementi Zone
    void addZone(Integer id, Zone zone);
    void addZones(Integer id, List<Zone> zones);
    void removeZone(Integer id, Zone zoneName);
    void removeZones(Integer id, List<Zone> zones);

    //letture
    Beach getBeach(Integer id);
    Optional<Beach> getOwnerBeach(Integer ownerId);
}
