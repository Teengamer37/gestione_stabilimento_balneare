package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.layout.Zone;
import com.example.s_balneare.infrastructure.persistence.jdbc.common.JdbcTransactionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Beach tramite
 * libreria JDBC.<br>
 * Detiene anche riferimenti alle varie DAO del package attuale per dividere ancora meglio le operazioni.<br>
 * Nella maggior parte dei casi, questa Repository orchestra le DAO per l'interazione tra oggetti e Database.
 *
 * @see BeachRepository BeachRepository
 * @see JdbcBeachInventoryDao JdbcBeachInventoryDao
 * @see JdbcBeachServicesDao JdbcBeachServicesDao
 * @see JdbcParkingDao JdbcParkingDao
 * @see JdbcZoneDao JdbcZoneDao
 * @see JdbcSeasonDao JdbcSeasonDao
 * @see JdbcSpotDao JdbcSpotDao
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class JdbcBeachRepository implements BeachRepository {
    private final JdbcBeachInventoryDao inventoryDao;
    private final JdbcBeachServicesDao servicesDao;
    private final JdbcParkingDao parkingDao;
    private final JdbcZoneDao zoneDao;
    private final JdbcSeasonDao seasonDao;
    private final JdbcSpotDao spotDao;

    public JdbcBeachRepository() {
        this.inventoryDao = new JdbcBeachInventoryDao();
        this.servicesDao = new JdbcBeachServicesDao();
        this.parkingDao = new JdbcParkingDao();
        this.zoneDao = new JdbcZoneDao();
        this.seasonDao = new JdbcSeasonDao(zoneDao);
        this.spotDao = new JdbcSpotDao(zoneDao);
    }

    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext)
     * e lo converte di nuovo nella classe concreta per estrarre java.sql.Connection.
     *
     * @param context Token connessione
     * @return oggetto java.sql.Connection implementato in JDBC
     * @throws IllegalArgumentException se il token non è di tipo JdbcTransactionContext (quindi non rispecchia il JDBC)
     */
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext(Connection connection))) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return connection;
    }

    /**
     * Inserimento nuova spiaggia nel DB.
     *
     * @param beach   Nuova spiaggia da aggiungere
     * @param context Connessione JDBC
     * @return ID generato dal Database
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     * @throws IllegalArgumentException se il beach non ha parametri corretti
     * @throws SQLException             se ci sono problemi col Database
     */
    @Override
    public Integer save(Beach beach, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sqlBeach = "INSERT INTO beaches (name, description, phoneNumber, addressId, extraInfo, active, closed, ownerId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Integer newId;

            //passo 1: inserisco in beaches gli oggetti Beach e BeachGeneral
            try (PreparedStatement statement = connection.prepareStatement(sqlBeach, Statement.RETURN_GENERATED_KEYS)) {
                BeachGeneral general = beach.getBeachGeneral();
                if (general == null)
                    throw new IllegalArgumentException("ERROR: BeachGeneral cannot be null when saving a beach");

                statement.setString(1, general.name());
                statement.setString(2, general.description());
                statement.setString(3, general.phoneNumber());
                statement.setInt(4, beach.getAddressId());
                statement.setString(5, beach.getExtraInfo());
                statement.setBoolean(6, beach.isActive());
                statement.setBoolean(7, beach.isClosed());
                statement.setInt(8, beach.getOwnerId());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) newId = rs.getInt(1);
                    else throw new SQLException("ERROR: SQL FAILED, no ID generated for beach");
                }
            }

            //passo 2: inserisco in beach_inventories l'oggetto BeachInventory (se presente)
            if (beach.getBeachInventory() != null) {
                inventoryDao.upsert(newId, beach.getBeachInventory(), connection);
            }

            //passo 3: inserisco in beach_services l'oggetto ManageBeachService (se presente)
            if (beach.getBeachServices() != null) {
                servicesDao.upsert(newId, beach.getBeachServices(), connection);
            }

            //passo 4: inserisco in parkings l'oggetto Parking (se presente)
            if (beach.getParking() != null) {
                parkingDao.upsert(newId, beach.getParking(), connection);
            }

            //passo 5: inserisco in pricings, seasons, zones e zone_tariffs la lista di Seasons
            if (beach.getSeasons() != null) {
                seasonDao.syncSeasons(newId, beach.getSeasons(), connection);
            }

            //passo 6: inserisco in spots gli Spot di ciascuna Zone che fa parte di Beach
            spotDao.syncZones(newId, beach.getZones(), connection);

            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save beach", e);
        }
    }

    /**
     * Aggiorno spiaggia presente nel DB.
     *
     * @param beach   Oggetto da aggiornare (stesso ID, attributi diversi)
     * @param context Connessione JDBC
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     * @throws IllegalArgumentException se il beach non ha parametri corretti
     */
    @Override
    public void update(Beach beach, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (beach.getId() == null || beach.getId() <= 0)
            throw new IllegalArgumentException("ERROR: beach must have a valid ID");

        String sqlBeach = "UPDATE beaches SET name = ?, description = ?, phoneNumber = ?, addressId = ?, extraInfo = ?, active = ?, closed = ?, ownerId = ? WHERE id = ?";

        try {
            //passo 1: aggiorno dati generali da Beach e BeachGeneral
            try (PreparedStatement statement = connection.prepareStatement(sqlBeach)) {
                BeachGeneral general = beach.getBeachGeneral();
                if (general == null)
                    throw new IllegalArgumentException("ERROR: BeachGeneral cannot be null when updating a beach");

                statement.setString(1, general.name());
                statement.setString(2, general.description());
                statement.setString(3, general.phoneNumber());
                statement.setInt(4, beach.getAddressId());
                statement.setString(5, beach.getExtraInfo());
                statement.setBoolean(6, beach.isActive());
                statement.setBoolean(7, beach.isClosed());
                statement.setInt(8, beach.getOwnerId());
                statement.setInt(9, beach.getId());
                statement.executeUpdate();
            }

            //passo 2: aggiorno/inserisco dati nuovi su BeachInventory (o elimino dati)
            //dalla tabella beach_inventories
            if (beach.getBeachInventory() != null) {
                inventoryDao.upsert(beach.getId(), beach.getBeachInventory(), connection);
            } else {
                inventoryDao.delete(beach.getId(), connection);
            }

            //passo 3: aggiorno/inserisco dati nuovi su ManageBeachService (o elimino dati)
            //dalla tabella beach_services
            if (beach.getBeachServices() != null) {
                servicesDao.upsert(beach.getId(), beach.getBeachServices(), connection);
            } else {
                servicesDao.delete(beach.getId(), connection);
            }

            //passo 4: aggiorno/inserisco dati nuovi su Parking (o elimino dati)
            //dalla tabella parkings
            if (beach.getParking() != null) {
                parkingDao.upsert(beach.getId(), beach.getParking(), connection);
            } else {
                parkingDao.delete(beach.getId(), connection);
            }

            //passo 5: aggiorno/inserisco dati nuovi su Season, Pricing, Zone e ZoneTariff (o elimino dati)
            if (!beach.getSeasons().isEmpty()) {
                seasonDao.syncSeasons(beach.getId(), beach.getSeasons(), connection);
            } else {
                seasonDao.deleteAllSeasons(beach.getId(), connection);
            }

            //passo 6: aggiorno/inserisco dati nuovi dei vari Spot sulle Zone (o elimino dati)
            if (!beach.getZones().isEmpty()) {
                spotDao.syncZones(beach.getId(), beach.getZones(), connection);
            } else {
                spotDao.deleteAllZones(beach.getId(), connection);
            }

            //passo 7: cancello le Zone che non hanno né Spot né ZoneTariff connesse
            zoneDao.deleteOrphanedZones(beach.getId(), connection);
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update beach", e);
        }
    }

    /**
     * Permette di eliminare una spiaggia.
     *
     * @param id      ID della spiaggia da eliminare
     * @param context Connessione JDBC
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     * @throws IllegalArgumentException se l'ID non è valido
     */
    @Override
    public void delete(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        String sqlBeach = "DELETE FROM beaches WHERE id = ?";

        //apro connessione
        try {
            //cancello riga da beach_services
            servicesDao.delete(id, connection);
            //cancello riga da beach_inventories
            inventoryDao.delete(id, connection);
            //cancello riga da parkings
            parkingDao.delete(id, connection);
            //elimino la spiaggia
            try (PreparedStatement st = connection.prepareStatement(sqlBeach)) {
                st.setInt(1, id);
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete beach", e);
        }
    }

    /**
     * Stampa TUTTE le spiagge senza inventario né stagioni (nel caso possa servire nella ricerca delle stesse).
     *
     * @param context Connessione JDBC
     * @return lista di spiagge trovate nel Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Beach> findAll(TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "SELECT b.*, " +
                "bs.bathrooms, bs.showers, bs.pool, bs.bar, bs.restaurant, bs.wifi, bs.volleyballField, " +
                "p.nAutoPark, p.nMotoPark, p.nElectricPark, p.CCTV " +
                "FROM beaches b " +
                "LEFT JOIN beach_services bs ON b.id = bs.beachId " +
                "LEFT JOIN parkings p ON b.id = p.beachId " +
                "WHERE b.active = TRUE";
        List<Beach> beaches = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    //passo 1: ricostruisco BeachGeneral
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String telephoneNumber = rs.getString("phoneNumber");

                    BeachGeneral general = new BeachGeneral(name, description, telephoneNumber);

                    //passo 2: ricostruisco ManageBeachService
                    BeachServices services = null;
                    Boolean bathrooms = (Boolean) rs.getObject("bathrooms");
                    if (bathrooms != null) {
                        services = new BeachServices(
                                bathrooms,
                                rs.getBoolean("showers"),
                                rs.getBoolean("pool"),
                                rs.getBoolean("bar"),
                                rs.getBoolean("restaurant"),
                                rs.getBoolean("wifi"),
                                rs.getBoolean("volleyballField")
                        );
                    }

                    //passo 3: ricostruisco Parking
                    Parking parking = null;
                    Integer nAutoPark = (Integer) rs.getObject("nAutoPark");
                    if (nAutoPark != null) {
                        parking = new Parking(
                                nAutoPark,
                                rs.getInt("nMotoPark"),
                                rs.getInt("nElectricPark"),
                                rs.getBoolean("CCTV")
                        );
                    }

                    //passo 4: salvo gli altri parametri
                    int ownerId = rs.getInt("ownerId");
                    int addressId = rs.getInt("addressId");
                    if (rs.wasNull()) ownerId = 0;
                    String extraInfo = rs.getString("extraInfo");
                    boolean active = rs.getBoolean("active");
                    boolean closed = rs.getBoolean("closed");

                    //passo 5: aggiungo alla lista
                    beaches.add(new Beach(id, ownerId, addressId, general, null, services, parking, extraInfo, null, null, active, closed));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find beaches", e);
        }
        return beaches;
    }

    /**
     * Cerca una spiaggia per ID e la restituisce.
     *
     * @param id      ID della spiaggia da cercare
     * @param context Connessione JDBC
     * @return oggetto Optional dal quale, se la spiaggia è stata trovata, si può estrarre Beach
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     * @throws IllegalArgumentException se l'ID inserito non è valido
     */
    @Override
    public Optional<Beach> findById(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        String sql = "SELECT b.*, " +
                "bi.countOmbrelloni, bi.countTende, bi.countExtraSdraio, bi.countExtraLettini, bi.countExtraSedie, bi.countCamerini, " +
                "bs.bathrooms, bs.showers, bs.pool, bs.bar, bs.restaurant, bs.wifi, bs.volleyballField, " +
                "p.nAutoPark, p.nMotoPark, p.nElectricPark, p.CCTV " +
                "FROM beaches b " +
                "LEFT JOIN beach_inventories bi ON b.id = bi.beachId " +
                "LEFT JOIN beach_services bs ON b.id = bs.beachId " +
                "LEFT JOIN parkings p ON b.id = p.beachId " +
                "WHERE b.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                //no ID = fine funzione
                if (!rs.next()) return Optional.empty();

                //passo 1: ricostruisco BeachGeneral
                String name = rs.getString("name");
                String description = rs.getString("description");
                String telephoneNumber = rs.getString("phoneNumber");

                BeachGeneral general = new BeachGeneral(name, description, telephoneNumber);

                //passo 2: ricostruisco BeachInventory (gestisco anche se non inizializzato)
                BeachInventory inventory = null;
                Integer countOmbrelloni = (Integer) rs.getObject("countOmbrelloni");
                if (countOmbrelloni != null) {
                    inventory = new BeachInventory(
                            countOmbrelloni,
                            rs.getInt("countTende"),
                            rs.getInt("countExtraSdraio"),
                            rs.getInt("countExtraLettini"),
                            rs.getInt("countExtraSedie"),
                            rs.getInt("countCamerini")
                    );
                }

                //passo 3: ricostruisco ManageBeachService (gestisco anche se non inizializzato)
                BeachServices services = null;
                Boolean bathrooms = (Boolean) rs.getObject("bathrooms");
                if (bathrooms != null) {
                    services = new BeachServices(
                            bathrooms,
                            rs.getBoolean("showers"),
                            rs.getBoolean("pool"),
                            rs.getBoolean("bar"),
                            rs.getBoolean("restaurant"),
                            rs.getBoolean("wifi"),
                            rs.getBoolean("volleyballField")
                    );
                }

                //passo 4: ricostruisco Parking (gestisco anche se non inizializzato)
                Parking parking = null;
                Integer nAutoPark = (Integer) rs.getObject("nAutoPark");
                if (nAutoPark != null) {
                    parking = new Parking(
                            nAutoPark,
                            rs.getInt("nMotoPark"),
                            rs.getInt("nElectricPark"),
                            rs.getBoolean("CCTV")
                    );
                }

                //passo 5: recupero le Zone e le Season associate alla Beach
                List<Zone> zones = spotDao.findZonesByBeachId(id, connection);
                List<Season> seasons = seasonDao.findSeasonsByBeachId(id, connection);

                //passo 6: salvo gli altri parametri
                int ownerId = rs.getInt("ownerId");
                int addressId = rs.getInt("addressId");
                if (rs.wasNull()) ownerId = 0;
                String extraInfo = rs.getString("extraInfo");
                boolean active = rs.getBoolean("active");
                boolean closed = rs.getBoolean("closed");

                //passo 7: creo oggetto Beach e faccio il return
                Beach beach = new Beach(id, ownerId, addressId, general, inventory, services, parking, extraInfo, seasons, zones, active, closed);
                return Optional.of(beach);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find beach", e);
        }
    }

    /**
     * Cerca tutte le stagioni associate ad una spiaggia.
     *
     * @param beachId ID della spiaggia da cercare per le stagioni
     * @param context Connessione JDBC
     * @return una lista di stagioni che si riferiscono alla spiaggia
     * @throws IllegalArgumentException se l'ID passato non è valido
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public List<Season> findBeachSeasons(Integer beachId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        if (beachId == null || beachId <= 0) {
            throw new IllegalArgumentException("ERROR: beachId not valid");
        }

        try {
            //delegato alla funzione presente in JdbcSeasonDao
            return seasonDao.findSeasonsByBeachId(beachId, connection);
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find seasons for beach " + beachId, e);
        }
    }

    /**
     * Cerca spiaggia per ID del proprietario.
     *
     * @param ownerId ID del proprietario
     * @param context Connessione JDBC
     * @return oggetto Optional dal quale, se la spiaggia è stata trovata, si può estrarre Beach
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     * @throws IllegalArgumentException se l'ID inserito non è valido
     */
    @Override
    public Optional<Beach> findByOwnerId(Integer ownerId, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        if (ownerId == null || ownerId <= 0) throw new IllegalArgumentException("ERROR: ownerId not valid");

        //prendo l'ID della Beach associata all'Owner
        String sql = "SELECT id FROM beaches WHERE ownerId = ?";
        Integer beachId = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    beachId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find beach id by owner", e);
        }

        //se trovato, allora uso findById per ricavare completamente la Beach
        if (beachId != null) {
            return findById(beachId, context);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Aggiorna la colonna active di una spiaggia specifica.
     *
     * @param beachId ID della spiaggia
     * @param context Connessione JDBC
     * @param active  Nuovo stato della spiaggia
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     * @throws IllegalArgumentException se l'ID non è valido
     */
    @Override
    public void updateStatus(Integer beachId, boolean active, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");

        String sql = "UPDATE beaches SET active = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setInt(2, beachId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update beach status", e);
        }
    }

    /**
     * Chiama metodo su JdbcSpotDao per controllare se una list di Spot appartengono alla spiaggia.
     *
     * @param beachId ID della spiaggia
     * @param spotIds Lista di ID dei Spot da controllare
     * @param context Connessione JDBC
     * @return risultato di JdbcSpotDao.doSpotsBelongToBeach()
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     * @throws IllegalArgumentException se i parametri passati non sono validi
     * @see JdbcSpotDao#doSpotsBelongToBeach(Integer, List, Connection) JdbcSpotDao.doSpotsBelongToBeach()
     */
    @Override
    public boolean doSpotsBelongToBeach(Integer beachId, List<Integer> spotIds, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità parametri
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");
        if (spotIds == null || spotIds.isEmpty()) return false;

        try {
            return spotDao.doSpotsBelongToBeach(beachId, spotIds, connection);
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to verify spot ownership", e);
        }
    }

    /**
     * Rinomina una zona della spiaggia.
     *
     * @param beachId     ID della spiaggia
     * @param oldZoneName Nome della zona da rinominare
     * @param newZoneName Nuovo nome da applicare
     * @param context     Connessione JDBC
     * @throws IllegalArgumentException se i parametri passati non sono validi
     * @throws RuntimeException         se ci sono problemi di connessione col Database
     */
    @Override
    public void renameZone(Integer beachId, String oldZoneName, String newZoneName, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità parametri
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: invalid beachId");
        if (oldZoneName == null || oldZoneName.isEmpty() || newZoneName == null || newZoneName.isEmpty())
            throw new IllegalArgumentException("ERROR: invalid old and/or new zone name");

        try {
            zoneDao.renameZone(beachId, oldZoneName, newZoneName, connection);
        } catch (SQLException e) {
            //SQLState "23000" indica errore di integrità referenziale
            if ("23000".equals(e.getSQLState())) {
                throw new IllegalArgumentException("ERROR: zone name already exists");
            }
            throw new RuntimeException("ERROR: unable to rename zone", e);
        }
    }
}