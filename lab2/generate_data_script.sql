-- Удаление всех данных из таблиц
DELETE FROM source;
DELETE FROM mailing;
-- DELETE FROM session;
DELETE FROM image;
-- DELETE FROM new_tag;
DELETE FROM comment;
-- DELETE FROM group_permission;
-- DELETE FROM page;
-- DELETE FROM log;
DELETE FROM article;
DELETE FROM afisha;
DELETE FROM category;
DELETE FROM tag;
DELETE FROM "user";
-- DELETE FROM permission;
-- DELETE FROM content;
DELETE FROM "group";

-- Данные для таблицы mailing
INSERT INTO mailing(email)
    SELECT
        (array['vasya', 'petya', 'atrem', 'maxim', 'kolya', 'lena', 'misha', 'asya'])[ceil(random() * 8)] || '_' ||
        SUBSTRING(md5(random()::text) from 1 for ceil(random() * 8 + 5)::integer) || '_' ||
        (array['1990', '1991', '1992', '1993', '1994', '1995', '1996', '1997', '1998'])[ceil(random() * 8)] ||
        (array['@mail.ru', '@gmail.com', '@yandex.ru'])[ceil(random() * 3)]
    FROM generate_series(1, 30);

-- Данные для таблицы session
-- INSERT INTO session(session_key, session_data, expire_date)
--     SELECT
--         SUBSTRING(sha256(random()::text::bytea)::text from 3 for 40),
--         md5(random()::text) || sha256(random()::text::bytea),
--         now() + interval '1 day' * round(random() * 100) + interval '1 hour' * round(random() * 24)
--             + interval '1 minute' * round(random() * 60) + interval '1 second' * round(random() * 60)
--     FROM generate_series(1, 15);

-- Данные для таблицы content
-- INSERT INTO content(entity)
--     SELECT
--         (array['user', 'tag', 'category', 'new', 'image', 'page', 'comment', 'log', 'mailing', 'session',
--         'permission', 'group'])[iter]
--     FROM generate_series(1, 12) as iter;

-- Данные для таблицы category
INSERT INTO category(title)
    SELECT
        (array['спорт', 'политика', 'кинематограф', 'искусство', 'экономика', 'наука', 'музыка'])[iter]
    FROM generate_series(1, 7) as iter;

-- Данные для таблицы source
INSERT INTO source(title, url)
SELECT
    (array[
        'Яндекс ДЗЕН',
        'РИА',
        'Лента',
        'News Front',
        'Лайф'])[iter],
    (array[
        'https://zen.yandex.ru/',
        'https://ria.ru/',
        'https://lenta.ru/',
        'https://news-front.info/',
        'https://life.ru/'])[iter]
FROM generate_series(1, 5) as iter;

-- Данные для таблицы tag
INSERT INTO tag(title)
    SELECT
        (array['ufc', 'футбол', 'хоккей', 'внешняя политика', 'внутренняя политика', 'конфликт', 'премьеры фильмов',
            'зарубежные фильмы', 'театр', 'балет', 'внешняя экономика', 'внутренняя экономика', 'кризис',
            'гаджеты', 'IT', 'программирование', 'поп-музыка', 'концерты'])[iter]
    FROM generate_series(1, 18) as iter;

-- Данные для таблицы group
INSERT INTO "group"(title)
    SELECT
        (array['admin', 'editor', 'seo', 'guest'])[iter]
    FROM generate_series(1, 4) as iter;

-- Данные для таблицы user
INSERT INTO "user"(password, username, first_name, last_name, email, group_id, last_login, date_joined, is_superuser,
                   is_staff, is_active)
    SELECT
        md5(random()::text),
        'user_' || iter,
        'first_name_' || iter,
        'last_name_' || iter,
        'email_' || iter || (array['@mail.ru', '@gmail.com', '@yandex.ru'])[ceil(random() * 3)],
        (SELECT min(id) FROM "group") + trunc(random() * 4),
        now() - interval '1 day' * round(random() * 200) + interval '1 minute' * round(random() * 60)
            + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60),
        now() - interval '1 day' * round(random() * 200 + 201) + interval '1 minute' * round(random() * 60)
            + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60),
        FALSE,
        (array[TRUE, FALSE])[round(random()) + 1],
        (array[TRUE, FALSE])[round(random()) + 1]
    FROM generate_series(1, 10) as iter;

-- Данные для таблицы new
INSERT INTO article(title, lead, create_date, edit_date, text, is_published, category_id, user_id)
    SELECT
        'title_' || iter,
        'lead_' || iter,
        now() - interval '1 day' * round(random() * 50 + 51) + interval '1 minute' * round(random() * 60)
            + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60),
        now() - interval '1 day' * round(random() * 50) + interval '1 minute' * round(random() * 60)
            + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60),
        'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. ' ||
        'Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus ' ||
        'mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis ' ||
        'enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, ' ||
        'imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt.',
        (array[TRUE, FALSE])[round(random()) + 1],
        (SELECT min(id) FROM category) + trunc(random() * 7),
        (SELECT min(id) FROM "user") + trunc(random() * 10)
    FROM generate_series(1, 50) as iter;

