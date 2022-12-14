package commons.models;

import commons.entities.Activity;
import commons.utils.CompareType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ComparisonQuestionTest {
    private static final long POSITIVE = 8;
    private static final long TOTAL = 589;

    private ComparisonQuestion question;
    private Activity firstActivity;
    private Activity secondActivity;

    @BeforeEach
    public void startup() {
        firstActivity = new Activity("q1", POSITIVE, "src");
        secondActivity = new Activity("q2", POSITIVE + POSITIVE, "src");
        question = new ComparisonQuestion(firstActivity, secondActivity);
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(question);
    }

    @Test
    public void defaultConstructorConstructsValidObject() {
        assertNotNull(new ComparisonQuestion());
    }

    @Test
    public void getFirstActivityReturnsFirstActivity() {
        assertEquals(firstActivity, question.getFirstActivity());
    }

    @Test
    public void setFirstActivitySetsFirstActivity() {
        question.setFirstActivity(secondActivity);
        assertEquals(secondActivity, question.getFirstActivity());
    }

    @Test
    public void getSecondActivityReturnsSecondActivity() {
        assertEquals(secondActivity, question.getSecondActivity());
    }

    @Test
    public void setSecondActivitySetsSecondActivity() {
        question.setSecondActivity(firstActivity);
        assertEquals(firstActivity, question.getSecondActivity());
    }

    @Test
    public void getUserAnswerReturnsUserAnswer() {
        assertEquals(new Answer(CompareType.EMPTY), question.getUserAnswer());
    }

    @Test
    public void setUserAnswerChangesUserAnswer() {
        question.setUserAnswer(new Answer(CompareType.EQUAL), POSITIVE);
        assertEquals(CompareType.EQUAL, question.getUserAnswer().generateAnswer());
    }

    @Test
    public void getPointsReturnsPoints() {
        question.setUserAnswer(new Answer(CompareType.SMALLER), POSITIVE);
        assertEquals(TOTAL, question.calculatePoints());
    }

    @Test
    public void equalsReturnsFalseIfDifferentType() {
        assertNotEquals("str", question);
    }

    @Test
    public void equalsReturnsFalseIfDifferentFields() {
        assertNotEquals(new ComparisonQuestion(secondActivity, firstActivity), question);
    }

    @Test
    public void equalsReturnsTrueIfEqualFields() {
        ComparisonQuestion expected = new ComparisonQuestion(firstActivity, secondActivity);
        assertEquals(expected, question);
    }

    @Test
    public void generateCorrectAnswerReturnsNotNull() {
        assertNotNull(question.generateCorrectAnswer());
    }

    @Test
    public void generateQuestionTextReturnsQuestionText() {
        assertEquals("Which one consumes more energy?", question.generateQuestionText());
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertEquals(
                String.format("ComparisonQuestion{firstActivity=%s,secondActivity=%s}",
                        question.getFirstActivity().toString(),
                        question.getSecondActivity().toString()), question.toString()
        );
    }

    @Test
    public void equalsReturnsTrueIfSame() {
        assertEquals(question, question);
    }

    @Test
    public void hashCodeReturnsSameForSameFields() {
        assertEquals(question.hashCode(), question.hashCode());
    }
}
