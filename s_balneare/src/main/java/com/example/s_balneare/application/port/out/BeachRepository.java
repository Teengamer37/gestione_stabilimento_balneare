package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.beach.Beach;

import java.util.List;
import java.util.Optional;

//porta (interfaccia) uscente dall'applicazione per le spiagge
//(serve per poi implementare queste funzioni nella classe che userà JDBC e MySQL)
public interface BeachRepository {
    int save(Beach beach);
    void update(Beach beach);
    void delete(int id);
    Optional<Beach> findById(int id);

    //trova gli ID delle stagioni di una spiaggia
    List<Integer> findBeachSeasonIds(int beachId);

    //trova tutte le spiagge senza inventario né stagioni (nel caso possa servire nella ricerca delle stesse)
    List<Beach> findAll();
}