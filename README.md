# Анализатор логов

## Параметры 

- `u` - Минимально допустимый уровень доступности (проценты) (например, "99.9")
- `t` - Приемлемое время ответа (миллисекунды) (например, "45")

## Запуск

1. Собрать `mvn package`.
2. Скопировать файл `/target/app.jar` в нужную директорию.
3. Использовать `cat access.log | java -jar app.jar -u 99.9 -t 45`.

## Пример результата выполнения
```text
16:47:02 16:47:27 74.91594
16:47:29 16:47:36 90.58116
16:47:39 16:48:01 85.92162
16:48:03 16:48:16 86.65158
16:48:18 16:48:18 81.521736
16:48:20 16:48:22 90.860214
16:48:24 16:48:29 89.07767
16:48:33 16:48:39 92.84165
16:48:41 16:48:48 87.350426
16:48:50 16:48:52 92.02128
```