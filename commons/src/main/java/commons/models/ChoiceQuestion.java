package commons.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import commons.entities.Activity;
import commons.utils.QuestionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonTypeName(value = "choice")
public class ChoiceQuestion extends Question {
    private static final long TRUE_FACTOR = 500;
    private static final long TIME_FACTOR = 800;

    private Activity comparedActivity;
    private List<Activity> activities;
    private Activity answer;

    @SuppressWarnings("unused")
    public ChoiceQuestion() {
        super(QuestionType.CHOICE);
        // for object mapper
    }

    /**
     * Constructs a choice question (what will you do instead of...?)
     * from a list of activities
     * @param activities the list of activities this question
     *                   is based on
     */
    public ChoiceQuestion(List<Activity> activities) {
        super(QuestionType.CHOICE);
        setActivities(activities);
        this.userAnswer = new Answer((Activity) null);
    }

    /**
     * Sets the answer to the question as the activity
     * with the minimal consumption
     * @param activities the activities this question
     *                   is based on
     */
    private void loadAnswer(List<Activity> activities) {
        long minConsumption = Integer.MAX_VALUE;
        for(Activity a : activities) {
            if(a.consumption < minConsumption) {
                answer = a;
                minConsumption = a.consumption;
            }
        }
    }

    /**
     * sets the answer
     * @param answer
     */

    public void setAnswer(Activity answer) {
        this.answer = answer;
    }

    /**
     * gets the incorrect Activities in a list
     * @return list of Activity
     */
    public List<Activity> getIncorrectActivities(){
        ArrayList<Activity> incorrectActivities = new ArrayList<>();
        for(Activity a: activities){
            if(!a.equals(answer) && !a.equals(comparedActivity)){
                incorrectActivities.add(a);
            }
        }
        Collections.shuffle(incorrectActivities);
        return incorrectActivities;
    }

    /**
     * Returns the activity the question is based on
     * @return the activity the question is based on
     */
    public Activity getComparedActivity() {
        return comparedActivity;
    }

    /**
     * Sets the activity this question is based on
     * @param activities the activities the question
     *                   is based on, from which the compared
     *                   activity is chosen as the second
     *                   maximal
     */
    private void loadComparedActivity(List<Activity> activities) {
        long minConsumption = Integer.MAX_VALUE;
        for(Activity a : activities) {
            if(a.consumption < minConsumption && a != answer) {
                comparedActivity = a;
                this.imagePath = comparedActivity.imagePath;
                minConsumption = a.consumption;
            }
        }
    }

    public void setComparedActivity(Activity activity) {
        this.comparedActivity = activity;
    }

    /**
     * Returns the question answer
     * @return question correct answer
     */
    public Activity getAnswer() {
        return answer;
    }


    /**
     * Returns the activities this question is based on
     * @return the activities this question is based on
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * Sets the activities this question is based on
     * @param activities the activities the question
     *                   is based on
     */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
        loadAnswer(activities);
        loadComparedActivity(activities);
    }

    /**
     * Calculates the points based on whether
     * the user's answer is correct and the time
     * it took them to answer the question
     * @return the current answer points
     */
    @Override
    public long calculatePoints() {
        return ((hasCorrectUserAnswer() ? 1 : 0) * Math.round(TRUE_FACTOR + TIME_FACTOR / (seconds + 1)));
    }

    /**
     * Returns true if the answer chosen by the user
     * is the correct option for this multiple-choice
     * question
     * @return true if user answer chosen is correct
     */
    @Override
    public boolean hasCorrectUserAnswer() {
        if (userAnswer == null || userAnswer.generateAnswer() == null) {
            return false;
        }

        return answer.title.equals(((Activity) userAnswer.generateAnswer()).title);
    }

    /**
     * Returns a string representation of the given object
     * @return a string representation of the given object
     */
    @Override
    public String toString() {
        return "ChoiceQuestion{" +
                "activities=" + activities +
                '}';
    }

    /**
     * Checks whether another object is of the same instance
     * and has fields equal to the given object
     * @param o the object to compare the current object with
     * @return true of the two objects are the same or are of
     * the same instance and have equal fields
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ChoiceQuestion that = (ChoiceQuestion) o;
        return comparedActivity.equals(that.comparedActivity) &&
                activities.equals(that.activities) && answer.equals(that.answer);
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), comparedActivity, activities, answer);
    }

    /**
     * Returns the text representation of the question
     * @return the text representation of the question
     */
    @Override
    public String generateQuestionText(){
         return String.format("What could you do instead of %s to consume less energy?",
                getComparedActivity().title);
    }

    /**
     * Returns an answer object corresponding to the correct answer to this question
     * @return an answer object corresponding to the correct answer to this question
     */
    @Override
    public Answer generateCorrectAnswer(){
        return new Answer(answer);
    }
}
