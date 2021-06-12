INSERT INTO "user" (password, username, first_name, last_name, email, last_login, date_joined, is_superuser, is_staff,
                    is_active, group_id) VALUES
    ('password111', 'user111', 'Александр', 'Колесников', 'mail111@mail.ru', to_timestamp(1563000000),
     to_timestamp(1560000000), true, true, true, 1),
    ('password222', 'user222', 'Александр', 'Вениаминов', 'mail222@mail.ru', to_timestamp(1563000000),
     to_timestamp(1560000000), true, true, false, 1),
    ('password333', 'user333', 'Максим', 'Шаповалов', 'mail333@mail.ru', to_timestamp(1563000000),
     to_timestamp(1560000000), false, false, false, 2);
