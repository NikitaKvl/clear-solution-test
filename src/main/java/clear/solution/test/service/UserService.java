package clear.solution.test.service;

import clear.solution.test.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface UserService {
    User createUser(User requestUser);

    User updateUserFields(Long id, Map<String, Object> fields);

    User updateUser(Long id, User requestUser);

    void deletedUser(Long id);

    List<User> findUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate);
}
