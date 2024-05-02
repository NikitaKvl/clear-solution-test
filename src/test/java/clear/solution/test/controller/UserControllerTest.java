package clear.solution.test.controller;

import clear.solution.test.dto.UserRequestDTO;
import clear.solution.test.entity.User;
import clear.solution.test.exception.InvalidAgeException;
import clear.solution.test.exception.UserNotFoundException;
import clear.solution.test.mapper.UserMapper;
import clear.solution.test.service.UserService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void createUser() {
        UserRequestDTO userToSave = new UserRequestDTO("jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "3800000000");
        when(userService.createUser(userMapper.mapToUser(userToSave))).thenReturn(
                new User(1L, "jane.doe@example.org", "Jane", "Doe",
                        LocalDate.of(1997, 07, 13), "42 Main St", "3800000000"));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToSave)
                .when()
                .post("/users")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("email", equalTo("jane.doe@example.org"))
                .body("firstName", equalTo("Jane"))
                .body("lastName", equalTo("Doe"))
                .body("birthDate", equalTo("1997-07-13"))
                .body("address", equalTo("42 Main St"))
                .body("phoneNumber", equalTo("3800000000"));
    }

    @Test
    public void createUser_badRequestWhenInvalidEmail() {
        UserRequestDTO userToSave = new UserRequestDTO("jane.doe", "Jane", "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "3800000000");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToSave)
                .when()
                .post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    public void createUser_badRequestWhenFirstNameIsBlank() {
        UserRequestDTO userToSaveWithEmptyValue = new UserRequestDTO("jane.doe@gmail.com", " ", "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "3800000000");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToSaveWithEmptyValue)
                .when()
                .post("/users")
                .then()
                .statusCode(400);

        UserRequestDTO userToSaveWithNullValue = new UserRequestDTO("jane.doe@gmail.com", null, "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "3800000000");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToSaveWithNullValue)
                .when()
                .post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    public void createUser_badRequestWhenLastNameIsBlank() {
        UserRequestDTO userToSaveWithEmptyValue = new UserRequestDTO("jane.doe@gmail.com", "Jane", " ",
                LocalDate.of(1997, 07, 13), "42 Main St", "3800000000");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToSaveWithEmptyValue)
                .when()
                .post("/users")
                .then()
                .statusCode(400);

        UserRequestDTO userToSaveWithNullValue = new UserRequestDTO("jane.doe@gmail.com", "Jane", null,
                LocalDate.of(1997, 07, 13), "42 Main St", "3800000000");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToSaveWithNullValue)
                .when()
                .post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    public void createUser_badRequestWhenBirthDateIsNull() {
        UserRequestDTO userToSave = new UserRequestDTO("jane.doe@gmail.com", "Jane", "Doe",
                null, "42 Main St", "3800000000");

        when(userService.createUser(userMapper.mapToUser(userToSave))).thenThrow(InvalidAgeException.class);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToSave)
                .when()
                .post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    public void createUser_badRequest_WhenUserIsYounger() {
        UserRequestDTO userToSave = new UserRequestDTO("jane.doe@gmail.com", "Jane", "Doe",
                LocalDate.now().minusDays(1L), "42 Main St", "3800000000");

        when(userService.createUser(userMapper.mapToUser(userToSave))).thenThrow(InvalidAgeException.class);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToSave)
                .when()
                .post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    public void updateUserFields() {
        Map<String, Object> fieldsToUpdate = Map.of("email", "jane.doe@example.org", "firstName", "Jane", "LastName", "Doe",
                "birthDate", LocalDate.of(1997, 07, 13).toString(), "address", "42 Main St", "phoneNumber", "6625550144");
        when(userService.updateUserFields(1L, fieldsToUpdate)).thenReturn(
                new User(1L, "jane.doe@example.org", "Jane", "Doe",
                        LocalDate.of(1997, 07, 13), "42 Main St", "6625550144"));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(fieldsToUpdate)
                .when()
                .patch("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("email", equalTo("jane.doe@example.org"))
                .body("firstName", equalTo("Jane"))
                .body("lastName", equalTo("Doe"))
                .body("birthDate", equalTo("1997-07-13"))
                .body("address", equalTo("42 Main St"))
                .body("phoneNumber", equalTo("6625550144"));
    }

    @Test
    public void updateUser() {
        UserRequestDTO userToUpdate = new UserRequestDTO("jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "6625550144");
        when(userService.updateUser(1L, userMapper.mapToUser(userToUpdate))).thenReturn(
                new User(1L, "jane.doe@example.org", "Jane", "Doe",
                        LocalDate.of(1997, 07, 13), "42 Main St", "6625550144"));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToUpdate)
                .when()
                .put("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("email", equalTo("jane.doe@example.org"))
                .body("firstName", equalTo("Jane"))
                .body("lastName", equalTo("Doe"))
                .body("birthDate", equalTo("1997-07-13"))
                .body("address", equalTo("42 Main St"))
                .body("phoneNumber", equalTo("6625550144"));
    }

    @Test
    public void updateUser_BadRequestWhenInvalidEmail() {
        UserRequestDTO userToUpdateWithInvalidEmail = new UserRequestDTO("jane.doe", "Jane", "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "6625550144");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToUpdateWithInvalidEmail)
                .when()
                .put("/users/1")
                .then()
                .statusCode(400);
    }

    @Test
    public void updateUser_BadRequestWhenFirstNameIsBlank() {
        UserRequestDTO userToUpdateWithEmptyValue = new UserRequestDTO("jane.doe@example.org", " ", "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "6625550144");
        UserRequestDTO userToUpdateWithNullValue = new UserRequestDTO("jane.doe@example.org", null, "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "6625550144");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToUpdateWithEmptyValue)
                .when()
                .put("/users/1")
                .then()
                .statusCode(400);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToUpdateWithNullValue)
                .when()
                .put("/users/1")
                .then()
                .statusCode(400);
    }

    @Test
    public void updateUser_BadRequestWhenLastNameIsBlank() {
        UserRequestDTO userToUpdateWithEmptyValue = new UserRequestDTO("jane.doe@example.org", "Jane", " ",
                LocalDate.of(1997, 07, 13), "42 Main St", "6625550144");
        UserRequestDTO userToUpdateWithNullValue = new UserRequestDTO("jane.doe@example.org", "Jane", null,
                LocalDate.of(1997, 07, 13), "42 Main St", "6625550144");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToUpdateWithEmptyValue)
                .when()
                .put("/users/1")
                .then()
                .statusCode(400);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToUpdateWithNullValue)
                .when()
                .put("/users/1")
                .then()
                .statusCode(400);
    }

    @Test
    public void updateUser_BadRequestWhenDOBIsNull() {
        UserRequestDTO userToUpdateWithNullValue = new UserRequestDTO("jane.doe@example.org", "Jane", "Doe",
                null, "42 Main St", "6625550144");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToUpdateWithNullValue)
                .when()
                .put("/users/1")
                .then()
                .statusCode(400);
    }

    @Test
    public void updateUser_BadRequest_userCanNotChangeDOB() {
        UserRequestDTO userToUpdate = new UserRequestDTO("jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1997, 07, 13), "42 Main St", "6625550144");
        when(userService.updateUser(1L, userMapper.mapToUser(userToUpdate))).thenThrow(InvalidAgeException.class);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(userToUpdate)
                .when()
                .put("/users/1")
                .then()
                .statusCode(400);
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;
        doNothing().when(userService).deletedUser(userId);

        RestAssuredMockMvc
                .given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/users/{id}", userId)
                .then()
                .statusCode(204);

        verify(userService).deletedUser(userId);
    }

    @Test
    public void testDeleteUser_userNotFound() {
        Long userId = 1L;

        doThrow(UserNotFoundException.class).when(userService).deletedUser(userId);

        RestAssuredMockMvc
                .given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/users/{id}", userId)
                .then()
                .statusCode(404);

        verify(userService).deletedUser(userId);
    }

    @Test
    public void findUsersByBirthDateRange() {
        LocalDate fromDate = LocalDate.of(1996, 01, 01);
        LocalDate toDate = LocalDate.of(2000, 01, 01);
        List<User> mockUser = List.of(
                new User(1L, "jane.doe@example.org", "Jane", "Doe",
                        LocalDate.of(1997, 07, 13), "42 Main St", "3800000000"),
                new User(2L, "bob.ten@example.org", "Bob", "Ten",
                        LocalDate.of(1998, 05, 15), "57 Main St", "3800000000"),
                new User(3L, "alice.kim@example.org", "Alice", "Kim",
                        LocalDate.of(1999, 04, 20), "90 Main St", "3800011100")
        );
        when(userService.findUsersByBirthDateRange(fromDate, toDate)).thenReturn(mockUser);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .queryParam("fromDate", fromDate.toString())
                .queryParam("toDate", toDate.toString())
                .when()
                .get("/users")
                .then()
                .body("size()", equalTo(3))
                .body("[0].id", equalTo(1))
                .body("[0].email", equalTo("jane.doe@example.org"))
                .body("[0].firstName", equalTo("Jane"))
                .body("[0].lastName", equalTo("Doe"))
                .body("[0].birthDate", equalTo("1997-07-13"))
                .body("[0].address", equalTo("42 Main St"))
                .body("[0].phoneNumber", equalTo("3800000000"));
    }

    @Test
    public void findUsersByBirthDateRange_BadRequestWhenFromDateIsGreaterThanToDate() {
        LocalDate fromDate = LocalDate.of(1996, 01, 02);
        LocalDate toDate = LocalDate.of(1996, 01, 01);

        when(userService.findUsersByBirthDateRange(fromDate, toDate)).thenThrow(IllegalArgumentException.class);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .queryParam("fromDate", fromDate.toString())
                .queryParam("toDate", toDate.toString())
                .when()
                .get("/users")
                .then()
                .statusCode(400);
    }
}
