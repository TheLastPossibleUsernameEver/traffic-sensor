# TrafficSensor #
Spark Streaming data limiter in Java

## Подробная инструкция по установке и конфигурированию ##
Примечание: Используется Linux-дистрибутив Debian 9 (Stretch) amd64

### I. Установка Docker и запуск CDH for Docker ###
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

