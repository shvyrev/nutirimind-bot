package com.nutrimind.service;

import com.nutrimind.model.OnboardingState;
import com.nutrimind.model.OnboardingStep;
import com.nutrimind.model.User;
import com.nutrimind.model.UserProfile;
import com.nutrimind.model.enums.ActivityLevel;
import com.nutrimind.model.enums.HealthGoal;
import com.nutrimind.model.enums.OnboardingStepType;
import com.nutrimind.model.enums.UserState;
import com.nutrimind.service.NutritionCalculator.NutritionPlan;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class OnboardingService {
    private static final List<OnboardingStep> ONBOARDING_STEPS = List.of(
        new OnboardingStep(1, OnboardingStepType.WELCOME, "Добро пожаловать в NutriMind! Давай настроим твой персональный план питания."),
        new OnboardingStep(2, OnboardingStepType.PERSONAL_INFO, "Расскажи, пожалуйста, твой возраст, рост и вес? (формат: возраст рост вес, например: 30 175 70)"),
        new OnboardingStep(3, OnboardingStepType.HEALTH_GOAL, "Какая у тебя цель: похудеть, набрать мышечную массу или просто питаться сбалансированно?"),
        new OnboardingStep(4, OnboardingStepType.ACTIVITY_LEVEL, "Как часто ты занимаешься спортом? Выбери вариант:\n1. Почти не занимаюсь\n2. 1-2 раза в неделю\n3. 3-4 раза в неделю\n4. 5+ раз в неделю"),
        new OnboardingStep(5, OnboardingStepType.DIET_PREFERENCES, "Есть ли у тебя предпочтения в питании? (например: вегетарианство, палео, кето)"),
        new OnboardingStep(6, OnboardingStepType.RESTRICTIONS, "Есть ли у тебя пищевые ограничения или аллергии?"),
        new OnboardingStep(7, OnboardingStepType.EATING_HABITS, "Расскажи о своих пищевых привычках: сколько раз в день обычно ешь, есть ли перекусы?"),
        new OnboardingStep(8, OnboardingStepType.COMMUNICATION_STYLE, "Выбери стиль общения: строгий наставник или легкий приятель?"),
        new OnboardingStep(9, OnboardingStepType.COMPLETION, "Отлично! Собираю всю информацию для твоего плана питания...")
    );

    private final TelegramBotService telegramBotService;

    private final StateManager stateManager;

    private final AchievementService achievementService;

    @Inject
    AnswerValidator answerValidator;

    @Inject
    NutritionCalculator nutritionCalculator;

    public Uni<User> startOnboarding(User user) {
        return stateManager.transitionToState(user, UserState.ONBOARDING)
            .chain(updatedUser -> initOnboardingState(updatedUser)
                .chain(state -> askQuestion(updatedUser, getStepByType(OnboardingStepType.WELCOME))));
    }

    public Uni<User> processAnswer(User user, String answer) {
        return OnboardingState.findByUserId(user.id)
            .onItem().ifNull().failWith(() -> new IllegalStateException("OnboardingState not found for user: " + user.id))
            .chain(state -> validateAndSaveAnswer(state, answer)
                .chain(updatedState -> askNextQuestion(user, updatedState)));
    }

    private Uni<User> askNextQuestion(User user, OnboardingState state) {
        OnboardingStepType nextStep = getNextStep(state.currentStep);
        if (nextStep != OnboardingStepType.COMPLETION) {
            return askQuestion(user, getStepByType(nextStep));
        } else {
            return completeOnboarding(user, state);
        }
    }

    private Uni<User> askQuestion(User user, OnboardingStep step) {
        return telegramBotService.sendMessage(user.telegramId, step.question())
            .chain(() -> stateManager.transitionToState(user, UserState.AWAITING_ANSWER));
    }

    private Uni<User> completeOnboarding(User user, OnboardingState state) {
        state.completed = true;
        return state.store()
            .chain(() -> createUserProfileFromAnswers(state))
            .chain(profile -> calculateNutritionPlan(profile))
            .chain(calculatorPlan -> createNutritionPlanEntity(calculatorPlan, user))
            .chain(plan -> plan.persistAndFlush())
            .chain(plan -> {
                user.setNutritionPlan(plan);
                return user.persistAndFlush();
            })
            .chain(updatedUser -> stateManager.transitionToState(updatedUser, UserState.ACTIVE))
            .chain(updatedUser -> {
                com.nutrimind.model.NutritionPlan plan = updatedUser.getNutritionPlan();
                String completionMessage = """
                    🎉 Отлично! Твой персональный план готов!
                    
                    📊 Твои дневные цели:
                    - Калории: %.0f ккал
                    - Белки: %.0f г
                    - Жиры: %.0f г
                    - Углеводы: %.0f г
                    
                    Начнем твое путешествие к здоровому питанию!
                    """.formatted(
                    plan.getDailyCalories(),
                    plan.getProteinTarget(),
                    plan.getFatTarget(),
                    plan.getCarbsTarget()
                );
                return telegramBotService.sendMessage(updatedUser.telegramId, completionMessage)
                    .call(() -> achievementService.awardOnboardingAchievement(updatedUser))
                    .chain(() -> Uni.createFrom().item(updatedUser));
            });
    }

    public boolean isOnboardingComplete(User user) {
        return user.state == UserState.ACTIVE;
    }

    private Uni<OnboardingState> initOnboardingState(User user) {
        return OnboardingState.findByUserId(user.id)
            .onItem().ifNull().switchTo(() -> {
                OnboardingState newState = new OnboardingState();
                newState.user = user;
                newState.currentStep = OnboardingStepType.WELCOME;
                newState.answers = new HashMap<>();
                newState.completed = false;
                return newState.store();
            });
    }

    private Uni<OnboardingState> validateAndSaveAnswer(OnboardingState state, String answer) {
        return validateAnswer(state.currentStep, answer)
            .chain(isValid -> {
                if (!isValid) {
                    return Uni.createFrom().failure(new IllegalArgumentException("Invalid answer for step: " + state.currentStep));
                }
                if (state.answers == null) {
                    state.answers = new HashMap<>();
                }
                state.answers.put(state.currentStep.name(), answer);
                state.currentStep = getNextStep(state.currentStep);
                return state.store();
            });
    }

    private Uni<Boolean> validateAnswer(OnboardingStepType step, String answer) {
        return switch (step) {
            case PERSONAL_INFO -> answerValidator.validateAgeHeightWeight(answer);
            case HEALTH_GOAL -> answerValidator.validateHealthGoal(answer);
            case ACTIVITY_LEVEL -> answerValidator.validateActivityLevel(answer);
            case COMMUNICATION_STYLE -> answerValidator.validateCommunicationStyle(answer);
            case DIET_PREFERENCES -> answerValidator.validateDietPreferences(answer);
            case RESTRICTIONS -> answerValidator.validateRestrictions(answer);
            case EATING_HABITS -> answerValidator.validateEatingHabits(answer);
            default -> Uni.createFrom().item(true);
        };
    }

    private OnboardingStepType getNextStep(OnboardingStepType currentStep) {
        OnboardingStepType[] steps = OnboardingStepType.values();
        int currentIndex = currentStep.ordinal();
        if (currentIndex < steps.length - 1) {
            return steps[currentIndex + 1];
        }
        return OnboardingStepType.COMPLETION;
    }

    private OnboardingStep getStepByType(OnboardingStepType type) {
        return ONBOARDING_STEPS.stream()
            .filter(step -> step.type() == type)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Step not found for type: " + type));
    }

    private Uni<UserProfile> createUserProfileFromAnswers(OnboardingState state) {
        return Uni.createFrom().item(() -> {
            UserProfile profile = new UserProfile();
            profile.setUser(state.user);
            
            // Parse personal info
            String personalInfo = (String) state.answers.get(OnboardingStepType.PERSONAL_INFO.name());
            String[] parts = personalInfo.split(" ");
            profile.setAge(Integer.parseInt(parts[0]));
            profile.setHeight(Double.parseDouble(parts[1]));
            profile.setWeight(Double.parseDouble(parts[2]));
            
            // Parse other answers
            profile.setHealthGoal(parseHealthGoal((String) state.answers.get(OnboardingStepType.HEALTH_GOAL.name())));
            profile.setActivityLevel(parseActivityLevel((String) state.answers.get(OnboardingStepType.ACTIVITY_LEVEL.name())));
            
            // TODO: Parse other fields from answers
            return profile;
        }).chain(profile -> profile.persistAndFlush());
    }

    private Uni<NutritionPlan> calculateNutritionPlan(UserProfile profile) {
        return nutritionCalculator.calculatePlan(profile);
    }

    private Uni<com.nutrimind.model.NutritionPlan> createNutritionPlanEntity(NutritionPlan calculatorPlan, User user) {
        return Uni.createFrom().item(() -> {
            com.nutrimind.model.NutritionPlan plan = new com.nutrimind.model.NutritionPlan();
            plan.setUser(user);
            plan.setDailyCalories(calculatorPlan.dailyCalories());
            plan.setProteinTarget(calculatorPlan.proteinTarget());
            plan.setFatTarget(calculatorPlan.fatTarget());
            plan.setCarbsTarget(calculatorPlan.carbsTarget());
            return plan;
        });
    }

    private HealthGoal parseHealthGoal(String answer) {
        String lower = answer.toLowerCase();
        if (lower.contains("похуд")) return HealthGoal.WEIGHT_LOSS;
        if (lower.contains("набор") || lower.contains("мыш")) return HealthGoal.MUSCLE_GAIN;
        if (lower.contains("сбаланс")) return HealthGoal.MAINTENANCE;
        return HealthGoal.HEALTH_IMPROVEMENT;
    }

    private ActivityLevel parseActivityLevel(String answer) {
        String clean = answer.replaceAll("[^0-9]", "");
        if (!clean.isEmpty()) {
            int times = Integer.parseInt(clean);
            if (times == 0) return ActivityLevel.SEDENTARY;
            if (times <= 2) return ActivityLevel.LIGHTLY_ACTIVE;
            if (times <= 4) return ActivityLevel.MODERATELY_ACTIVE;
            if (times <= 6) return ActivityLevel.VERY_ACTIVE;
        }
        return ActivityLevel.EXTREMELY_ACTIVE;
    }
}