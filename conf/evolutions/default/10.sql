# add scores to GAME table

# --- !Ups

ALTER TABLE `GAME`
ADD COLUMN `USER_ONE_MOVE` TINYINT(1) NOT NULL DEFAULT 1;
# --- !Downs

