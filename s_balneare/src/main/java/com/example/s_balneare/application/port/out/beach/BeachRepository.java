package com.example.s_balneare.application.port.out.beach;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.Season;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcBeachRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database su oggetti di tipo Beach.<br>
 * Implementata in JdbcBeachRepository.
 *
 * @see JdbcBeachRepository JdbcBeachRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public interface BeachRepository {
    //manipolazione
    Integer save(Beach beach, TransactionContext context);
    void update(Beach beach, TransactionContext context);
    void delete(Integer id, TransactionContext context);

    //cerca spiaggia per ID
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

    //rinomina una zona della spiaggia
    void renameZone(Integer beachId, String oldZoneName, String newZoneName, TransactionContext context);
}