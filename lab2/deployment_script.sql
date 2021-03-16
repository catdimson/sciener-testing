-- CREATE DATABASE db_news
--     WITH
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'Russian_Russia.1251'
--     LC_CTYPE = 'Russian_Russia.1251'

-- Создание таблица user
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
    CONSTRAINT user_pk PRIMARY KEY (id),
    CONSTRAINT username_unique UNIQUE (username)
);

-- Создание таблица category
CREATE TABLE IF NOT EXISTS category (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(50) NOT NULL,
    CONSTRAINT category_pk PRIMARY KEY (id)
);

-- Создание таблица tag
CREATE TABLE IF NOT EXISTS tag (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(50) NOT NULL,
    CONSTRAINT tag_pk PRIMARY KEY (id)
);

-- Создание таблица content
CREATE TABLE IF NOT EXISTS content (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    entity character varying(50) NOT NULL,
    CONSTRAINT content_pk PRIMARY KEY (id),
    CONSTRAINT entity_unique UNIQUE (entity)
);

-- Создание таблица mailing
CREATE TABLE IF NOT EXISTS mailing (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    email character varying(80) NOT NULL,
    CONSTRAINT mailing_pk PRIMARY KEY (id),
    CONSTRAINT email_unique UNIQUE (email)
);

-- Создание таблица session
CREATE TABLE IF NOT EXISTS session (
    session_key character varying(40) NOT NULL,
    session_data text NOT NULL,
    expire_date timestamp NOT NULL,
    CONSTRAINT session_key_pk PRIMARY KEY (session_key)
);
CREATE INDEX IF NOT EXISTS session_index ON session (expire_date);

-- Создание таблица new
CREATE TABLE IF NOT EXISTS new (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(250) NOT NULL,
    lead character varying(350) NOT NULL,
    create_date timestamp NOT NULL,
    edit_date timestamp NOT NULL,
    text text NOT NULL,
    is_published boolean DEFAULT false,
    category integer NOT NULL DEFAULT 1,
    "user" integer NOT NULL,
    CONSTRAINT new_pk PRIMARY KEY (id),
    CONSTRAINT fk_category FOREIGN KEY (category)
        REFERENCES category (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_user FOREIGN KEY ("user")
        REFERENCES "user" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS fk_index_category_id ON new (category);
CREATE INDEX IF NOT EXISTS fk_index_new_user_id ON new ("user");

-- Создание таблица image
CREATE TABLE IF NOT EXISTS image (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    title character varying(80) NOT NULL,
    path character varying(500) NOT NULL,
    new integer NOT NULL,
    CONSTRAINT image_pk PRIMARY KEY (id),
    CONSTRAINT fk_new FOREIGN KEY (new)
        REFERENCES new (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_image_new_id ON image (new);

-- Создание таблица page
CREATE TABLE IF NOT EXISTS page (
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
CREATE INDEX IF NOT EXISTS fk_index_page_content_id ON page (content);

-- Создание таблица log
CREATE TABLE IF NOT EXISTS log (
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
CREATE INDEX IF NOT EXISTS fk_index_log_user_id ON log ("user");

-- Создание таблица comment
CREATE TABLE IF NOT EXISTS comment (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    text character varying(3000),
    create_date timestamp NOT NULL,
    edit_date timestamp NOT NULL,
    new integer NOT NULL,
    "user" integer NOT NULL,
    CONSTRAINT comment_pk PRIMARY KEY (id),
    CONSTRAINT fk_new FOREIGN KEY (new)
        REFERENCES new (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY ("user")
        REFERENCES "user" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_comment_new_id ON comment (new);
CREATE INDEX IF NOT EXISTS fk_index_comment_user_id ON comment ("user");

-- Создание таблица permission
CREATE TABLE IF NOT EXISTS permission (
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
CREATE INDEX IF NOT EXISTS fk_index_permission_content_id ON permission (content);

-- Создание таблица user_permission для реализации связи многие-ко-многим
CREATE TABLE IF NOT EXISTS user_permission (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    "user" integer NOT NULL,
    permission integer NOT NULL,
    CONSTRAINT user_permission_pk PRIMARY KEY (id),
    CONSTRAINT user_permission_unique UNIQUE ("user", permission),
    CONSTRAINT fk_user FOREIGN KEY ("user")
        REFERENCES "user" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_permission FOREIGN KEY (permission)
        REFERENCES permission (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_user_permission_user_id ON user_permission ("user");
CREATE INDEX IF NOT EXISTS fk_index_user_permission_permission_id ON user_permission (permission);

-- Создание таблица new_tag для реализации связи многие-ко-многим
CREATE TABLE IF NOT EXISTS new_tag (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    new integer NOT NULL,
    tag integer NOT NULL,
    CONSTRAINT new_tag_pk PRIMARY KEY (id),
    CONSTRAINT new_tag_unique UNIQUE (new, tag),
    CONSTRAINT fk_new FOREIGN KEY (new)
        REFERENCES new (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_tag FOREIGN KEY (tag)
        REFERENCES tag (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS fk_index_new_tag_new_id ON new_tag (new);
CREATE INDEX IF NOT EXISTS fk_index_new_tag_tag_id ON new_tag (tag);
