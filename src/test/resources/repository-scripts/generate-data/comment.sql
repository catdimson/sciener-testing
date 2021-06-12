INSERT INTO comment (text, create_date, edit_date, user_id, article_id) VALUES
    ('Текст 1', to_timestamp(1561410000), to_timestamp(1561410000), 1, 1),
    ('Текст 2', to_timestamp(1561410000), to_timestamp(1561410000), 2, 1),
    ('Текст 3', to_timestamp(1561410000), to_timestamp(1561410000), 1, 1);

INSERT INTO attachment (title, path, comment_id) VALUES
    ('Прикрепление 1', '/static/attachments/attachment1.png', 1),
    ('Прикрепление 2', '/static/attachments/attachment2.png', 2);
