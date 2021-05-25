# 1. В pg_hba.conf добавить строку host all all 0.0.0.0/0 md5
# 2. Убедиться, что в postgresql.conf установлено listen_addresses = '*'
# 3. Билд образа по Dockerfile командой docker docker build -t new_app .
# 4. Запуск образа командой:
# docker run -e DB_USER="you_username" -e DB_PASSWORD="you_password" -e DB_URL="you_url" --name App_new -p 5000:5000 -d new_app
# где you_username, you_password, you_url - логин, пароль, url БД подключения
FROM adoptopenjdk/openjdk11:ubi
RUN mkdir /opt/app_new
WORKDIR /opt/app_new
COPY target/uber-blg_kotik_dmitry-1.0.jar /opt/app_new
CMD java -jar uber-blg_kotik_dmitry-1.0.jar ${DB_USER} ${DB_PASSWORD} ${DB_URL}