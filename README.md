## **Технологии, использованные в Rangiffler 1.0**
- [Spring Authorization Server](https://spring.io/projects/spring-authorization-server)
- [Spring OAuth 2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Spring data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Web](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web)
- [Spring actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Spring gRPC by https://github.com/yidongnan](https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html)
- [Spring web-services](https://docs.spring.io/spring-ws/docs/current/reference/html/)
- [Apache Kafka](https://developer.confluent.io/quickstart/kafka-docker/)
- [Docker](https://www.docker.com/resources/what-container/)
- [Docker-compose](https://docs.docker.com/compose/)
- [Postgres](https://www.postgresql.org/about/)
- [React](https://ru.reactjs.org/docs/getting-started.html)
- [GraphQL](https://graphql.org/)
- [JUnit 6 (Extensions, Resolvers, etc)](https://docs.junit.org/6.0.3/overview.html)
- [Retrofit 2](https://square.github.io/retrofit/)
- [Allure](https://docs.qameta.io/allure/)
- [Selenide](https://selenide.org/)
- [Selenoid & Selenoid-UI](https://aerokube.com/selenoid/latest/)
- [Allure-docker-service](https://github.com/fescobar/allure-docker-service)
- [Java 21](https://adoptium.net/en-GB/temurin/releases/)
- [Gradle 9.0](https://docs.gradle.org/9.0.0/release-notes.html)
- И многие другие

# Готовность диплома
#### 1. ✅ BE
#### 2. ✅ Unit тесты
| Сервис    | количество |
|-----------|------------|
| auth      | 8          |
| gateway   | 23         |
| geo       | 3          |
| photo     | 26         |
| userdata  | 36         |
| **всего** | **96**     |
#### 3. ❌ GraphQL тесты
#### 4. ❌ UI тесты
#### 5. ✅ Инфра докер
#### 6. ✅ Kafka

# Что хотелось бы реализовать но не хватило времени?
#### 1. Хранение фото в MinIO
#### 2. Кафка фото и лайки
#### 3. Github actions
#### 4. Prod deploy

# Запуск Rangiffler в докере:

#### 0. Докер bash скрипты
- можно запускать из документации README.md
- имеют документацию **-h** (help).

Пример вызова документации:
```sh
  bash some-docker-script.sh -h
```

#### 1. Создать бесплатную учетную запись на https://hub.docker.com/ (если отсутствует)

#### 2. Создать в настройках своей учетной записи access_token

[Инструкция](https://docs.docker.com/docker-hub/access-tokens/).

#### 3. Выполнить docker login с созданным access_token (в инструкции это описано)
```sh
  vi /etc/hosts # MacOs
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

```sh
  cd rangiffler # MacOs
```

#### 6. Запустить все сервисы

```sh
  bash docker-compose-dev.sh # MacOs
```

Текущая версия `docker-compose-dev.sh` **удалит все запущенные Docker контейнеры в системе**, поэтому если у вас есть
созданные
контейнеры для других проектов - отредактируйте строку ```posh docker rm $(docker ps -a -q)```, чтобы включить в grep
только те контейнеры, что непосредственно относятся к rangiffler.

- Фронтенд Rangiffler при запуске в докере будет работать для вас по адресу http://frontend.rangiffler.dc
- GraphiQL интерфейс сервиса rangiffler-gateway доступен по адресу: http://gateway.rangiffler.dc:8080/graphiql (в РФ работает с VPN )

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

```json
{
    "auths": {
        "https://index.docker.io/v1/": {}
    },
    "currentContext": "desktop-linux"
}
```

2. **Если вы не можете подключиться к БД в docker, указывая верные login и password**, то возможно у вас поднята другая база на
том же порту 3306.
Это известная проблема, что **mysql** в docker может стартануть при занятом порту 3306, надо убедиться что у вас не
поднят никакой другой **mysql** на этом порту.

3. **Если вы используете Windows и контейнер с БД не стартует с ошибкой в логе:**

```posh
server started
/usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/init-database.sh
/usr/local/bin/docker-entrypoint.sh: /docker-entrypoint-initdb.d/init-database.sh: /bin/bash^M: bad interpreter: No such file or directory
```

То необходимо выполнить следующие команды в каталоге **/mysql** :
```sh
  sed -i -e 's/\r$//' init-database.sh # MacOs
  chmod +x init-database.sh
```

# Запуск локального тестового окружения 'Selenoid / Allure-docker-server' в докере:
```sh
  bash docker-compose-tests-local-env.sh # MacOs
```
UI будет доступен по ссылке
- Selenoid UI http://127.0.0.1:9091/#/
- Allure UI http://127.0.0.1:5252/
# Запуск e-2-e тестов в Docker network изолированно Rangiffler в докере:

#### 1. Перейти в корневой каталог проекта

```sh
  cd rangiffler # MacOs
```

#### 2. Запустить все сервисы и тесты:

```sh
  bash docker-compose-e2e.sh # MacOs
```

#### 3. Selenoid UI доступен по адресу: http://localhost:9090/

#### 5. Allure-ui доступен по адресу: http://localhost:5252/

#### 4. Allure report доступен по адресу: http://localhost:5050/allure-docker-service/projects/niffler-ng/reports/latest/index.html

<img src="/rangiffler-gql-client/src/assets/deer-logo.svg" width="250">
