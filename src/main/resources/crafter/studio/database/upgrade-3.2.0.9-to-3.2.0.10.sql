/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

CREATE TABLE IF NOT EXISTS `item_translation_request` (
  `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
  `source_id`               BIGINT(20)      NOT NULL,
  `locale_code`             VARCHAR(16)     NOT NULL,
  `date_requested`          TIMESTAMP       NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY `item_translation_request_ix_source`(`source_id`) REFERENCES `item` (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    ROW_FORMAT = DYNAMIC ;

UPDATE _meta SET version = '3.2.0.10' ;