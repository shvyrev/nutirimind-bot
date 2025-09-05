package com.nutrimind.service;

import com.nutrimind.model.Achievement;
import com.nutrimind.model.User;
import com.nutrimind.model.UserAchievement;
import com.nutrimind.model.enums.AchievementType;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AchievementService {

    private final TelegramBotService telegramBotService;

    public Uni<UserAchievement> awardAchievement(User user, Achievement achievement) {
        return UserAchievement.find("user = ?1 and achievement = ?2", user, achievement)
            .firstResult()
            .onItem().transform(entity -> (UserAchievement) entity)
            .onItem().ifNull().switchTo(() -> {
                UserAchievement ua = new UserAchievement();
                ua.user = user;
                ua.achievement = achievement;
                ua.earnedAt = LocalDateTime.now();
                return ua.store();
            })
            .chain(saved -> notifyUserAboutAchievement(user, achievement)
                .chain(() -> Uni.createFrom().item(saved)));
    }

    public Uni<List<UserAchievement>> getUserAchievements(User user) {
        return UserAchievement.findByUserId(user.id);
    }

    public Uni<List<Achievement>> getAchievementsByType(AchievementType type) {
        return Achievement.findByType(type);
    }

    public Uni<Achievement> findAchievementByName(String name) {
        return Achievement.findByName(name);
    }

    public Uni<Boolean> hasAchievement(User user, Achievement achievement) {
        return UserAchievement.find("user = ?1 and achievement = ?2", user, achievement)
            .firstResult()
            .onItem().transform(result -> result != null)
            .onFailure().recoverWithItem(false);
    }

    public Uni<Void> awardOnboardingAchievement(User user) {
        return findAchievementByName("Первый шаг")
            .onItem().ifNotNull().transformToUni(achievement ->
                awardAchievement(user, achievement)
                    .onItem().ignore().andContinueWithNull()
            )
            .onFailure().recoverWithNull();
    }

    public Uni<Void> awardStreakAchievement(User user, int streakDays) {
        if (streakDays >= 7) {
            return findAchievementByName("Неделя дисциплины")
                .onItem().ifNotNull().transformToUni(achievement -> 
                    awardAchievement(user, achievement)
                        .onItem().ignore().andContinueWithNull()
                );
        } else if (streakDays >= 30) {
            return findAchievementByName("Месяц регулярности")
                .onItem().ifNotNull().transformToUni(achievement -> 
                    awardAchievement(user, achievement)
                        .onItem().ignore().andContinueWithNull()
                );
        }
        return Uni.createFrom().nullItem();
    }

    private Uni<Void> notifyUserAboutAchievement(User user, Achievement achievement) {
        String message = String.format("""
            🏆 Поздравляем! Вы получили достижение!
            
            %s: %s
            %s
            
            +%d очков к вашему рейтингу!
            """, 
            achievement.name, 
            achievement.description,
            achievement.icon != null ? achievement.icon : "⭐",
            achievement.pointsValue != null ? achievement.pointsValue : 10
        );

        return telegramBotService.sendMessage(user.telegramId, message)
            .onFailure().recoverWithNull();
    }
}