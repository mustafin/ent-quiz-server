-- phpMyAdmin SQL Dump
-- version 4.3.11
-- http://www.phpmyadmin.net
--
-- Хост: 127.0.0.1
-- Время создания: Июн 20 2016 г., 14:37
-- Версия сервера: 5.6.24
-- Версия PHP: 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База данных: `entquiz`
--

-- --------------------------------------------------------

--
-- Структура таблицы `answer`
--

CREATE TABLE IF NOT EXISTS `answer` (
  `ID` bigint(20) NOT NULL,
  `IS_TRUE` bit(1) DEFAULT NULL,
  `TITLE` varchar(255) DEFAULT NULL,
  `question_id` bigint(20) DEFAULT NULL,
  `IMG` varchar(40) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `answer`
--

INSERT INTO `answer` (`ID`, `IS_TRUE`, `TITLE`, `question_id`, `IMG`) VALUES
(39, b'1', 'чеснока', 47, ''),
(40, b'0', 'клевера', 47, ''),
(41, b'0', 'яблони', 47, ''),
(42, b'0', 'подсолнечника', 47, ''),
(43, b'0', 'одуванчика', 47, ''),
(44, b'0', 'петуния', 48, ''),
(45, b'0', 'паслен', 48, ''),
(46, b'1', 'донник', 48, ''),
(47, b'0', 'таволга', 48, ''),
(48, b'0', 'клевер', 48, ''),
(49, b'1', '1791 г.', 49, ''),
(50, b'0', '1792 г.', 49, ''),
(51, b'0', '1780 г.', 49, ''),
(52, b'0', ' 1795 г.', 49, ''),
(53, b'0', '1787 г.', 49, ''),
(54, b'0', 'щиток', 50, ''),
(55, b'0', 'кисть', 50, ''),
(56, b'0', 'сережка', 50, ''),
(57, b'0', 'колос', 50, ''),
(58, b'1', 'зонтик', 50, ''),
(59, b'0', 'В.Ф. Храпатию.', 51, ''),
(60, b'0', 'В.Г. Клочкову.', 51, ''),
(61, b'1', 'И.В. Сталину.', 51, ''),
(62, b'0', 'Д.Г. Павлову.', 51, ''),
(63, b'0', 'Г.К. Жукову.', 51, ''),
(64, b'0', '1793 г.', 52, ''),
(65, b'0', '1786 г.', 52, ''),
(66, b'0', '1787 г.', 52, ''),
(67, b'0', '1776г.', 52, ''),
(68, b'1', ' 1791 г.', 52, ''),
(69, b'0', 'В пруду развели разную рыбу как-то карпов сазанов лещей.', 54, ''),
(70, b'0', 'Обращаться с языком кое-как значит и мыслить кое-как неточно приблизительно неверно. (А. Н. Толстой)', 54, ''),
(71, b'0', 'Зеленая долина горы в белых шапках все было залито солнцем. (В. Шукшин)', 54, ''),
(72, b'0', 'Все это и ночь и дали и горы и звезды и туманы казалось мне исполненным невиданной прелести. (В. Короленко)', 54, ''),
(73, b'1', 'Везде на полях и на лесных просеках и на дорожках пар поднимался от земли. (М. Пришвин)', 54, ''),
(74, b'0', ' 1356 г.', 53, ''),
(75, b'0', '1389 г.', 53, ''),
(76, b'0', '1354 г.', 53, ''),
(77, b'1', '1378 г.', 53, ''),
(78, b'0', '1350 г.', 53, ''),
(79, b'1', 'Ученье без уменья не польза, а беда. (Пословица)', 55, ''),
(80, b'0', 'Кругом были пни, да корявые стволы, да поросль. (С. Антонов)', 55, ''),
(81, b'0', 'Дремали не только леса, но и лесные озера и ленивые лесные реки с пресной водой. (К. Паустовский)', 55, ''),
(82, b'0', 'Ни близких берегов, ни далеких гор, ни даже воды – ничего не было видно. (В. Солоухин)', 55, ''),
(83, b'0', 'В человеке должно быть все прекрасно: и лицо, и одежда, и душа, и мысли. (А. П. Чехов)', 55, ''),
(84, b'0', ' Ученье без уменья не польза, а беда. (Пословица)', 56, ''),
(85, b'0', 'Кругом были пни, да корявые стволы, да поросль. (С. Антонов)', 56, ''),
(86, b'0', 'Дремали не только леса, но и лесные озера и ленивые лесные реки с пресной водой. (К. Паустовский)', 56, ''),
(87, b'0', 'Ни близких берегов, ни далеких гор, ни даже воды – ничего не было видно. (В. Солоухин)', 56, ''),
(88, b'1', 'В человеке должно быть все прекрасно: и лицо, и одежда, и душа, и мысли. (А. П. Чехов)', 56, ''),
(89, b'0', '1466 году', 57, ''),
(90, b'0', '1472 году', 57, ''),
(91, b'1', ' 1470 году', 57, ''),
(92, b'0', '1465 году', 57, ''),
(93, b'0', ' 1468 году', 57, ''),
(94, b'0', 'Биік сезім', 58, ''),
(95, b'1', 'Биік тау', 58, ''),
(96, b'0', 'Биік ой', 58, ''),
(97, b'0', 'Биік тұлға', 58, ''),
(98, b'0', 'Биік сөз', 58, ''),
(99, b'0', 'мангытами и чигили', 59, ''),
(100, b'1', 'кыпчаками и уйсунами', 59, ''),
(101, b'0', 'ягма и карлуками', 59, ''),
(102, b'0', 'найманами и кереитами', 59, ''),
(103, b'0', 'аргынами и барласами', 59, ''),
(104, b'0', 'Ниетің', 60, ''),
(105, b'1', 'Қисық', 60, ''),
(106, b'0', 'Болса', 60, ''),
(107, b'0', 'Ағайынға', 60, ''),
(108, b'0', 'Өкпелеме', 60, ''),
(109, b'1', 'Тұмау аяғы – құрт, тұман аяғы – жұт.', 61, ''),
(110, b'0', 'Самат аяғын ауыртып алды.', 61, ''),
(111, b'0', 'Ол науқасынан айығып кетті.', 61, ''),
(112, b'0', 'Жел күшейе түсті.', 61, ''),
(113, b'0', 'Ол аяғын созып жатыр.', 61, ''),
(114, b'0', 'Сыгнак', 62, ''),
(115, b'0', 'Сайрам', 62, ''),
(116, b'1', 'Сауран', 62, ''),
(117, b'0', 'Отрар', 62, ''),
(118, b'0', 'Туркестан', 62, ''),
(119, b'0', 'Ф. Магеллана', 63, ''),
(120, b'1', 'Абеля Тасмана', 63, ''),
(121, b'0', 'Д. Кука', 63, ''),
(122, b'0', 'Васко да Гамы', 63, ''),
(123, b'0', 'Х. Колумба', 63, ''),
(124, b'1', 'Жизни крестьян.', 64, ''),
(125, b'0', 'Природе.', 64, ''),
(126, b'0', 'Любви.', 64, ''),
(127, b'0', 'Исторических событиях.', 64, ''),
(128, b'0', 'Богатырях.', 64, ''),
(129, b'1', '«Смерть Поэта».', 65, ''),
(130, b'0', '«Узник».', 65, ''),
(131, b'0', '«Прощай, немытая Россия…».', 65, ''),
(132, b'0', '«Когда волнуется желтеющая нива…».', 65, ''),
(133, b'0', '«Родина».', 65, ''),
(134, b'0', 'Численным', 66, ''),
(135, b'1', ' Сложным', 66, ''),
(136, b'0', 'Цифровым', 66, ''),
(137, b'0', 'Именованным', 66, ''),
(138, b'0', 'Линейным', 66, ''),
(139, b'1', '«Макар Чудра».', 67, ''),
(140, b'0', '«В людях».', 67, ''),
(141, b'0', '«Мать».', 67, ''),
(142, b'0', '«На дне».', 67, ''),
(143, b'0', '«Фома Гордеев».', 67, ''),
(144, b'0', 'биосфера', 68, ''),
(145, b'1', 'гидросфера', 68, ''),
(146, b'0', 'неосфера', 68, ''),
(147, b'0', 'литосфера', 68, ''),
(148, b'0', 'атмосфера', 68, ''),
(149, b'1', 'profession', 69, ''),
(150, b'0', 'decision', 69, ''),
(151, b'0', 'respect', 69, ''),
(152, b'0', 'condition', 69, ''),
(153, b'0', 'success', 69, ''),
(154, b'0', 'Advise', 70, ''),
(155, b'0', 'Suppose', 70, ''),
(156, b'0', 'Reach', 70, ''),
(157, b'0', 'See', 70, ''),
(158, b'1', 'Notice', 70, ''),
(159, b'0', 'any', 71, ''),
(160, b'0', 'the', 71, ''),
(161, b'0', 'an', 71, ''),
(162, b'0', '–', 71, ''),
(163, b'1', 'some', 71, '');

-- --------------------------------------------------------

--
-- Структура таблицы `category`
--

CREATE TABLE IF NOT EXISTS `category` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `category`
--

INSERT INTO `category` (`id`, `name`) VALUES
(7, 'Биология'),
(8, 'Русский язык'),
(9, 'Қазақ тiлi'),
(10, 'Литература'),
(11, 'Всемирная История'),
(12, 'История Казахстана'),
(13, 'География'),
(14, 'Английский');

-- --------------------------------------------------------

--
-- Структура таблицы `game`
--

CREATE TABLE IF NOT EXISTS `game` (
  `ID` int(11) unsigned NOT NULL,
  `USER_ONE` int(11) unsigned NOT NULL,
  `USER_TWO` int(11) unsigned DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `SCORE_ONE` int(2) NOT NULL DEFAULT '0',
  `SCORE_TWO` int(2) NOT NULL DEFAULT '0',
  `USER_ONE_MOVE` tinyint(1) NOT NULL DEFAULT '1',
  `FINISHED` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `game_user`
--

CREATE TABLE IF NOT EXISTS `game_user` (
  `ID` int(11) unsigned NOT NULL,
  `PASSWORD` varchar(64) NOT NULL,
  `USERNAME` varchar(50) NOT NULL,
  `RATING` bigint(11) NOT NULL DEFAULT '1200'
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `game_user`
--

INSERT INTO `game_user` (`ID`, `PASSWORD`, `USERNAME`, `RATING`) VALUES
(1, '61304c99d645456f73c0126dfc69c6de249d52b81608bf2ec66ea01322e1aa', 'murat', 1200),
(3, '61304c99d645456f73c0126dfc69c6de249d52b81608bf2ec66ea01322e1aa', 'askar', 1200),
(4, '8d969eef6ecad3c29a3a629280e686cf03f5d5a86aff3ca1200923adc6c92', 'Tash', 1200),
(5, '8d969eef6ecad3c29a3a629280e686cf03f5d5a86aff3ca1200923adc6c92', 'Sergei', 1200);

-- --------------------------------------------------------

--
-- Структура таблицы `game_user_devices`
--

CREATE TABLE IF NOT EXISTS `game_user_devices` (
  `USER_ID` int(11) unsigned NOT NULL,
  `DEVICE_ID` varchar(100) NOT NULL,
  `DEVICE_OS` varchar(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `game_user_devices`
--

INSERT INTO `game_user_devices` (`USER_ID`, `DEVICE_ID`, `DEVICE_OS`) VALUES
(4, '02d3ebe24b2018b23e9ef5e0c7484e52166da2828225dbb96b362353bec33a0f', 'iOS'),
(4, '17955abb3ae9df78ad2756f92e90b4c8c20f6a234b9d67b8a0d257b9e1d012c1', 'iOS'),
(5, 'd08712625a4c5cb73948a4c2a8d6aa6a28d0089301a32e8383987b6caf9d75d8', 'iOS'),
(5, 'e58cb6acd10aee6180b76e857667037c4b58ad649a5ecd745fa40c6c098fdc5b', 'iOS');

-- --------------------------------------------------------

--
-- Структура таблицы `play_evolutions`
--

CREATE TABLE IF NOT EXISTS `play_evolutions` (
  `id` int(11) NOT NULL,
  `hash` varchar(255) NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `apply_script` text,
  `revert_script` text,
  `state` varchar(255) DEFAULT NULL,
  `last_problem` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `play_evolutions`
--

INSERT INTO `play_evolutions` (`id`, `hash`, `applied_at`, `apply_script`, `revert_script`, `state`, `last_problem`) VALUES
(1, '48d9e2e5dcfb4aa6643abc5eb24a8c260d19344b', '2015-07-12 14:50:18', 'ALTER TABLE QUESTION ADD COLUMN IMG VARCHAR(200) NOT NULL;', '', 'applied', 'Duplicate column name ''IMG'' [ERROR:1060, SQLSTATE:42S21]'),
(2, '1dda17f0536165ea2c053d55ca8667cef44d1c69', '2015-07-29 07:58:31', 'ALTER TABLE ANSWER ADD COLUMN IMG VARCHAR(200) NOT NULL;', '', 'applied', ''),
(3, '0010478fd6a4af4c0e70cb0e21652135464c40f7', '2015-09-14 11:18:21', 'CREATE TABLE IF NOT EXISTS GAME_USER(\n`ID` int(11) UNSIGNED PRIMARY KEY NOT NULL,\n`PASSWORD` varchar(64) NOT NULL,\n`USERNAME` varchar(50) NOT NULL\n) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;', '', 'applied', ''),
(4, '07387fdbdddc2b234aafadf6d452f94e8b08ddb9', '2015-09-14 11:19:12', 'ALTER TABLE GAME_USER ADD COLUMN `RATING` INT(11) DEFAULT 1200 NOT NULL;', '', 'applied', 'Duplicate column name ''RATING'' [ERROR:1060, SQLSTATE:42S21]'),
(5, '2b484f7f01715b3e2fe87ce3dd868c53398ddd7b', '2015-09-14 11:19:12', 'ALTER TABLE `GAME_USER` CHANGE `ID` `ID` int(11) UNSIGNED NOT NULL AUTO_INCREMENT', '', 'applied', ''),
(6, '5c99d2f912f83a2b84a0329bc4df302f99fc05b5', '2015-09-14 11:20:04', 'CREATE TABLE `GAME` (`ID` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT, `USER_ONE` INT(11) UNSIGNED NOT NULL,\n`USER_TWO` INT(11) UNSIGNED NOT NULL, `CREATED_AT` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\nPRIMARY KEY (`ID`) ,\nFOREIGN KEY (`USER_ONE`) REFERENCES `GAME_USER`(`ID`),\nFOREIGN KEY (`USER_TWO`) REFERENCES `GAME_USER`(`ID`)) ENGINE = InnoDB;', 'DROP TABLE `GAME`', 'applied', 'Table ''game'' already exists [ERROR:1050, SQLSTATE:42S01]'),
(7, '73af1ef187dedc4a62289559035dc6dc319148d4', '2015-10-01 18:22:06', 'CREATE TABLE `ROUND` (`ID` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,\n`GAME_ID` INT(11) UNSIGNED NOT NULL,\n`CATEGORY_ID` BIGINT(20) NOT NULL,\n`QUES_ONE` BIGINT(20) NOT NULL,\n`QUES_TWO` BIGINT(20) NOT NULL,\n`QUES_THREE` BIGINT(20) NOT NULL,\n`UONE_ANSONE` BIGINT(20),\n`UONE_ANSTWO` BIGINT(20),\n`UONE_ANSTHREE` BIGINT(20),\n`UTWO_ANSONE` BIGINT(20),\n`UTWO_ANSTWO` BIGINT(20),\n`UTWO_ANSTHREE` BIGINT(20),\nPRIMARY KEY (`ID`),\nFOREIGN KEY (`GAME_ID`) REFERENCES `GAME`(`ID`),\nFOREIGN KEY (`CATEGORY_ID`) REFERENCES `CATEGORY`(`ID`),\nFOREIGN KEY (`QUES_ONE`) REFERENCES `QUESTION`(`ID`),\nFOREIGN KEY (`QUES_TWO`) REFERENCES `QUESTION`(`ID`),\nFOREIGN KEY (`QUES_THREE`) REFERENCES `QUESTION`(`ID`),\nFOREIGN KEY (`UONE_ANSONE`) REFERENCES `ANSWER`(`ID`),\nFOREIGN KEY (`UONE_ANSTWO`) REFERENCES `ANSWER`(`ID`),\nFOREIGN KEY (`UONE_ANSTHREE`) REFERENCES `ANSWER`(`ID`),\nFOREIGN KEY (`UTWO_ANSONE`) REFERENCES `ANSWER`(`ID`),\nFOREIGN KEY (`UTWO_ANSTWO`) REFERENCES `ANSWER`(`ID`),\nFOREIGN KEY (`UTWO_ANSTHREE`) REFERENCES `ANSWER`(`ID`)\n) ENGINE = InnoDB;', '# DROP TABLE `ROUND`', 'applied', ''),
(8, 'f4d117b06b5d5e9788ccfde0d071b28c3e82d902', '2015-10-11 15:56:13', 'ALTER TABLE `GAME`\nADD COLUMN `SCORE_ONE` INT(2) NOT NULL DEFAULT 0,\nADD COLUMN `SCORE_TWO` INT(2) NOT NULL DEFAULT 0;', '', 'applied', ''),
(9, '13ec6a3f6321b93ca189216b2d519e49bd660954', '2015-10-16 13:00:14', 'ALTER TABLE `ROUND`\nMODIFY `CATEGORY_ID` BIGINT(20) NULL,\nMODIFY `QUES_ONE` BIGINT(20) NULL,\nMODIFY `QUES_TWO` BIGINT(20) NULL,\nMODIFY `QUES_THREE` BIGINT(20) NULL;', '', 'applied', ''),
(10, 'a5c19bf26698ccabf1ccae855e9b8a89129efdd9', '2015-10-17 17:47:40', 'ALTER TABLE `GAME`\nADD COLUMN `USER_ONE_MOVE` TINYINT(1) NOT NULL DEFAULT 1;', '', 'applied', 'Duplicate column name ''USER_ONE_MOVE'' [ERROR:1060, SQLSTATE:42S21]');

-- --------------------------------------------------------

--
-- Структура таблицы `question`
--

CREATE TABLE IF NOT EXISTS `question` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `IMG` varchar(40) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `question`
--

INSERT INTO `question` (`id`, `title`, `category_id`, `IMG`) VALUES
(47, 'Мочковатая корневая система характерна для:', 7, ''),
(48, 'Растение семейства бобовых имеющее соцветие кисть:', 7, ''),
(49, '«Билль о правах», внесший демократические поправки в конституцию США, был принят в:', 11, ''),
(50, 'Цветки примулы собраны в соцветие:', 7, ''),
(51, '«Велика Россия, а отступать некуда: позади — Москва!» — эти легендарные слова принадлежат:', 11, ''),
(52, '«Декларация независимости» США была принята в:', 11, ''),
(53, '«Золотая булла» была издана Карлом IV в:', 11, ''),
(54, 'Двоеточие, две запятые и тире пропущены в предложении', 8, ''),
(55, 'Предложение с однородными сказуемыми:', 8, ''),
(56, 'Предложение с однородными подлежащими и обобщающим словом при них:', 8, ''),
(57, 'Абулхаир хан умер во время похода на Могулистан в:', 12, ''),
(58, '“Биік” сөзінің тура мағынада қолданғанын белгілеңіз.', 9, ''),
(59, 'Казахская народность сложилась из двух этнических групп во главе с:', 12, ''),
(60, 'Ауыспалы мағынада қолданылған сөзді көрсетіңіз.\r\nНиетің қисық болса, ағайынға өкпелеме.', 9, ''),
(61, 'Ауыспалы мағынада қолданылып тұрған сөзі бар сөйлемді анықтаңыз.', 9, ''),
(62, 'В XVI-XVII веках в Дешт-и-Кыпчаке основным местом торговли являлся город на границе с\r\nкочевой степью:', 12, ''),
(63, 'Понятие о целостности Мирового океана утвердилось после путешествия', 13, ''),
(64, 'Былины- произведения устного народного творчества о:', 10, ''),
(65, 'Мотив одиночества и тоски по свободе в творчестве М.Ю. Лермонтова звучит в стихотворении:', 10, ''),
(66, 'Масштаб записанный с пояснением ( например в 1 см – 100 м) называется', 13, ''),
(67, 'Романтическое мироощущение героев показано М. Горьким в произведении:', 10, ''),
(68, 'Приведенные ниже термины: барометр, бриз, муссон – относятся к оболочке', 13, ''),
(69, 'Выберите слово, сходное по значению со словом occupation', 14, ''),
(70, 'Выберите слово, близкое по значению: believe', 14, ''),
(71, 'Выберите правильное слово\r\nThis is … way to school.', 14, '');

-- --------------------------------------------------------

--
-- Структура таблицы `role`
--

CREATE TABLE IF NOT EXISTS `role` (
  `ID` bigint(20) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `round`
--

CREATE TABLE IF NOT EXISTS `round` (
  `ID` int(11) unsigned NOT NULL,
  `GAME_ID` int(11) unsigned NOT NULL,
  `CATEGORY_ID` bigint(20) DEFAULT NULL,
  `QUES_ONE` bigint(20) DEFAULT NULL,
  `QUES_TWO` bigint(20) DEFAULT NULL,
  `QUES_THREE` bigint(20) DEFAULT NULL,
  `UONE_ANSONE` bigint(20) DEFAULT NULL,
  `UONE_ANSTWO` bigint(20) DEFAULT NULL,
  `UONE_ANSTHREE` bigint(20) DEFAULT NULL,
  `UTWO_ANSONE` bigint(20) DEFAULT NULL,
  `UTWO_ANSTWO` bigint(20) DEFAULT NULL,
  `UTWO_ANSTHREE` bigint(20) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `ID` bigint(20) NOT NULL,
  `ENABLED` bit(1) DEFAULT b'1',
  `PASSWORD` varchar(255) DEFAULT NULL,
  `USERNAME` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `user`
--

INSERT INTO `user` (`ID`, `ENABLED`, `PASSWORD`, `USERNAME`) VALUES
(1, b'1', '61304c99d645456f73c0126dfc69c6de249d52b81608bf2ec66ea01322e1aa', 'admin');

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `answer`
--
ALTER TABLE `answer`
  ADD PRIMARY KEY (`ID`), ADD KEY `FK_7tfb0oeiv6j49ngugjalk35aq` (`question_id`);

--
-- Индексы таблицы `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `game`
--
ALTER TABLE `game`
  ADD PRIMARY KEY (`ID`), ADD KEY `USER_ONE` (`USER_ONE`), ADD KEY `USER_TWO` (`USER_TWO`);

--
-- Индексы таблицы `game_user`
--
ALTER TABLE `game_user`
  ADD PRIMARY KEY (`ID`);

--
-- Индексы таблицы `game_user_devices`
--
ALTER TABLE `game_user_devices`
  ADD UNIQUE KEY `UNIQUE_INDEX` (`USER_ID`,`DEVICE_ID`,`DEVICE_OS`);

--
-- Индексы таблицы `play_evolutions`
--
ALTER TABLE `play_evolutions`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `question`
--
ALTER TABLE `question`
  ADD PRIMARY KEY (`id`), ADD KEY `FK_9cka8218kjlltqfaddq2lusw7` (`category_id`);

--
-- Индексы таблицы `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`ID`), ADD KEY `FK_i2tvaqmf5t5x1i9yeip7qsc8v` (`user_id`);

--
-- Индексы таблицы `round`
--
ALTER TABLE `round`
  ADD PRIMARY KEY (`ID`), ADD KEY `round_ibfk_1` (`GAME_ID`), ADD KEY `round_ibfk_10` (`UTWO_ANSTWO`), ADD KEY `round_ibfk_11` (`UTWO_ANSTHREE`), ADD KEY `round_ibfk_2` (`CATEGORY_ID`), ADD KEY `round_ibfk_3` (`QUES_ONE`), ADD KEY `round_ibfk_4` (`QUES_TWO`), ADD KEY `round_ibfk_5` (`QUES_THREE`), ADD KEY `round_ibfk_7` (`UONE_ANSTWO`), ADD KEY `round_ibfk_8` (`UONE_ANSTHREE`), ADD KEY `round_ibfk_9` (`UTWO_ANSONE`), ADD KEY `round_ibfk_6` (`UONE_ANSONE`);

--
-- Индексы таблицы `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `answer`
--
ALTER TABLE `answer`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=164;
--
-- AUTO_INCREMENT для таблицы `category`
--
ALTER TABLE `category`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=15;
--
-- AUTO_INCREMENT для таблицы `game`
--
ALTER TABLE `game`
  MODIFY `ID` int(11) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=152;
--
-- AUTO_INCREMENT для таблицы `game_user`
--
ALTER TABLE `game_user`
  MODIFY `ID` int(11) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT для таблицы `question`
--
ALTER TABLE `question`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=72;
--
-- AUTO_INCREMENT для таблицы `role`
--
ALTER TABLE `role`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT для таблицы `round`
--
ALTER TABLE `round`
  MODIFY `ID` int(11) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=179;
--
-- AUTO_INCREMENT для таблицы `user`
--
ALTER TABLE `user`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `answer`
--
ALTER TABLE `answer`
ADD CONSTRAINT `FK_7tfb0oeiv6j49ngugjalk35aq` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `game`
--
ALTER TABLE `game`
ADD CONSTRAINT `game_ibfk_1` FOREIGN KEY (`USER_ONE`) REFERENCES `game_user` (`ID`),
ADD CONSTRAINT `game_ibfk_2` FOREIGN KEY (`USER_TWO`) REFERENCES `game_user` (`ID`);

--
-- Ограничения внешнего ключа таблицы `game_user_devices`
--
ALTER TABLE `game_user_devices`
ADD CONSTRAINT `game_user_devices_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `game_user` (`ID`);

--
-- Ограничения внешнего ключа таблицы `question`
--
ALTER TABLE `question`
ADD CONSTRAINT `FK_9cka8218kjlltqfaddq2lusw7` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `role`
--
ALTER TABLE `role`
ADD CONSTRAINT `FK_i2tvaqmf5t5x1i9yeip7qsc8v` FOREIGN KEY (`user_id`) REFERENCES `user` (`ID`);

--
-- Ограничения внешнего ключа таблицы `round`
--
ALTER TABLE `round`
ADD CONSTRAINT `round_ibfk_1` FOREIGN KEY (`GAME_ID`) REFERENCES `game` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_10` FOREIGN KEY (`UTWO_ANSTWO`) REFERENCES `answer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_11` FOREIGN KEY (`UTWO_ANSTHREE`) REFERENCES `answer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_2` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_3` FOREIGN KEY (`QUES_ONE`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_4` FOREIGN KEY (`QUES_TWO`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_5` FOREIGN KEY (`QUES_THREE`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_6` FOREIGN KEY (`UONE_ANSONE`) REFERENCES `answer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_7` FOREIGN KEY (`UONE_ANSTWO`) REFERENCES `answer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_8` FOREIGN KEY (`UONE_ANSTHREE`) REFERENCES `answer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `round_ibfk_9` FOREIGN KEY (`UTWO_ANSONE`) REFERENCES `answer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
