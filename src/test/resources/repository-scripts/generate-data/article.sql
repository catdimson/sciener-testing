INSERT INTO article (title, lead, create_date, edit_date, text, is_published, category_id, user_id, source_id) VALUES
        ('Заголовок 1', 'Лид 1', to_timestamp(1561410000), to_timestamp(1561410000), 'Текст 1', true, 1, 1, 1),
        ('Заголовок 1', 'Лид 2', to_timestamp(1561410000), to_timestamp(1561410000), 'Текст 2', true, 2, 2, 2),
        ('Заголовок 3', 'Лид 3', to_timestamp(1561410000), to_timestamp(1561410000), 'Текст 3', true, 2, 2, 2);

INSERT INTO image (title, path, article_id) VALUES
        ('Изображение 1', '/static/images/image1.png', 1),
        ('Изображение 2', '/static/images/image2.png', 2);

INSERT INTO tag (title) VALUES ('Тег 1'), ('Тег 2');

INSERT INTO article_tag (article_id, tag_id) VALUES (1, 1), (2, 2);