package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.beach.Beach;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

//interfacce per manipolazione oggetti di tipo Beach
public interface BeachRepository {
    int save(Beach beach);
    int save(Beach beach, Connection conn);
    void update(Beach beach);
    void delete(int id);
    Optional<Beach> findById(int id);

    //trova gli ID delle stagioni di una spiaggia
    //TODO: da spostare su SeasonRepository
    List<Integer> findBeachSeasonIds(int beachId);

    //trova tutte le spiagge senza inventario né stagioni (nel caso possa servire nella ricerca delle stesse)
    List<Beach> findAll();
}