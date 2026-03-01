package api;

import extensions.junit.ApiExtension;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import model.api.CreateTaskRequest;
import model.api.Task;
import model.api.UpdateTaskRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApiExtension.class)
@Feature("Todo Task API")
public class TaskApiTest {

    private static final Logger log = LoggerFactory.getLogger(TaskApiTest.class);

    private final TaskApiClient api = new TaskApiClient();

    @Test
    @Story("Get tasks")
    @Description("Retrieve all tasks and verify 200 response with non-null list")
    @DisplayName("GET /tasks returns task list")
    void getAllTasksReturnsList() {
        List<Task> tasks = api.getAllTasks();
        assertNotNull(tasks, "Task list should not be null");
        log.info("Task list contains {} items", Optional.of(tasks.size()));
    }

    @Test
    @Story("Create task")
    @Description("Create a new task and verify returned object has correct id, text and defaults")
    @DisplayName("POST /tasks creates task with correct data")
    void createTaskReturnsCorrectData() {
        String text = "API test task - " + System.currentTimeMillis();
        Task created = api.createTask(new CreateTaskRequest(text));

        try {
            assertAll(
                () -> assertNotNull(created.getId(), "id should not be null"),
                () -> assertFalse(created.getId().isBlank(), "id should not be blank"),
                () -> assertEquals(text, created.getText(), "text should match the request"),
                () -> assertFalse(created.isCompleted(), "new task should not be completed"),
                () -> assertTrue(created.getCreatedDate() > 0, "createdDate should be set"),
                () -> assertNull(created.getCompletedDate(), "completedDate should be null for new task")
            );
        } finally {
            cleanUp(created.getId());
        }
    }

    @Test
    @Story("Update task")
    @Description("Update text of an existing task and verify the change is reflected in the response")
    @DisplayName("POST /tasks/{id} updates task text")
    void updateTaskTextReturnsUpdatedData() {
        Task created = api.createTask(new CreateTaskRequest("Original - " + System.currentTimeMillis()));

        try {
            String updatedText = "Updated - " + System.currentTimeMillis();
            Task updated = api.updateTask(created.getId(), new UpdateTaskRequest(updatedText));

            assertAll(
                () -> assertEquals(created.getId(), updated.getId(), "id should not change after update"),
                () -> assertEquals(updatedText, updated.getText(), "text should reflect the update"),
                () -> assertFalse(updated.isCompleted(), "completed flag should not change on text update")
            );
        } finally {
            cleanUp(created.getId());
        }
    }

    @Test
    @Story("Delete task")
    @Description("Delete an existing task and verify it no longer appears in the task list")
    @DisplayName("DELETE /tasks/{id} removes task from list")
    void deleteTaskRemovesItFromList() {
        Task created = api.createTask(new CreateTaskRequest("To be deleted - " + System.currentTimeMillis()));

        api.deleteTask(created.getId());

        List<Task> tasks = api.getAllTasks();
        boolean stillPresent = false;
        for (Task t : tasks) {
            if (t.getId().equals(created.getId())) {
                stillPresent = true;
                break;
            }
        }
        assertFalse(stillPresent, "Deleted task should not appear in GET /tasks response");
    }

    @Test
    @Story("Complete task")
    @Description("Mark a task as complete and verify completed=true and completedDate is populated")
    @DisplayName("POST /tasks/{id}/complete sets completed flag and date")
    void completeTaskSetsCompletedFlagAndDate() {
        Task created = api.createTask(new CreateTaskRequest("To complete - " + System.currentTimeMillis()));

        try {
            Task completed = api.completeTask(created.getId());

            assertAll(
                () -> assertTrue(completed.isCompleted(), "completed should be true after /complete"),
                () -> assertNotNull(completed.getCompletedDate(), "completedDate should be set after /complete"),
                () -> assertTrue(completed.getCompletedDate() > 0, "completedDate should be a positive timestamp")
            );
        } finally {
            cleanUp(created.getId());
        }
    }

    @Test
    @Story("Incomplete task")
    @Description("Re-open a completed task and verify completed=false and completedDate is cleared")
    @DisplayName("POST /tasks/{id}/incomplete clears completed flag")
    void incompleteTaskClearsCompletedFlag() {
        Task created = api.createTask(new CreateTaskRequest("To incomplete - " + System.currentTimeMillis()));

        try {
            api.completeTask(created.getId());
            Task reopened = api.incompleteTask(created.getId());

            assertAll(
                () -> assertFalse(reopened.isCompleted(), "completed should be false after /incomplete"),
                () -> assertNull(reopened.getCompletedDate(), "completedDate should be null after /incomplete")
            );
        } finally {
            cleanUp(created.getId());
        }
    }

    @Test
    @Story("Get completed tasks")
    @Description("Retrieve completed tasks and verify every task in the list has completed=true")
    @DisplayName("GET /tasks/completed returns only completed tasks")
    void getCompletedTasksReturnsOnlyCompleted() {
        Task created = api.createTask(new CreateTaskRequest("Completed task - " + System.currentTimeMillis()));
        api.completeTask(created.getId());

        try {
            List<Task> completedTasks = api.getCompletedTasks();
            assertFalse(completedTasks.isEmpty(), "completed tasks list should not be empty");
            for (Task t : completedTasks) {
                assertTrue(t.isCompleted(), "Every task in GET /tasks/completed must have completed=true");
            }
        } finally {
            cleanUp(created.getId());
        }
    }

    @Test
    @Story("Create task - validation")
    @Description("Attempt to create a task with empty text — API should return 422")
    @DisplayName("POST /tasks with empty text returns 422")
    void createTaskWithEmptyTextReturns422() {
        int status = api.createTaskRaw(new CreateTaskRequest(""))
            .then().extract().statusCode();
        assertEquals(422, status, "Empty text should be rejected with 422");
    }

    @Test
    @Story("Delete task - validation")
    @Description("Attempt to delete a task with a non-existent ID — API should return 404")
    @DisplayName("DELETE /tasks/{id} with unknown id returns 404")
    void deleteNonExistentTaskReturns400() {
        int status = api.deleteTaskRaw("non-existent-id-" + System.currentTimeMillis())
            .then().extract().statusCode();
        assertEquals(404, status, "Deleting a non-existent task should return 404");
    }

    @Step("Clean up: delete task {id}")
    private void cleanUp(String id) {
        try {
            api.deleteTask(id);
        } catch (Exception e) {
            log.warn("Cleanup failed for task id={}: {}", id, e.getMessage());
        }
    }
}
