package clear.solution.test.controller;

import clear.solution.test.dto.UserRequestDTO;
import clear.solution.test.dto.UserResponseDTO;
import clear.solution.test.entity.User;
import clear.solution.test.mapper.UserMapper;
import clear.solution.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Validated @RequestBody UserRequestDTO userRequestDTO) {
        User createdUser = userService.createUser(userMapper.mapToUser(userRequestDTO));
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.mapToUserResponse(createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                      @Validated @RequestBody UserRequestDTO userRequestDTO) {
        User updatedUser = userService.updateUser(id, userMapper.mapToUser(userRequestDTO));
        return ResponseEntity.ok(userMapper.mapToUserResponse(updatedUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateSomeUserFields(@PathVariable("id") Long id,
                                                     @RequestBody Map<String, Object> fields) {
        User user = userService.updateUserFields(id, fields);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deletedUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findUsersByBirthDateRange(@RequestParam LocalDate fromDate,
                                                                           @RequestParam LocalDate toDate) {
        List<UserResponseDTO> users = userService.findUsersByBirthDateRange(fromDate, toDate)
                .stream()
                .map(userMapper::mapToUserResponse)
                .toList();
        return ResponseEntity.ok(users);
    }
}
