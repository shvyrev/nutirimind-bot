package com.nutrimind.service;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class OnboardingServiceTest {

    @Inject
    AnswerValidator answerValidator;


    @Test
    void testRestrictionsValidation() {
        assertTrue(answerValidator.validateRestrictions("аллергия на орехи").await().indefinitely());
        assertTrue(answerValidator.validateRestrictions("непереносимость лактозы").await().indefinitely());
        assertFalse(answerValidator.validateRestrictions("x").await().indefinitely()); // too short
    }

    @Test
    void testEatingHabitsValidation() {
        assertTrue(answerValidator.validateEatingHabits("ем 3 раза в день с перекусами").await().indefinitely());
        assertTrue(answerValidator.validateEatingHabits("2 основных приема пищи").await().indefinitely());
        assertFalse(answerValidator.validateEatingHabits("z").await().indefinitely()); // too short
    }

    @Test
    void testCommunicationStyleValidation() {
        assertTrue(answerValidator.validateCommunicationStyle("строгий наставник").await().indefinitely());
        assertTrue(answerValidator.validateCommunicationStyle("легкий приятель").await().indefinitely());
        assertFalse(answerValidator.validateCommunicationStyle("другой").await().indefinitely());
    }
}