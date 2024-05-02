package clear.solution.test.service.impl;

import clear.solution.test.entity.User;
import clear.solution.test.exception.InvalidAgeException;
import clear.solution.test.exception.UserNotFoundException;
import clear.solution.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final List<User> users = new ArrayList<>();
    @Value("${user.minimum.age}")
    private int minimalAge;
    private Long nextId = 1L;

    @Override
    public User createUser(User requestUser) {
        if (!isUserHasMinimalAge(requestUser.getBirthDate())) {
            throw new InvalidAgeException("User is under the minimum age.");
        }
        requestUser.setId(nextId++);
        users.add(requestUser);
        return requestUser;
    }

    @Override
    public User updateUserFields(Long id, Map<String, Object> fields) {
        User user = findUserById(id);

        fields.forEach((field, value) -> {
            switch (field) {
                case "email" -> user.setEmail((String) value);
                case "firstName" -> user.setFirstName((String) value);
                case "lastName" -> user.setLastName((String) value);
                case "birthDate" -> throw new IllegalArgumentException("User can't change DOB");
                case "address" -> user.setAddress((String) value);
                case "phoneNumber" -> user.setPhoneNumber((String) value);
                default -> throw new IllegalArgumentException("Field " + field + " not found on User.");
            }
        });
        return user;
    }

    @Override
    public User updateUser(Long id, User requestUser) {
        User updateUser = findUserById(id);
        if (!updateUser.getBirthDate().equals(requestUser.getBirthDate())) {
            throw new IllegalArgumentException("User can't change DOB");
        }
        updateUser.setEmail(requestUser.getEmail());
        updateUser.setFirstName(requestUser.getFirstName());
        updateUser.setLastName(requestUser.getLastName());
        updateUser.setBirthDate(requestUser.getBirthDate());
        updateUser.setAddress(requestUser.getAddress());
        updateUser.setPhoneNumber(requestUser.getPhoneNumber());
        return updateUser;
    }

    @Override
    public void deletedUser(Long id) {
        users.remove(findUserById(id));
    }

    @Override
    public List<User> findUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        if (!fromDate.isBefore(toDate)) {
            throw new IllegalArgumentException("fromDate must be less than toDate");
        }
        return users.stream()
                .filter(user -> user.getBirthDate().isAfter(fromDate) && user.getBirthDate().isBefore(toDate))
                .toList();
    }

    private boolean isUserHasMinimalAge(LocalDate birthDate) {
        Period period = Period.between(birthDate, LocalDate.now());
        return period.getYears() >= minimalAge;
    }

    private User findUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found for this id: " + id));
    }
}
