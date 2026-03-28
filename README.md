## Feature: комментарии к событиям

### Обзор
Ключевые свойства:
- Комментарий можно создать, только для опубликованного события.
- Комментарий можно редактировать.
- Комментарий можно удалить.

### Бизнес-правила
- Целевое событие должно существовать и находиться в состоянии `PUBLISHED`.
- Текст комментария:
  - не должен быть пустым
  - длина: `1..2000`

  
### Роли и права
- **Public API**
  - Может читать комментарии к событию.
- **Private API**
  - Может получить свой комментарий для редактирования.
  - Может создавать комментарии к опубликованным событиям.
  - Может редактировать/удалять только свои комментарии.
- **Admin API**
  - Может удалять любой комментарий. 

### REST API

#### Public
- `GET /events/{eventId}/comments?from=0&size=10`

#### Private
- `POST /users/{userId}/events/{eventId}/comments`
- `PATCH /users/{userId}/comments/{commentId}`
- `DELETE /users/{userId}/comments/{commentId}`
- `GET /users/{userId}/events/{eventId}/comments/{commentId}`

#### Admin
- `DELETE /admin/comments/{commentId}`

### Модель данных
Новая таблица:
- `comments`
  - `id` (PK)
  - `event_id` (FK -> `events`)
  - `author_id` (FK -> `users`)
  - `text`
  - `created`
  - `edited`

### DTO
DTO ответа:
- `id`
- `eventId`
- `authorId`
- `text`
- `created`
- `edited`

DTO - редактирование и создание:
- `id`
- `text`

### Postman-тесты
+ добавить детальное описание тестов...
+ тесты на ресты -> существует и выдают 200
+ тесты на валидацию дто
+ функциональные тесты
