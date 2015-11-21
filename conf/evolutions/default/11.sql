# GAME_USER_DEVICES table

# --- !Ups

CREATE TABLE IF NOT EXISTS GAME_USER_DEVICES(
  `USER_ID` int(11) UNSIGNED NOT NULL,
  `DEVICE_ID` varchar(100) NOT NULL,
  `DEVICE_OS` varchar(64) NOT NULL,
  FOREIGN KEY (`USER_ID`) REFERENCES `GAME_USER`(`ID`),
  UNIQUE KEY `UNIQUE_INDEX` (`USER_ID`,`DEVICE_ID`, `DEVICE_OS`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

# --- !Downs