-- Данные для таблицы afisha
INSERT INTO afisha(title, lead, description, age_limit, timing, place, phone, date, is_commercial, user_id, source_id)
    SELECT
        'title_' || iter,
        'lead_' || iter,
        'description_' || iter,
        round(random() * 18)::text,
        round(random() * 240)::text,
        'place_' || iter,
        '89202004433',
        now() - interval '1 day' * round(random() * 50) + interval '1 minute' * round(random() * 60)
            + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60),
        (array[TRUE, FALSE])[round(random()) + 1],
        (SELECT min(id) FROM "user") + trunc(random() * 10),
        (SELECT min(id) FROM source) + trunc(random() * 5)
    FROM generate_series(1, 20) as iter;

-- Данные для таблицы comment
INSERT INTO comment(text, create_date, edit_date, article_id, user_id)
    SELECT
        'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. ' ||
        'Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus ' ||
        'mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis ' ||
        'enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, ' ||
        'imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt.',
        now() - interval '1 day' * round(random() * 20 + 21) + interval '1 minute' * round(random() * 60)
            + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60),
        now() - interval '1 day' * round(random() * 20) + interval '1 minute' * round(random() * 60)
            + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60),
        (SELECT min(id) FROM article) + trunc(random() * 50),
        (SELECT min(id) FROM "user") + trunc(random() * 10)
    FROM generate_series(1, 20);

-- Данные для таблицы image
INSERT INTO image(title, path, article_id)
    SELECT
        'title_' || iter,
        '/part' || iter || '/_' || md5(random()::text) || '/_' || md5(random()::text) || '/_' || md5(random()::text),
        (SELECT min(id) FROM article) + trunc(random() * 50)
    FROM generate_series(1, 25) as iter;

-- Данные для таблицы log
-- INSERT INTO log(content, "user", action, action_time)
--     SELECT
--         (SELECT min(id) FROM content) + trunc(random() * 12),
--         (SELECT min(id) FROM "user") + trunc(random() * 10),
--         (array['create ', 'delete ', 'update '])[trunc(random() * 3) + 1] || (array['user', 'tag', 'category', 'new',
--             'image', 'page', 'comment', 'log', 'mailing', 'session', 'permission', 'group'])[trunc(random() * 12) + 1],
--         now() - interval '1 day' * round(random() * 50 + 51) + interval '1 minute' * round(random() * 60)
--             + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60)
--     FROM generate_series(1, 150) as iter;

-- Данные для таблицы page
-- INSERT INTO page(title, meta_charset, meta_description, meta_keywords, title_menu, favicon_path, is_published, url, content)
--     SELECT
--         'title_' || iter,
--         'meta_charset_' || iter,
--         'meta_description_' || iter,
--         'meta_keywords_' || iter,
--         'title_menu_' || iter,
--         '/path/' || iter || '/favicon.ico',
--         (array[TRUE, FALSE])[round(random()) + 1],
--         '/url' || iter,
--         (SELECT min(id) FROM content) + trunc(random() * 12)
--     FROM generate_series(1, 25) as iter;

-- Данные для таблицы permission
-- INSERT INTO permission(content, action, permission)
--     SELECT
--         (SELECT min(id) FROM content) + iter - 1,
--         'can create',
--         TRUE
--     FROM generate_series(1, 12) as iter;
-- INSERT INTO permission(content, action, permission)
--     SELECT
--         (SELECT min(id) FROM content) + iter - 1,
--         'can delete',
--         TRUE
--     FROM generate_series(1, 12) as iter;
-- INSERT INTO permission(content, action, permission)
--     SELECT
--         (SELECT min(id) FROM content) + iter - 1,
--         'can update',
--         TRUE
--     FROM generate_series(1, 12) as iter;
-- INSERT INTO permission(content, action, permission)
--     SELECT
--         (SELECT min(id) FROM content) + iter - 1,
--         'can view',
--         TRUE
--     FROM generate_series(1, 12) as iter;

-- Данные для таблицы реализации связи многие-ко-многим new_tag
INSERT INTO article_tag(article_id, tag_id)
    SELECT
        (SELECT min(id) FROM article) + iter - 1,
        (SELECT min(id) FROM tag) + trunc(random() * 9)
    FROM generate_series(1, 25) as iter;
INSERT INTO article_tag(article_id, tag_id)
    SELECT
        (SELECT min(id) FROM article) + iter - 1 + 24,
        (SELECT min(id) FROM tag) + trunc(random() * 9) + 9
    FROM generate_series(1, 25) as iter;

-- Данные для таблицы реализации связи многие-ко-многим group_permission
-- INSERT INTO group_permission("group", permission)
--     SELECT
--         (SELECT min(id) FROM "group"),
--         (SELECT min(id) FROM permission) + iter -1
--     FROM generate_series(1, 48) as iter;
-- INSERT INTO group_permission("group", permission)
--     SELECT
--         (SELECT min(id) FROM "group") + 1,
--         (SELECT min(id) FROM permission) + iter -1
-- FROM generate_series(1, 24) as iter;