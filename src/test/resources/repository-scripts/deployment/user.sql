DROP TABLE "user" CASCADE;
DROP TABLE "group" CASCADE;

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