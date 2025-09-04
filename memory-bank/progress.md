# Обновление доменной модели NutriMind

## Дата: 2025-09-04
## Выполненные работы

### Созданы enum-типы:
- `UserState` (ONBOARDING, ACTIVE, PAUSED, CHURNED)
- `CommunicationStyle` (MENTOR, BUDDY) 
- `ActivityLevel` (SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTREMELY_ACTIVE)
- `HealthGoal` (WEIGHT_LOSS, MUSCLE_GAIN, MAINTENANCE, HEALTH_IMPROVEMENT)
- `MealType` (BREAKFAST, LUNCH, DINNER, SNACK)
- `ChallengeType` (NUTRITION, EXERCISE, MINDFULNESS, HABIT)
- `DifficultyLevel` (EASY, MEDIUM, HARD)
- `ChallengeStatus` (NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED)
- `AchievementType` (STREAK, CHALLENGE_COMPLETION, NUTRITION_GOAL, COMMUNITY)
- `TipType` (MOTIVATIONAL, EDUCATIONAL, MINDFULNESS, BEHAVIORAL)

### Обновленные сущности:
- **User** - добавлены state, communicationStyle, lastActiveAt, связи с профилем и другими сущностями
- **FoodEntry** - расширена эмоциональным контекстом (moodBefore, moodAfter, wasHungry, eatingContext)
- **Challenge** - обновлена с enum-типами и дополнительными полями
- **PsychologicalTip** - обновлена с enum-типами

### Новые сущности:
- **UserProfile** - детальный профиль пользователя с диетическими предпочтениями
- **NutritionPlan** - персонализированный план питания с рекомендациями
- **ChallengeProgress** - отслеживание прогресса по челленджам
- **Achievement** - система достижений
- **UserAchievement** - связь пользователей с достижениями

### Удаленные сущности:
- UserGoal (устаревшая)
- UserChallenge (заменена на ChallengeProgress)

### Миграция базы данных:
Создана полная SQL-миграция `V1.0.0__nutrimind.sql` включающая:
- Все таблицы с правильными типами данных
- Check constraints для enum-значений
- Внешние ключи для всех связей
- Последовательности для ID

### Технические изменения:
- Обновлен `pom.xml` с добавлением зависимостей Flyway и JDBC PostgreSQL
- Настроена конфигурация Flyway в `application.properties`

Новая доменная модель полностью соответствует требованиям и готова к использованию.