package clear.solution.test.service.impl;

import clear.solution.test.entity.User;
import clear.solution.test.exception.InvalidAgeException;
import clear.solution.test.exception.UserNotFoundException;
import clear.solution.test.mapper.UserMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserServiceImpl.class, UserMapperImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void testCreateUser_isOk() {
        User requestUser = new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1990, 1, 1), "42 Main St", "6625550144");

        User user = userServiceImpl.createUser(requestUser);

        assertNotNull(user);
        assertEquals("jane.doe@example.org", user.getEmail());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
        assertEquals("42 Main St", user.getAddress());
        assertEquals("6625550144", user.getPhoneNumber());
    }

    @Test
    void testCreateUser_InvalidAge() {
        User requestUser = new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(2019, 1, 2), "42 Main St", "6625550144");
        assertThrows(InvalidAgeException.class, () -> userServiceImpl.createUser(requestUser));
    }

    @Test
    void testUpdateUserFields_isOk() {
        User requestUser = new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1990, 1, 1), "42 Main St", "6625550144");

        User user = userServiceImpl.createUser(requestUser);
        User updateUserFields = userServiceImpl.updateUserFields(1L,
                Map.of("email", "bob.ten@example.org", "firstName", "Bob", "lastName", "Ten",
                        "address", "57 Main St", "phoneNumber", "3800000000"));

        assertNotNull(updateUserFields);
        assertEquals("bob.ten@example.org", updateUserFields.getEmail());
        assertEquals("Bob", updateUserFields.getFirstName());
        assertEquals("Ten", updateUserFields.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
        assertEquals("57 Main St", updateUserFields.getAddress());
        assertEquals("3800000000", updateUserFields.getPhoneNumber());
    }

    @Test
    void testUpdateUserFields_UserNotFound() {
        Map<String, Object> fieldsToUpdate = Map.of("email", "jane.doe@example.org", "firstName", "Jane", "lastName", "Doe",
                "birthDate", LocalDate.of(1997, 07, 13).toString(),
                "address", "42 Main St", "phoneNumber", "6625550144");
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.updateUserFields(1L, fieldsToUpdate));
    }

    @Test
    void testUpdateUserFieldsChangeBirthDate_UserCanNotChangeDOB() {
        Map<String, Object> fieldsToUpdate = Map.of("birthDate", LocalDate.of(1997, 07, 13).toString());
        userServiceImpl.createUser(new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1990, 1, 1), "42 Main St", "6625550144"));
        assertThrows(IllegalArgumentException.class, () -> userServiceImpl.updateUserFields(1L, fieldsToUpdate));
    }

    @Test
    void testUpdateUserFields_PassUnexpectedField() {
        Map<String, Object> fieldsToUpdate = Map.of("country", "Ukraine");
        userServiceImpl.createUser(new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1990, 1, 1), "42 Main St", "6625550144"));
        assertThrows(IllegalArgumentException.class, () -> userServiceImpl.updateUserFields(1L, fieldsToUpdate));
    }

    @Test
    void testUpdateUser_isOk() {
        User requestUser = new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1990, 1, 1), "42 Main St", "6625550144");

        User user = userServiceImpl.createUser(requestUser);

        User updateUser = userServiceImpl.updateUser(1L, new User(null, "bob.ten@example.org", "Bob",
                "Ten", LocalDate.of(1990, 1, 1), "57 Main St", "6625550144"));

        assertNotNull(updateUser);
        assertEquals("bob.ten@example.org", updateUser.getEmail());
        assertEquals("Bob", updateUser.getFirstName());
        assertEquals("Ten", updateUser.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
        assertEquals("57 Main St", updateUser.getAddress());
        assertEquals("6625550144", updateUser.getPhoneNumber());
    }

    @Test
    void testUpdateUser_ExceptionWhenChaneDOB() {
        User requestUser = new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1990, 1, 1), "42 Main St", "6625550144");
        userServiceImpl.createUser(requestUser);

        User userToUpdate = new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1990, 1, 2), "42 Main St", "6625550144");

        assertThrows(IllegalArgumentException.class, () -> userServiceImpl.updateUser(1L, userToUpdate));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        User user = new User(null, "jane.doe@example.org", "Jane", "Doe",
                LocalDate.of(1970, 1, 1), "42 Main St", "6625550144");
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.updateUser(1L, user));
    }

    @Test
    void testDeletedUser_UserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.deletedUser(1L));
    }

    @Test
    void testFindUsersByBirthDateRange_isEmpty() {
        LocalDate fromDate = LocalDate.of(1970, 1, 1);
        LocalDate toDate = LocalDate.of(1970, 1, 2);

        assertTrue(userServiceImpl.findUsersByBirthDateRange(fromDate, toDate).isEmpty());
    }

    @Test
    void testFindUsersByBirthDateRange_FromDateIsGreaterThanToDate() {
        LocalDate toDate = LocalDate.of(1970, 1, 1);
        LocalDate fromDate = LocalDate.of(1970, 1, 2);

        assertThrows(IllegalArgumentException.class, () -> userServiceImpl.findUsersByBirthDateRange(fromDate, toDate));
    }
}

