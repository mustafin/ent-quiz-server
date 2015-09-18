# Add auto increment id to GAME_USER

# --- !Ups

ALTER TABLE `GAME_USER` CHANGE `ID` `ID` int(11) UNSIGNED NOT NULL AUTO_INCREMENT

# --- !Downs