package api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.api.CreateTaskRequest;
import model.api.Task;
import model.api.UpdateTaskRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class TaskApiClient {

    private static final Logger log = LoggerFactory.getLogger(TaskApiClient.class);
    private static final String BASE_URL = "https://todo-be-production-0bb9.up.railway.app";

    private final RequestSpecification spec;

    public TaskApiClient() {
        spec = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType(ContentType.JSON)
            .addFilter(new AllureRestAssured())
            .build();
    }

    @Step("GET /tasks")
    public List<Task> getAllTasks() {
        log.info("GET /tasks");
        Task[] tasks = given().spec(spec)
            .when()
            .get("/tasks")
            .then()
            .statusCode(200)
            .extract().as(Task[].class);
        log.info("Retrieved {} tasks", tasks.length);
        return Arrays.asList(tasks);
    }

    @Step("POST /tasks")
    public Task createTask(CreateTaskRequest request) {
        log.info("POST /tasks - text: '{}'", request.getText());
        Task task = given().spec(spec)
            .body(request)
            .when()
            .post("/tasks")
            .then()
            .statusCode(200)
            .extract().as(Task.class);
        log.info("Created task: {}", task.getId());
        return task;
    }

    @Step("POST /tasks/{id} - update")
    public Task updateTask(String id, UpdateTaskRequest request) {
        log.info("POST /tasks/{} - text: '{}'", id, request.getText());
        Task task = given().spec(spec)
            .pathParam("id", id)
            .body(request)
            .when()
            .post("/tasks/{id}")
            .then()
            .statusCode(200)
            .extract().as(Task.class);
        log.info("Updated task: {}", task);
        return task;
    }

    @Step("DELETE /tasks/{id}")
    public void deleteTask(String id) {
        log.info("DELETE /tasks/{}", id);
        given().spec(spec)
            .pathParam("id", id)
            .when()
            .delete("/tasks/{id}")
            .then()
            .statusCode(200);
        log.info("Deleted task: {}", id);
    }

    @Step("GET /tasks/completed")
    public List<Task> getCompletedTasks() {
        log.info("GET /tasks/completed");
        Task[] tasks = given().spec(spec)
            .when()
            .get("/tasks/completed")
            .then()
            .statusCode(200)
            .extract().as(Task[].class);
        log.info("Retrieved {} completed tasks", tasks.length);
        return Arrays.asList(tasks);
    }

    @Step("POST /tasks/{id}/complete")
    public Task completeTask(String id) {
        log.info("POST /tasks/{}/complete", id);
        Task task = given().spec(spec)
            .pathParam("id", id)
            .when()
            .post("/tasks/{id}/complete")
            .then()
            .statusCode(200)
            .extract().as(Task.class);
        log.info("Completed task: {}", id);
        return task;
    }

    @Step("POST /tasks/{id}/incomplete")
    public Task incompleteTask(String id) {
        log.info("POST /tasks/{}/incomplete", id);
        Task task = given().spec(spec)
            .pathParam("id", id)
            .when()
            .post("/tasks/{id}/incomplete")
            .then()
            .statusCode(200)
            .extract().as(Task.class);
        log.info("Incompleted task: {}", id);
        return task;
    }

    public Response createTaskRaw(CreateTaskRequest request) {
        log.info("POST /tasks (raw) - text: '{}'", request.getText());
        return given().spec(spec)
            .body(request)
            .when()
            .post("/tasks");
    }

    public Response deleteTaskRaw(String id) {
        log.info("DELETE /tasks/{} (raw)", id);
        return given().spec(spec)
            .pathParam("id", id)
            .when()
            .delete("/tasks/{id}");
    }
}
