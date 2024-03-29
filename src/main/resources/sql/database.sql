CREATE DATABASE `ebaynju2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

use ebaynju2;

-- ebaynju2.aliconfig definition

CREATE TABLE `aliconfig` (
                             `accesskey` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `accesssecret` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `id` int NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ebaynju2.`user` definition

CREATE TABLE `user` (
                        `user_id` bigint NOT NULL AUTO_INCREMENT,
                        `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                        `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                        `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                        `create_time` date NOT NULL,
                        `last_login_time` date DEFAULT NULL,
                        `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                        PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ebaynju2.good definition

# CREATE TABLE `good` (
#                         `id` bigint NOT NULL AUTO_INCREMENT,
#                         `name` varchar(256) NOT NULL,
#                         `description` text NOT NULL,
#                         `img` text,
#                         `sellerId` bigint NOT NULL,
#                         `onSale` ENUM('ON_SALE', 'DEALING', 'SOLD') NOT NULL,
#                         `buyerId` bigint DEFAULT NULL,
#                         `price` float NOT NULL,
#                         PRIMARY KEY (`id`),
#                         KEY `good_FK` (`sellerId`),
#                         KEY `good_FK_1` (`buyerId`),
#                         CONSTRAINT `good_FK` FOREIGN KEY (`sellerId`) REFERENCES `user` (`user_id`),
#                         CONSTRAINT `good_FK_1` FOREIGN KEY (`buyerId`) REFERENCES `user` (`user_id`)
# ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `good` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(256) NOT NULL,
                        `imgList` TEXT,
                        `sellerId` BIGINT NOT NULL,
                        `onSale` ENUM('ON_SALE', 'DEALING', 'SOLD') NOT NULL, -- Assuming these are the states in SaleState enum
                        `buyerId` BIGINT DEFAULT NULL,
                        `newnessDesc` VARCHAR(255) DEFAULT NULL,
                        `purchasePrice` DOUBLE NOT NULL,
                        `expectPrice` DOUBLE NOT NULL,
                        `mainDesc` TEXT,
                        PRIMARY KEY (`id`),
                        KEY `good_fk_seller` (`sellerId`),
                        KEY `good_fk_buyer` (`buyerId`),
                        CONSTRAINT `good_fk_seller` FOREIGN KEY (`sellerId`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                        CONSTRAINT `good_fk_buyer` FOREIGN KEY (`buyerId`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;