# DNSLookup

Программа для отправки DNS-запросов на получение записей A-типа с публичного DNS-сервера.

## Описание

DNSLookup - это Java-приложение, которое позволяет отправлять DNS-запросы на получение IP-адресов для заданных доменных имен с использованием публичного DNS-сервера.

Программа собирает DNS-запрос, отправляет его на указанный DNS-сервер, получает ответ и выводит IP-адрес доменного имени.

## Использование

1. Убедитесь, что у вас установлена Java Development Kit (JDK).
2. Скомпилируйте исходный код с помощью команды `javac DNSLookup.java`.
3. Запустите программу, указав доменное имя в качестве аргумента командной строки. Например:
   ```bash
   java DNSLookup google.com
