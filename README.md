# TrafficSensor #
Spark Streaming data limiter in Java

## Подробная инструкция по установке и конфигурированию ##
Примечание: На сервере используется Debian 9 (Stretch) amd64
Примечание 2: В контейнере используется CentOS 6.6

### Установка Docker и запуск CDH for Docker ###
#### Зайдите в командную строку и установите containerd.io ####
``` $ wget https://download.docker.com/linux/debian/dists/stretch/pool/stable/amd64/containerd.io_1.2.6-3_amd64.deb ```
 
``` $ dpkg -i containerd.io_1.2.6-3_amd64.deb```

#### Установите docker-ce ####

``` $ wget https://download.docker.com/linux/debian/dists/stretch/pool/stable/amd64/docker-ce_18.09.8~3-0~debian-stretch_amd64.deb ```

``` $ dpkg -i docker-ce_18.09.8~3-0~debian-stretch_amd64.deb  ```

#### Установите docker-cli ####

``` $ wget https://download.docker.com/linux/debian/dists/stretch/pool/stable/amd64/docker-ce-cli_19.03.0~3-0~debian-stretch_amd64.deb ```

``` $ dpkg -i docker-ce-cli_19.03.0~3-0~debian-stretch_amd64.deb```

#### Проверьте корректность установки запуском образа "hello-world" ####

``` $ docker run hello-world ```

#### Выгрузите образ CDH с Docker Hub

``` $ docker pull cloudera/quickstart:latest ```

#### Запустите Docker-образ

``` $ docker run -dit --privileged=true --hostname=quickstart.cloudera <image_hash> /usr/bin/docker-quickstart ``` 
*image_hash - хеш импортированного образа

### Клонирование репозитория и сборка проекта

#### Клонируйте репозиторий в текущую директорию

``` $ git clone https://github.com/TheLastPossibleUsernameEver/traffic-sensor ```

#### Скопируйте репозиторий в Docker-container

``` $ docker cp traffic-sensor <хеш-контейнера>:/root/ ```

*Хеш-контейнера можно узнать командой ```docker ps```

#### Зайдите в контейнер

``` $ docker attach <хеш-контейнер> ```

#### Удалите стандартную для CDH Java 7

``` root@quickstart.cloudera$ rm -rf /usr/java/cloudera* ```

#### Установите Java 8

``` root@quickstart.cloudera$ yum install java-1.8.0-openjdk ```
``` root@quickstart.cloudera$ yum install java-1.8.0-openjdk-devel```

#### Установите libpcap

``` root@quickstart.cloudera$ yum install libpcap-devel ```

#### Зайдите в директорию проекта

``` root@quickstart.cloudera$ cd traffic-sensor ```

#### Соберите проект

``` root@quickstart.cloudera$ mvn package```

#### Если Maven не установлен, то установите его

``` root@quickstart.cloudera$ cd /opt ```
``` root@quickstart.cloudera$ wget https://www-eu.apache.org/dist/maven/maven-3/3.6.2/binaries/apache-maven-3.6.2-bin.tar.gz ```
``` root@quickstart.cloudera$ ln -s apache-maven-3.6.2 maven ```
``` root@quickstart.cloudera$ vi /etc/profile.d/maven.sh ```
Перейдите в режим вставки, нажав i и добавьте следующее:

``` export M2_HOME=/opt/maven ```
``` export PATH=${M2_HOME}/bin:${PATH} ```

#### Выйдите из vi

Если вы не знаете, как выйти из vi, то нажмите Esc , затем наберите :wq и нажмите Enter

#### Проиндексируйте изменения

``` root@quickstart.cloudera$ source /etc/profile.d/maven.sh ```

### Установка Kafka

#### Скачайте tar-архив с Kafka

``` root@quickstart.cloudera$ wget http://www-us.apache.org/dist/kafka/2.3.0/kafka_2.11-2.3.0.tgz ```

#### Распакуйте его в текущую директорию

``` root@quickstart.cloudera$ tar -xvzf kafka_2.11-2.3.0.tgz  ```

#### Переместите появившуюся директорию в /opt

``` root@quickstart.cloudera$ mv kafka_2.11-2.3.0 /opt```

#### Установите и запустите tmux

``` root@quickstart.cloudera $ yum install tmux  ```
``` root@quickstart.cloudera $ tmux ```

#### Размножьте окно в 3 окна 

Ctrl-b + %

#### Перейдите во 2 окно

Ctrl-b + Right-Arrow или Left-Arrow 
 
#### Zookeeper запускать не нужно, т.к. его запуск инициализируется скриптом /usr/bin/docker-quickstart

#### Измените JAVA_HOME

``` root@quickstart.cloudera $ vi /etc/profile ```
Найдите строчку с export JAVA_HOME и замените на
``` export JAVA_HOME=/usr/lib/java-1.8.0-openjdk-1.8.0.222.b10-0.el6_10.x86_64 ```
Сохраните и выйдите: Esc, :wq, Enter.

#### Запустите Kafka-server

``` root@quickstart.cloudera$ cd/opt/kafka-2.11-2.3.0 ```
``` bin/kafka-server-start.sh config/server.properties ```

Во всех трёх окнах нужно будет набрать: 
``` source /etc/profile ```

#### Перейдите в первое окно

#### Запустите приложение

``` root@quickstart.cloudera$ cd /root/traffic-sensor/target ```
``` root@quickstart.cloudera$ java -jar uber-traffic-sensor-1.0-SNAPSHOT.jar ```

#### Перейдите в третье окно

#### Запустите Kafka-topics

``` root@quickstart.cloudera$ cd /opt/kafka_2.11-2.3.0 ```

``` root@quickstart.cloudera$ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic alerts --from-beginning```

#### В окне с Kafka-topics при превышении установленных лимитов траффика за 1 минуту будет отображаться сообщение "You have a new alert!"
