package com.example.s_balneare.application.port.in.beach;

import com.example.s_balneare.application.service.beach.ManageBeachService;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.layout.Zone;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare un oggetto di tipo Beach.<br>
 * Implementata in:
 *
 * @see ManageBeachService ManageBeachService
 */
public interface ManageBeachUseCase {
    //update di Beach
    void updateGeneralInfo(Integer id, BeachGeneral newGeneral);
    void updateInventory(Integer id, BeachInventory newInventory);
    void updateServices(Integer id, BeachServices newServices);
    void updateParking(Integer id, Parking newParking);
    void updateExtraInfo(Integer id, String extraInfo);

    //update attributo active di Beach
    void setBeachActive(Integer id, boolean active);

    //elementi Season
    void addSeason(Integer id, Season season);
    void addSeasons(Integer id, List<Season> seasons);
    void updateSeasonEndDate(Integer beachId, String seasonName, LocalDate newEndDate);
    void removeSeason(Integer beachId, String seasonName);

    //elementi Zone
    void addZone(Integer id, Zone zone);
    void addZones(Integer id, List<Zone> zones);
    void renameZone(Integer beachId, String oldZoneName, String newZoneName);
    void removeZone(Integer id, Zone zoneName);
    void removeZones(Integer id, List<Zone> zones);

    //letture
    Beach getBeach(Integer id);
    Optional<Beach> getOwnerBeach(Integer ownerId);
}