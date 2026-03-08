package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.Season;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.layout.Spot;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//interfacce per manipolazione oggetti di tipo Beach
public interface BeachRepository {
    Integer save(Beach beach);
    Integer save(Beach beach, TransactionContext context);
    void update(Beach beach);
    void delete(Integer id);
    Optional<Beach> findById(Integer id);

    //trova tutte le stagioni di una spiaggia
    List<Season> findBeachSeasons(Integer beachId);

    //trova tutte le spiagge senza inventario né stagioni (nel caso possa servire nella ricerca delle stesse)
    List<Beach> findAll();

    //cerca la spiaggia tramite proprietario
    Optional<Beach> findByOwnerId(Integer ownerId);

    //aggiorna solo lo stato della spiaggia senza usare update()
    void updateStatus(Integer beachId, boolean active);
}