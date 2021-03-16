-- Удаление всех данных из таблиц
DELETE FROM mailing;
DELETE FROM session;
DELETE FROM image;
DELETE FROM new_tag;
DELETE FROM comment;
DELETE FROM group_permission;
DELETE FROM page;
DELETE FROM log;
DELETE FROM new;
DELETE FROM category;
DELETE FROM tag;
DELETE FROM "user";
DELETE FROM permission;
DELETE FROM content;
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
INSERT INTO session(session_key, session_data, expire_date)
    SELECT
        SUBSTRING(sha256(random()::text::bytea)::text from 3 for 40),
        md5(random()::text) || sha256(random()::text::bytea),
        now() + interval '1 day' * round(random() * 100) + interval '1 hour' * round(random() * 24)
            + interval '1 minute' * round(random() * 60) + interval '1 second' * round(random() * 60)
    FROM generate_series(1, 15);

-- Данные для таблицы content
INSERT INTO content(entity)
    SELECT
        (array['user', 'tag', 'category', 'new', 'image', 'page', 'comment', 'log', 'mailing', 'session',
        'permission', 'group'])[iter]
    FROM generate_series(1, 12) as iter;

-- Данные для таблицы категорий
INSERT INTO category(title)
    SELECT
        (array['спорт', 'политика', 'кинематограф', 'искусство', 'экономика', 'наука', 'музыка'])[iter]
    FROM generate_series(1, 7) as iter;

-- Данные для таблицы тэгов
INSERT INTO tag(title)
    SELECT
        (array['ufc', 'футбол', 'хоккей', 'внешняя политика', 'внутренняя политика', 'конфликт', 'премьеры фильмов',
            'зарубежные фильмы', 'театр', 'балет', 'внешняя экономика', 'внутренняя экономика', 'кризис',
            'гаджеты', 'IT', 'программирование', 'поп-музыка', 'концерты'])[iter]
    FROM generate_series(1, 18) as iter;

-- Данные для таблицы групп
INSERT INTO "group"(title)
    SELECT
        (array['admin', 'editor', 'seo', 'guest'])[iter]
    FROM generate_series(1, 4) as iter;

-- Данные для таблицы пользователей
INSERT INTO "user"(password, username, first_name, last_name, email, "group", last_login, date_joined, is_superuser,
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

-- Данные для таблицы новостей
INSERT INTO new(title, lead, create_date, edit_date, text, is_published, category, "user")
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

-- Данные для таблицы комментариев
INSERT INTO comment(text, create_date, edit_date, new, "user")
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
        (SELECT min(id) FROM new) + trunc(random() * 50),
        (SELECT min(id) FROM "user") + trunc(random() * 10)
    FROM generate_series(1, 150);

-- Данные для таблицы изображений
INSERT INTO image(title, path, new)
    SELECT
        'title_' || iter,
        '/part' || iter || '/_' || md5(random()::text) || '/_' || md5(random()::text) || '/_' || md5(random()::text),
        (SELECT min(id) FROM new) + trunc(random() * 50)
    FROM generate_series(1, 80) as iter;

-- Данные для таблицы логов
-- INSERT INTO log(content, user, action, action_time)
--     SELECT
--         'title_' || iter,
--         '/part' || iter || '/_' || md5(random()::text) || '/_' || md5(random()::text) || '/_' || md5(random()::text),
--         (SELECT min(id) FROM new) + trunc(random() * 50)
--     FROM generate_series(1, 80) as iter;