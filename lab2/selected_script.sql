/*--- JOIN ---*/
-- (INNER JOIN) Получить имена, фамилии, эл.почту, заголовки новостей, дату редактирования
SELECT
    "user".first_name AS user_first_name,
    "user".last_name AS user_last_name,
    "user".email AS user_email,
    new.title AS new_title,
    new.edit_date AS new_edit_date
FROM "user"
JOIN new ON "user".id = new.user;

-- (LEFT OUTER JOIN) Вывести все новости и картинки к ним. Если картинки нет, то NULL
SELECT
    new.title AS new_title,
    new.lead AS new_lead,
    image.path AS image_path
FROM new
LEFT JOIN image ON image.new = new.id;

-- (CROSS JOIN) Получить перекрестное соединение двух таблиц: session и user. Смысла в этом нет, но для примера
SELECT *
FROM session CROSS JOIN "user";

-- (RIGHT OUTER JOIN) Получить всех пользователей, независимо от того, писали они комментарии, или нет
SELECT
    comment.text AS comment_text,
    comment.edit_date AS comment_edit_date,
    "user".first_name AS user_first_name,
    "user".last_name AS user_last_name
FROM comment
RIGHT JOIN "user" ON comment."user" = "user".id


/*--- GROUP BY ---*/
-- вывести из таблицы логов контент, и количество действий, которое над ним совершалось
SELECT
    log.content AS log_content,
    COUNT(*) AS actions_count
FROM log
GROUP BY content;

-- вывести групп каждого типа среди пользователей
SELECT
    "user"."group" AS user_group,
    COUNT(*) AS group_count
FROM "user"
GROUP BY "group";

-- отобрать кол-во опубликованных страниц, если кол-во типа контента на них >= 3
SELECT
    page.content AS page_content,
    COUNT(*) AS content_count
FROM page
WHERE is_published = TRUE
GROUP BY content
HAVING COUNT(*) >= 3