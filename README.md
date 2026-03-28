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
- todo ??
  
### Роли и права
- **Public API**
  - Может читать комментарии к событию.
- **Private API**
  - Может создавать комментарии к опубликованным событиям.
  - Может редактировать/удалять только свои комментарии.
- **Admin API**
  - Может искать комментарии по фильтрам. - ? 
  - Может удалять любой комментарий.

### REST API

#### Public
- `GET /events/{eventId}/comments?from=0&size=10`

#### Private
- `POST /users/{userId}/events/{eventId}/comments`
- `PATCH /users/{userId}/comments/{commentId}`
- `DELETE /users/{userId}/comments/{commentId}`

#### Admin
- `GET /admin/comments?eventId&authorId&text&rangeStart&rangeEnd&from=0&size=10`
- `DELETE /admin/comments/{commentId}`

### Модель данных
Новая таблица:
- `comments`
  - `id` (PK)
  - `event_id` (FK -> `events`)
  - `author_id` (FK -> `users`)
  - `text`
  - `created_on`
  - `edited_on`

### DTO
DTO ответа:
- `id`
- `eventId`
- `authorId`
- `text`
- `createdOn`
- `editedOn`

### Postman-тесты
+ добавить детальное описание тестов...
+ тесты на ресты -> существует и выдают 200
+ тесты на валидацию дто
+ функциональные тесты
to be continued...