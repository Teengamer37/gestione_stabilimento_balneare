package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.BeachRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.beach.BeachGeneral;
import com.example.s_balneare.domain.beach.BeachInventory;
import com.example.s_balneare.domain.beach.BeachServices;
import com.example.s_balneare.domain.beach.Parking;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBeachRepository implements BeachRepository {
    private final DataSource dataSource;

    public JdbcBeachRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //inserimento nuova spiaggia nel DB
    //DA USARE SE E SOLO SE SI HA UN'ISTANZA LIBERA E NON ASSOCIATA DI ADDRESS
    //SE USATA A CASO -> EXCEPION 100%
    @Override
    public int save(Beach beach) {
        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                int beachId = save(beach, connection);
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
    public int save(Beach beach, Connection conn) {
        String sqlBeach = "INSERT INTO beaches (name, description, telephoneNumber, addressId, extraInfo, active, ownerId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlInventory = "INSERT INTO beach_inventories (beachId, countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlServices = "INSERT INTO beach_services (beachId, bathrooms, showers, pool, bar, restaurant, wifi, volleyballField) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlParking = "INSERT INTO parkings (beachId, nAutoPark, nMotoPark, nBikePark, nElectricPark, CCTV) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            int newId;

            //passo 1: inserisco in beaches gli oggetti Beach e BeachGeneral
            try (PreparedStatement statement = conn.prepareStatement(sqlBeach, Statement.RETURN_GENERATED_KEYS)) {
                BeachGeneral general = beach.getBeachGeneral();
                if (general == null) throw new IllegalArgumentException("ERROR: BeachGeneral cannot be null when saving a beach.");

                statement.setString(1, general.getName());
                statement.setString(2, general.getDescription());
                statement.setString(3, general.getTelephoneNumber());
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
                try (PreparedStatement statement = conn.prepareStatement(sqlInventory)) {
                    BeachInventory inventory = beach.getBeachInventory();
                    statement.setInt(1, newId);
                    statement.setInt(2, inventory.countOmbrelloni());
                    statement.setInt(3, inventory.countTende());
                    statement.setInt(4, inventory.countExtraSdraio());
                    statement.setInt(5, inventory.countExtraLettini());
                    statement.setInt(6, inventory.countExtraSedie());
                    statement.setInt(7, inventory.countCamerini());
                    statement.executeUpdate();
                }
            }

            //passo 3: inserisco in beach_services l'oggetto BeachService (se presente)
            if (beach.getBeachServices() != null) {
                try (PreparedStatement statement = conn.prepareStatement(sqlServices)) {
                    BeachServices services = beach.getBeachServices();
                    statement.setInt(1, newId);
                    statement.setBoolean(2, services.bathrooms());
                    statement.setBoolean(3, services.showers());
                    statement.setBoolean(4, services.pool());
                    statement.setBoolean(5, services.bar());
                    statement.setBoolean(6, services.restaurant());
                    statement.setBoolean(7, services.wifi());
                    statement.setBoolean(8, services.volleyballField());
                    statement.executeUpdate();
                }
            }

            //passo 4: inserisco in parkings l'oggetto Parking (se presente)
            if (beach.getParking() != null) {
                try (PreparedStatement statement = conn.prepareStatement(sqlParking)) {
                    Parking parking = beach.getParking();
                    statement.setInt(1, newId);
                    statement.setInt(2, parking.nAutoPark());
                    statement.setInt(3, parking.nMotoPark());
                    statement.setInt(4, parking.nBikePark());
                    statement.setInt(5, parking.nElectricPark());
                    statement.setBoolean(6, parking.CCTV());
                    statement.executeUpdate();
                }
            }

            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save beach", e);
        }
    }

    //aggiorno spiaggia presente nel DB
    @Override
    public void update(Beach beach) {
        String sqlBeach = "UPDATE beaches SET name = ?, description = ?, telephoneNumber = ?, addressId = ?, extraInfo = ?, active = ?, ownerId = ? WHERE id = ?";

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
                    statement.setString(3, general.getTelephoneNumber());
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
                    upsertInventory(beach, connection);
                } else {
                    deleteComponent(beach.getId(), "beach_inventories");
                }

                //passo 3: aggiorno/inserisco dati nuovi su BeachService (o elimino dati)
                //dalla tabella beach_services
                if (beach.getBeachServices() != null) {
                    upsertServices(beach, connection);
                } else {
                    deleteComponent(beach.getId(), "beach_services");
                }

                //passo 4: aggiorno/inserisco dati nuovi su Parking (o elimino dati)
                //dalla tabella parkings
                if (beach.getParking() != null) {
                    upsertParking(beach, connection);
                } else {
                    deleteComponent(beach.getId(), "parkings");
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

    //permette di aggiornare beach_inventories
    private void upsertInventory(Beach beach, Connection connection) throws SQLException {
        //UPDATE se nel caso abbiamo già un beach_inventories istanziato nel DB
        String updateSql = "UPDATE beach_inventories SET countOmbrelloni = ?, countTende = ?, countExtraSdraio = ?, countExtraLettini = ?, countExtraSedie = ?, countCamerini = ? WHERE beachId = ?";
        try (PreparedStatement st = connection.prepareStatement(updateSql)) {
            BeachInventory inv = beach.getBeachInventory();
            st.setInt(1, inv.countOmbrelloni());
            st.setInt(2, inv.countTende());
            st.setInt(3, inv.countExtraSdraio());
            st.setInt(4, inv.countExtraLettini());
            st.setInt(5, inv.countExtraSedie());
            st.setInt(6, inv.countCamerini());
            st.setInt(7, beach.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                //INSERT altrimenti
                String insertSql = "INSERT INTO beach_inventories (beachId, countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertSt = connection.prepareStatement(insertSql)) {
                    insertSt.setInt(1, beach.getId());
                    insertSt.setInt(2, inv.countOmbrelloni());
                    insertSt.setInt(3, inv.countTende());
                    insertSt.setInt(4, inv.countExtraSdraio());
                    insertSt.setInt(5, inv.countExtraLettini());
                    insertSt.setInt(6, inv.countExtraSedie());
                    insertSt.setInt(7, inv.countCamerini());
                    insertSt.executeUpdate();
                }
            }
        }
    }

    //permette di aggiornare beach_services
    private void upsertServices(Beach beach, Connection connection) throws SQLException {
        //UPDATE se nel caso abbiamo già un beach_services istanziato nel DB
        String updateSql = "UPDATE beach_services SET bathrooms = ?, showers = ?, pool = ?, bar = ?, restaurant = ?, wifi = ?, volleyballField = ? WHERE beachId = ?";
        try (PreparedStatement st = connection.prepareStatement(updateSql)) {
            BeachServices srv = beach.getBeachServices();
            st.setBoolean(1, srv.bathrooms());
            st.setBoolean(2, srv.showers());
            st.setBoolean(3, srv.pool());
            st.setBoolean(4, srv.bar());
            st.setBoolean(5, srv.restaurant());
            st.setBoolean(6, srv.wifi());
            st.setBoolean(7, srv.volleyballField());
            st.setInt(8, beach.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                //INSERT altrimenti
                String insertSql = "INSERT INTO beach_services (beachId, bathrooms, showers, pool, bar, restaurant, wifi, volleyballField) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertSt = connection.prepareStatement(insertSql)) {
                    insertSt.setInt(1, beach.getId());
                    insertSt.setBoolean(2, srv.bathrooms());
                    insertSt.setBoolean(3, srv.showers());
                    insertSt.setBoolean(4, srv.pool());
                    insertSt.setBoolean(5, srv.bar());
                    insertSt.setBoolean(6, srv.restaurant());
                    insertSt.setBoolean(7, srv.wifi());
                    insertSt.setBoolean(8, srv.volleyballField());
                    insertSt.executeUpdate();
                }
            }
        }
    }

    //permette di aggiornare parkings
    private void upsertParking(Beach beach, Connection connection) throws SQLException {
        //UPDATE se nel caso abbiamo già un parking istanziato nel DB
        String updateSql = "UPDATE parkings SET nAutoPark = ?, nMotoPark = ?, nBikePark = ?, nElectricPark = ?, CCTV = ? WHERE beachId = ?";
        try (PreparedStatement st = connection.prepareStatement(updateSql)) {
            Parking p = beach.getParking();
            st.setInt(1, p.nAutoPark());
            st.setInt(2, p.nMotoPark());
            st.setInt(3, p.nBikePark());
            st.setInt(4, p.nElectricPark());
            st.setBoolean(5, p.CCTV());
            st.setInt(6, beach.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                //INSERT altrimenti
                String insertSql = "INSERT INTO parkings (beachId, nAutoPark, nMotoPark, nBikePark, nElectricPark, CCTV) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertSt = connection.prepareStatement(insertSql)) {
                    insertSt.setInt(1, beach.getId());
                    insertSt.setInt(2, p.nAutoPark());
                    insertSt.setInt(3, p.nMotoPark());
                    insertSt.setInt(4, p.nBikePark());
                    insertSt.setInt(5, p.nElectricPark());
                    insertSt.setBoolean(6, p.CCTV());
                    insertSt.executeUpdate();
                }
            }
        }
    }

    //permette di eliminare righe da una tabella
    private void deleteComponent(int beachId, String tableName) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE beachId = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement(sql)) {
                st.setInt(1, beachId);
                st.executeUpdate();
            }
        }
    }

    //permette di eliminare una spiaggia
    @Override
    public void delete(int id) {
        String sqlServices = "DELETE FROM beach_services WHERE beachId = ?";
        String sqlInventory = "DELETE FROM beach_inventories WHERE beachId = ?";
        String sqlParking = "DELETE FROM parkings WHERE beachId = ?";
        String sqlBeach = "DELETE FROM beaches WHERE id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                //cancello riga da beach_services
                try (PreparedStatement st = connection.prepareStatement(sqlServices)) {
                    st.setInt(1, id);
                    st.executeUpdate();
                }
                //cancello riga da beach_inventories
                try (PreparedStatement st = connection.prepareStatement(sqlInventory)) {
                    st.setInt(1, id);
                    st.executeUpdate();
                }
                //cancello riga da parkings
                try (PreparedStatement st = connection.prepareStatement(sqlParking)) {
                    st.setInt(1, id);
                    st.executeUpdate();
                }
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
                        String telephoneNumber = rs.getString("telephoneNumber");

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
                        beaches.add(new Beach(id, ownerId, addressId, general, null, services, parking, extraInfo, null, active));
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
    public Optional<Beach> findById(int id) {
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
                    String telephoneNumber = rs.getString("telephoneNumber");

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

                    //passo 6: salvo le stagioni
                    List<Integer> seasonIds = findBeachSeasonIds(id);

                    Beach beach = new Beach(id, ownerId, addressId, general, inventory, services, parking, extraInfo, seasonIds, active);
                    return Optional.of(beach);
                }
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to find beach", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection", e);
        }
    }

    //TODO: spostare funzione in JdbcSeasonRepository
    //cerca le stagioni di una spiaggia
    @Override
    public List<Integer> findBeachSeasonIds(int beachId) {
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
        return ids;
    }
}