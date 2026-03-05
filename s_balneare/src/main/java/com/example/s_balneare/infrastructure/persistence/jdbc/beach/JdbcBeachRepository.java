package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.application.port.out.BeachRepository;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.layout.Zone;
import com.example.s_balneare.infrastructure.persistence.jdbc.JdbcTransactionManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBeachRepository implements BeachRepository {
    private final DataSource dataSource;

    private final JdbcBeachInventoryDao inventoryDao;
    private final JdbcBeachServicesDao servicesDao;
    private final JdbcParkingDao parkingDao;

    public JdbcBeachRepository(DataSource dataSource) {
        this.dataSource = dataSource;

        this.inventoryDao = new JdbcBeachInventoryDao();
        this.servicesDao = new JdbcBeachServicesDao();
        this.parkingDao = new JdbcParkingDao();
    }

    //---- METODO HELPER ----
    //prende il token vuoto (TransactionContext)
    //lo converte di nuovo nella classe concreta per estrarre java.sql.Connection
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.getConnection();
    }

    //inserimento nuova spiaggia nel DB
    //DA USARE SE E SOLO SE SI HA UN'ISTANZA LIBERA E NON ASSOCIATA DI ADDRESS
    //SE USATA A CASO -> EXCEPTION 100%
    @Override
    public Integer save(Beach beach) {
        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                TransactionContext context = new JdbcTransactionManager.JdbcTransactionContext(connection);
                Integer beachId = save(beach, context);
                connection.commit();
                return beachId;
            } catch (SQLException e) {
                try {
                    //andata male -> ripristino allo stato iniziale
                    connection.rollback();
                } catch (SQLException e2) {
                    e.addSuppressed(e2);
                }
                throw new RuntimeException("ERROR: unable to save beach", e);
            } finally {
                try {
                    //in qualsiasi caso, rimetto autocommit a true
                    if (connection != null) connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("WARNING: unable to set autocommit to true");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    //inserimento nuova spiaggia nel DB
    @Override
    public Integer save(Beach beach, TransactionContext context) {
        //estraggo la connection JDBC
        Connection connection = getConnection(context);

        String sqlBeach = "INSERT INTO beaches (name, description, phoneNumber, addressId, extraInfo, active, ownerId) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Integer newId;

            //passo 1: inserisco in beaches gli oggetti Beach e BeachGeneral
            try (PreparedStatement statement = connection.prepareStatement(sqlBeach, Statement.RETURN_GENERATED_KEYS)) {
                BeachGeneral general = beach.getBeachGeneral();
                if (general == null) throw new IllegalArgumentException("ERROR: BeachGeneral cannot be null when saving a beach.");

                statement.setString(1, general.getName());
                statement.setString(2, general.getDescription());
                statement.setString(3, general.getPhoneNumber());
                statement.setInt(4, beach.getAddressId());
                statement.setString(5, beach.getExtraInfo());
                statement.setBoolean(6, beach.isActive());
                statement.setInt(7, beach.getOwnerId());
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

            //passo 3: inserisco in beach_services l'oggetto BeachService (se presente)
            if (beach.getBeachServices() != null) {
                servicesDao.upsert(newId, beach.getBeachServices(), connection);
            }

            //passo 4: inserisco in parkings l'oggetto Parking (se presente)
            if (beach.getParking() != null) {
                parkingDao.upsert(newId, beach.getParking(), connection);
            }

            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save beach", e);
        }
    }

    //aggiorno spiaggia presente nel DB
    @Override
    public void update(Beach beach) {
        //check validità ID
        if (beach.getId() == null || beach.getId() <= 0) throw new IllegalArgumentException("ERROR: beach must have a valid ID");

        String sqlBeach = "UPDATE beaches SET name = ?, description = ?, phoneNumber = ?, addressId = ?, extraInfo = ?, active = ?, ownerId = ? WHERE id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                //passo 1: aggiorno dati generali da Beach e BeachGeneral
                try (PreparedStatement statement = connection.prepareStatement(sqlBeach)) {
                    BeachGeneral general = beach.getBeachGeneral();
                    if (general == null) throw new IllegalArgumentException("ERROR: BeachGeneral cannot be null when updating a beach.");

                    statement.setString(1, general.getName());
                    statement.setString(2, general.getDescription());
                    statement.setString(3, general.getPhoneNumber());
                    statement.setInt(4, beach.getAddressId());
                    statement.setString(5, beach.getExtraInfo());
                    statement.setBoolean(6, beach.isActive());
                    statement.setInt(7, beach.getOwnerId());
                    statement.setInt(8, beach.getId());
                    statement.executeUpdate();
                }

                //passo 2: aggiorno/inserisco dati nuovi su BeachInventory (o elimino dati)
                //dalla tabella beach_inventories
                if (beach.getBeachInventory() != null) {
                    inventoryDao.upsert(beach.getId(), beach.getBeachInventory(), connection);
                } else {
                    inventoryDao.delete(beach.getId(), connection);
                }

                //passo 3: aggiorno/inserisco dati nuovi su BeachService (o elimino dati)
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

                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException e2) {
                    e.addSuppressed(e2);
                }
                throw new RuntimeException("ERROR: unable to update beach", e);
            } finally {
                try {
                    if (connection != null) connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("WARNING: unable to set autocommit to true");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    //permette di eliminare una spiaggia
    @Override
    public void delete(Integer id) {
        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        String sqlBeach = "DELETE FROM beaches WHERE id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

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

                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException e2) {
                    e.addSuppressed(e2);
                }
                throw new RuntimeException("ERROR: unable to delete beach", e);
            } finally {
                try {
                    if (connection != null) connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("WARNING: unable to set autocommit to true");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    //stampa TUTTE le spiagge senza inventario né stagioni (nel caso possa servire nella ricerca delle stesse)
    public List<Beach> findAll() {
        String sql = "SELECT b.*, " +
                "bs.bathrooms, bs.showers, bs.pool, bs.bar, bs.restaurant, bs.wifi, bs.volleyballField, " +
                "p.nAutoPark, p.nMotoPark, p.nBikePark, p.nElectricPark, p.CCTV " +
                "FROM beaches b " +
                "LEFT JOIN beach_services bs ON b.id = bs.beachId " +
                "LEFT JOIN parkings p ON b.id = p.beachId " +
                "WHERE b.active = TRUE";
        List<Beach> beaches = new ArrayList<>();

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        //passo 1: ricostruisco BeachGeneral
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String description = rs.getString("description");
                        String telephoneNumber = rs.getString("phoneNumber");

                        BeachGeneral general = new BeachGeneral(name, description, telephoneNumber);

                        //passo 2: ricostruisco BeachService
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
                                    rs.getInt("nBikePark"),
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

                        //passo 5: aggiungo alla lista
                        beaches.add(new Beach(id, ownerId, addressId, general, null, services, parking, extraInfo, null, null, active));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to find beaches", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
        return beaches;
    }

    //cerca una spiaggia per ID e la restituisce
    @Override
    public Optional<Beach> findById(Integer id) {
        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        String sql = "SELECT b.*, " +
                     "bi.countOmbrelloni, bi.countTende, bi.countExtraSdraio, bi.countExtraLettini, bi.countExtraSedie, bi.countCamerini, " +
                     "bs.bathrooms, bs.showers, bs.pool, bs.bar, bs.restaurant, bs.wifi, bs.volleyballField, " +
                     "p.nAutoPark, p.nMotoPark, p.nBikePark, p.nElectricPark, p.CCTV " +
                     "FROM beaches b " +
                     "LEFT JOIN beach_inventories bi ON b.id = bi.beachId " +
                     "LEFT JOIN beach_services bs ON b.id = bs.beachId " +
                     "LEFT JOIN parkings p ON b.id = p.beachId " +
                     "WHERE b.id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
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

                    //passo 3: ricostruisco BeachService (gestisco anche se non inizializzato)
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
                                rs.getInt("nBikePark"),
                                rs.getInt("nElectricPark"),
                                rs.getBoolean("CCTV")
                        );
                    }

                    //passo 5: salvo gli altri parametri
                    int ownerId = rs.getInt("ownerId");
                    int addressId = rs.getInt("addressId");
                    if (rs.wasNull()) ownerId = 0;
                    String extraInfo = rs.getString("extraInfo");
                    boolean active = rs.getBoolean("active");

                    //FIXME: attenzione qui!!!
                    //passo 6: salvo le stagioni
                    List<Season> seasons = findBeachSeasons(id);
                    List<Zone> zones = new ArrayList<>();

                    Beach beach = new Beach(id, ownerId, addressId, general, inventory, services, parking, extraInfo, seasons, zones, active);
                    return Optional.of(beach);
                }
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to find beach", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    //FIXME: modificare funzione!
    //cerca le stagioni di una spiaggia
    @Override
    public List<Season> findBeachSeasons(Integer beachId) {
        if (beachId == null || beachId <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        /*
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT pricingsId FROM seasons WHERE beachId = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, beachId);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        ids.add(rs.getInt("pricingsId"));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to find seasons", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
         */

        return new ArrayList<>();
    }
}