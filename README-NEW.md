## **Технологии, использованные в Rangiffler 1.0**


# Запуск Rangiffler в докере:

#### 1. Создать бесплатную учетную запись на https://hub.docker.com/ (если отсутствует)

#### 2. Создать в настройках своей учетной записи access_token

[Инструкция](https://docs.docker.com/docker-hub/access-tokens/).

#### 3. Выполнить docker login с созданным access_token (в инструкции это описано)

#### 4. Прописать в etc/hosts элиас для Docker-имени
```posh
User-MacBook-Pro rangiffler % vi /etc/hosts
```

```posh
##
# Host Database
#
# localhost is used to configure the loopback interface
# when the system is booting.  Do not change this entry.
##
- auth:       127.0.0.1       auth.rangiffler.dc
- gateway:    127.0.0.1       gateway.rangiffler.dc
- frontend:   127.0.0.1       frontend.rangiffler.dc
```

#### 5. Перейти в корневой каталог проекта

```posh
User-MacBook-Pro rangiffler % cd rangiffler
```

#### 6. Запустить все сервисы

```posh
User-MacBook-Pro  rangiffler % bash docker-compose-dev.sh
```

Текущая версия `docker-compose-dev.sh` **удалит все запущенные Docker контейнеры в системе**, поэтому если у вас есть
созданные
контейнеры для других проектов - отредактируйте строку ```posh docker rm $(docker ps -a -q)```, чтобы включить в grep
только те контейнеры, что непосредственно относятся к rangiffler.

- Фронтенд Rangiffler при запуске в докере будет работать для вас по адресу http://frontend.rangiffler.dc
- GraphiQL интерфейс сервиса rangiffler-gateway доступен по адресу: http://gateway.rangiffler.dc:8080/graphiql (не работает без VPN в РФ)

# Ошибки
1. **Если при выполнении скрипта docker-compose-dev.sh вы получили ошибку**
```
* What went wrong:
Execution failed for task ':rangiffler-auth:jibDockerBuild'.
> com.google.cloud.tools.jib.plugins.common.BuildStepsExecutionException: 
Build to Docker daemon failed, perhaps you should make sure your credentials for 'registry-1.docker.io...
```

То необходимо убедиться, что в `$USER/.docker/config.json` файле отсутствует запись `"credsStore": "desktop"`
При наличии такого ключа в json, его надо удалить.
Если файл пустой, то возможно не выполнен `docker login`. Если выполнялся, то надо создать файл руками по пути
`$USER/.docker/config.json`
с содержимым,

```
 {
        "auths": {
                "https://index.docker.io/v1/": {}
        },
        "currentContext": "desktop-linux"
}
```

2. **Если вы не можете подключиться к БД в docker, указывая верные login и password**, то возможно у вас поднята другая база на
том же порту 5432.
Это известная проблема, что **mysql** в docker может стартануть при занятом порту 5432, надо убедиться что у вас не
поднят никакой другой **mysql** на этом порту.

3. **Если вы используете Windows и контейнер с БД не стартует с ошибкой в логе:**

```
server started
/usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/init-database.sh
/usr/local/bin/docker-entrypoint.sh: /docker-entrypoint-initdb.d/init-database.sh: /bin/bash^M: bad interpreter: No such file or directory
```

То необходимо выполнить следующие команды в каталоге **/mysql** :
```posh
sed -i -e 's/\r$//' init-database.sh
chmod +x init-database.sh
```