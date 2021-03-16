-- Данные для таблицы mailing
INSERT INTO mailing(email)
    SELECT
        (array['vasya', 'petya', 'atrem', 'maxim', 'kolya', 'lena', 'misha', 'asya'])[ceil(random()*8)] || '_' ||
        SUBSTRING(md5(random()::text) from 1 for ceil(random()*8+5)::integer) || '_' ||
        (array['1990', '1991', '1992', '1993', '1994', '1995', '1996', '1997', '1998'])[ceil(random()*8)] ||
        (array['@mail.ru', '@gmail.com', '@yandex.ru'])[ceil(random()*3)]
    FROM generate_series(1,30);

-- Данные для таблицы session
INSERT INTO session(session_key, session_data, expire_date)
    SELECT
        SUBSTRING(sha256(random()::text::bytea)::text from 3 for 40),
        md5(random()::text) || sha256(random()::text::bytea),
        now() + interval '1 day' * round(random() * 100) + interval '1 minute' * round(random() * 60)
            + interval '1 hour' * round(random() * 24) + interval '1 second' * round(random() * 60)
    FROM generate_series(1, 15);

-- Данные для таблицы content
INSERT INTO content(entity)
    SELECT
        (array['user', 'tag', 'category', 'new', 'image', 'page', 'comment', 'log', 'mailing', 'session', 'permission'])[iter]
    FROM generate_series(1, 11) as iter;

--