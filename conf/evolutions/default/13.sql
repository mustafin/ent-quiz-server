# add finished to GAME table

# --- !Ups

ALTER TABLE `GAME`
ADD COLUMN `FINISHED` TINYINT(1) NOT NULL DEFAULT 0;
# --- !Downs

