DROP TABLE IF EXISTS prices;

CREATE TABLE wallets (
  ID IDENTITY AUTO_INCREMENT PRIMARY KEY,
  BALANCE DECIMAL NOT NULL
);