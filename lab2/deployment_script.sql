-- CREATE DATABASE db_news
--     WITH
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'Russian_Russia.1251'
--     LC_CTYPE = 'Russian_Russia.1251'

-- Создание таблица user
CREATE TABLE IF NOT EXISTS "group" (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(40) NOT NULL,
    CONSTRAINT group_pk PRIMARY KEY (id),
    CONSTRAINT title_unique_group UNIQUE (title)
);

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

-- Создание таблица category
CREATE TABLE IF NOT EXISTS category (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(50) NOT NULL,
    CONSTRAINT category_pk PRIMARY KEY (id),
    CONSTRAINT title_unique_category UNIQUE (title)
);

-- Создание таблицы source
CREATE TABLE IF NOT EXISTS source (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(50) NOT NULL,
    url character varying(500) NOT NULL,
    CONSTRAINT source_pk PRIMARY KEY (id)
);

-- Создание таблица tag
CREATE TABLE IF NOT EXISTS tag (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(50) NOT NULL,
    CONSTRAINT tag_pk PRIMARY KEY (id)
);

-- Создание таблица content
/*CREATE TABLE IF NOT EXISTS content (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    entity character varying(50) NOT NULL,
    CONSTRAINT content_pk PRIMARY KEY (id),
    CONSTRAINT entity_unique UNIQUE (entity)
);*/

-- Создание таблица mailing
CREATE TABLE IF NOT EXISTS mailing (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    email character varying(80) NOT NULL,
    CONSTRAINT mailing_pk PRIMARY KEY (id),
    CONSTRAINT email_unique UNIQUE (email)
);

-- Создание таблица session
/*CREATE TABLE IF NOT EXISTS session (
    session_key character varying(40) NOT NULL,
    session_data text NOT NULL,
    expire_date timestamp NOT NULL,
    CONSTRAINT session_key_pk PRIMARY KEY (session_key)
);
CREATE INDEX IF NOT EXISTS session_index ON session (expire_date);*/

-- Создание таблица article
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
    source_id integer NOT NULL,
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
        ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS fk_index_category_id ON article (category_id);
CREATE INDEX IF NOT EXISTS fk_index_article_user_id ON article (user_id);
CREATE INDEX IF NOT EXISTS fk_index_source_id ON article (source_id);

-- Создание таблица afisha
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
    source_id integer NOT NULL,

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

-- Создание таблица image
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

-- Создание таблица page
/*CREATE TABLE IF NOT EXISTS page (
     id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
     title character varying(80) NOT NULL,
     meta_charset character varying(350) NOT NULL,
     meta_description character varying(350) NOT NULL,
     meta_keywords character varying(350) NOT NULL,
     title_menu character varying(80) NOT NULL,
     favicon_path character varying(250) NOT NULL,
     is_published boolean DEFAULT false,
     url character varying(250),
     content integer NOT NULL,
     CONSTRAINT page_pk PRIMARY KEY (id),
     CONSTRAINT url_unique UNIQUE (url),
     CONSTRAINT fk_content FOREIGN KEY (content)
         REFERENCES content (id) MATCH SIMPLE
         ON UPDATE CASCADE
         ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_page_content_id ON page (content);*/

-- Создание таблица log
/*CREATE TABLE IF NOT EXISTS log (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    content integer NOT NULL,
    "user" integer NOT NULL,
    action character varying(50) NOT NULL,
    action_time timestamp NOT NULL,
    CONSTRAINT log_pk PRIMARY KEY (id),
    CONSTRAINT fk_content FOREIGN KEY (content)
        REFERENCES content (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY ("user")
        REFERENCES "user" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_log_content_id ON log (content);
CREATE INDEX IF NOT EXISTS fk_index_log_user_id ON log ("user");*/

-- Создание таблица comment
CREATE TABLE IF NOT EXISTS comment (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    text character varying(3000),
    create_date timestamp NOT NULL,
    edit_date timestamp NOT NULL,
    article_id integer NOT NULL,
    user_id integer NOT NULL,
    CONSTRAINT comment_pk PRIMARY KEY (id),
    CONSTRAINT fk_article FOREIGN KEY (article_id)
        REFERENCES article (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES "user" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_comment_new_id ON comment (article_id);
CREATE INDEX IF NOT EXISTS fk_index_comment_user_id ON comment (user_id);

-- Создание таблица attachment
CREATE TABLE IF NOT EXISTS attachment (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(80) NOT NULL,
    path character varying(500) NOT NULL,
    comment_id integer NOT NULL,
    CONSTRAINT attachment_pk PRIMARY KEY (id),
    CONSTRAINT fk_comment FOREIGN KEY (comment_id)
        REFERENCES comment (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_attachment_comment_id ON attachment (comment_id);

-- Создание таблица permission
/*CREATE TABLE IF NOT EXISTS permission (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    content integer NOT NULL,
    action character varying(50) NOT NULL,
    permission boolean DEFAULT false,
    CONSTRAINT permission_pk PRIMARY KEY (id),
    CONSTRAINT fk_content FOREIGN KEY (content)
        REFERENCES content (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_permission_content_id ON permission (content);*/

-- Создание таблица group_permission для реализации связи многие-ко-многим
/*CREATE TABLE IF NOT EXISTS group_permission (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    "group" integer NOT NULL,
    permission integer NOT NULL,
    CONSTRAINT group_permission_pk PRIMARY KEY (id),
    CONSTRAINT group_permission_unique UNIQUE ("group", permission),
    CONSTRAINT fk_group FOREIGN KEY ("group")
        REFERENCES "group" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_permission FOREIGN KEY (permission)
        REFERENCES permission (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_group_permission_user_id ON group_permission ("group");
CREATE INDEX IF NOT EXISTS fk_index_group_permission_permission_id ON group_permission (permission);*/

-- Создание таблица new_tag для реализации связи многие-ко-многим
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
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_new_tag_article_id ON article_tag (article_id);
CREATE INDEX IF NOT EXISTS fk_index_new_tag_tag_id ON article_tag (tag_id);
