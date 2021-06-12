DROP TABLE article_tag CASCADE;
DROP TABLE article CASCADE;
DROP TABLE "user" CASCADE;
DROP TABLE category CASCADE;
DROP TABLE source CASCADE;
DROP TABLE "group" CASCADE;
DROP TABLE tag CASCADE;
DROP TABLE image CASCADE;

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

CREATE TABLE IF NOT EXISTS category (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(50) NOT NULL,
    CONSTRAINT category_pk PRIMARY KEY (id),
    CONSTRAINT title_unique_category UNIQUE (title)
);

INSERT INTO category (title) VALUES ('Спорт'), ('Политика');

CREATE TABLE IF NOT EXISTS tag (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(50) NOT NULL,
    CONSTRAINT tag_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS article (
id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
title character varying(250) NOT NULL,
lead character varying(350) NOT NULL,
create_date timestamp NOT NULL,
edit_date timestamp NOT NULL,
text text NOT NULL,
is_published boolean DEFAULT false,
category_id integer NOT NULL DEFAULT 1,
user_id integer NOT NULL,
source_id integer,
CONSTRAINT article_pk PRIMARY KEY (id),
CONSTRAINT fk_category FOREIGN KEY (category_id)
    REFERENCES category (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
CONSTRAINT fk_user FOREIGN KEY (user_id)
    REFERENCES "user" (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
CONSTRAINT fk_source FOREIGN KEY (source_id)
    REFERENCES source (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE RESTRICT);
CREATE INDEX IF NOT EXISTS fk_index_category_id ON article (category_id);
CREATE INDEX IF NOT EXISTS fk_index_article_user_id ON article (user_id);
CREATE INDEX IF NOT EXISTS fk_index_source_id ON article (source_id);

CREATE TABLE IF NOT EXISTS image (
id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
title character varying(80) NOT NULL,
path character varying(500) NOT NULL,
article_id integer NOT NULL,
CONSTRAINT image_pk PRIMARY KEY (id),
CONSTRAINT fk_article FOREIGN KEY (article_id)
    REFERENCES article (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_image_article_id ON image (article_id);

CREATE TABLE IF NOT EXISTS article_tag (
id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
article_id integer NOT NULL,
tag_id integer NOT NULL,
CONSTRAINT article_tag_pk PRIMARY KEY (id),
CONSTRAINT article_tag_unique UNIQUE (article_id, tag_id),
CONSTRAINT fk_new FOREIGN KEY (article_id)
    REFERENCES article (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE,
CONSTRAINT fk_tag FOREIGN KEY (tag_id)
    REFERENCES tag (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE);
CREATE INDEX IF NOT EXISTS fk_index_new_tag_article_id ON article_tag (article_id);
CREATE INDEX IF NOT EXISTS fk_index_new_tag_tag_id ON article_tag (tag_id);