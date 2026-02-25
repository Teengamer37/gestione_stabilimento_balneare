/* TBD: Soldino, appena ci sei con i DB, in questo file mi devi fare la tabella utenti
   QUESTO FILE SERVE A FLYWAY

   esempio:
   CREATE TABLE IF NOT EXISTS users (
      id uuid PRIMARY KEY,
      role varchar(20) NOT NULL, -- CUSTOMER / OWNER / ADMIN
      email varchar(255) NOT NULL UNIQUE,
      password_hash varchar(255) NOT NULL,
      active boolean NOT NULL DEFAULT true
    );

    CREATE TABLE IF NOT EXISTS customers (
      user_id uuid PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
      username varchar(100) NOT NULL,
      beachName varchar(80) NOT NULL,
      surname varchar(80) NOT NULL,
      phone varchar(30) NOT NULL
      -- addressId fields qui oppure in tabella addressId separata (preferibilmente separata)
    );

    create table if not exists owners (
      user_id uuid PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
      username varchar(100) NOT NULL,
      phone varchar(30) NOT NULL
      -- TBD: più avanti: collegamento a beach/stabilimento
    );

    CREATE TABLE IF NOT EXISTS admins (
      user_id uuid PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
      username varchar(100) NOT NULL
    );

   ---------------------------------------------------------------------------------------------------

   Per lo stabilimento, abbiamo che ogni gestore ha un solo stabilimento (relazione 1 a 1)
 */