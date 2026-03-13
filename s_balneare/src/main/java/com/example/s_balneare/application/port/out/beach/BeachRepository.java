package com.example.s_balneare.application.port.out.beach;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.Season;
import com.example.s_balneare.domain.common.TransactionContext;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database su oggetti di tipo Beach.
 * Implementata in JdbcBeachRepository
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcBeachRepository JdbcBeachRepository
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager per le transazioni SQL
 */
//interfacce per manipolazione oggetti di tipo Beach
public interface BeachRepository {
    Integer save(Beach beach, TransactionContext context);
    void update(Beach beach, TransactionContext context);
    void delete(Integer id, TransactionContext context);
    Optional<Beach> findById(Integer id, TransactionContext context);

    //trova tutte le stagioni di una spiaggia
    List<Season> findBeachSeasons(Integer beachId, TransactionContext context);

    //trova tutte le spiagge senza inventario né stagioni (nel caso possa servire nella ricerca delle stesse)
    List<Beach> findAll(TransactionContext context);

    //cerca la spiaggia tramite proprietario
    Optional<Beach> findByOwnerId(Integer ownerId, TransactionContext context);

    //aggiorna solo lo stato della spiaggia senza usare update()
    void updateStatus(Integer beachId, boolean active, TransactionContext context);

    //verifica se gli spot appartengono alla spiaggia
    boolean doSpotsBelongToBeach(Integer beachId, List<Integer> spotIds, TransactionContext context);
}