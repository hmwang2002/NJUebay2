CREATE DATABASE `ebaynju2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

-- ebaynju2.aliconfig definition

CREATE TABLE `aliconfig` (
                             `accesskey` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `accesssecret` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `id` int NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ebaynju2.good definition

CREATE TABLE `good` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `name` varchar(256) NOT NULL,
                        `description` text NOT NULL,
                        `img` text,
                        `sellerId` bigint NOT NULL,
                        `onSale` tinyint(1) NOT NULL,
                        `buyerId` bigint DEFAULT NULL,
                        `price` float NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `good_FK` (`sellerId`),
                        KEY `good_FK_1` (`buyerId`),
                        CONSTRAINT `good_FK` FOREIGN KEY (`sellerId`) REFERENCES `user` (`user_id`),
                        CONSTRAINT `good_FK_1` FOREIGN KEY (`buyerId`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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