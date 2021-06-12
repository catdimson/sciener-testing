DROP TABLE "user" CASCADE;
DROP TABLE "group" CASCADE;
DROP TABLE afisha CASCADE;
DROP TABLE source CASCADE;

CREATE TABLE "group" (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(40) NOT NULL,
    CONSTRAINT group_pk PRIMARY KEY (id),
    CONSTRAINT title_unique_group UNIQUE (title)
);

INSERT INTO "group" (title)
    SELECT
        (array['admin', 'editor', 'seo', 'guest'])[iter]
    FROM generate_series(1, 4) as iter;

CREATE TABLE IF NOT EXISTS "user"  (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    password character varying(128) NOT NULL,
    username character varying(150) NOT NULL,
    first_name character varying(150) NOT NULL,
    last_name character varying(150),
    email character varying(254) NOT NULL,
    last_login timestamp NOT NULL,
    date_joined timestamp NOT NULL,
    is_superuser boolean NOT NULL DEFAULT false,
    is_staff boolean NOT NULL DEFAULT false,
    is_active boolean NOT NULL DEFAULT true,
    group_id integer NOT NULL,
    CONSTRAINT user_pk PRIMARY KEY (id),
    CONSTRAINT username_unique UNIQUE (username),
    CONSTRAINT fk_user_group_id FOREIGN KEY (group_id)
        REFERENCES "group" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

INSERT INTO "user" (password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff, is_active, group_id)
    VALUES
        ('qwerty12', 'alex', 'Александр', 'Колесников', 'alex1993@mail.ru', to_timestamp(1589922000), to_timestamp(1558299600), false, true, true, 2),
        ('qwerty000', 'max', 'Максим', 'Вердилов', 'maxiver@mail.ru', to_timestamp(1589922000), to_timestamp(1558299600), false, true, true, 2);

CREATE TABLE IF NOT EXISTS source (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(50) NOT NULL,
    url character varying(500) NOT NULL,
    CONSTRAINT source_pk PRIMARY KEY (id)
);

INSERT INTO source (title, url) VALUES ('Яндекс ДЗЕН', 'https://zen.yandex.ru/'), ('РИА', 'https://ria.ru/');

CREATE TABLE IF NOT EXISTS afisha (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(250) NOT NULL,
    image_url character varying(500),
    lead character varying(350) NOT NULL,
    description text NOT NULL,
    age_limit character varying(5),
    timing character varying(15),
    place character varying(300),
    phone character varying(20),
    date timestamp,
    is_commercial boolean NOT NULL DEFAULT false,
    user_id integer NOT NULL,
    source_id integer,
    CONSTRAINT afisha_pk PRIMARY KEY (id),
    CONSTRAINT fk_source FOREIGN KEY (source_id)
        REFERENCES source (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES "user" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS fk_index_source_id ON afisha (source_id);
CREATE INDEX IF NOT EXISTS fk_index_source_user_id ON afisha (user_id);
