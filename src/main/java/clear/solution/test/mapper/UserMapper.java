package clear.solution.test.mapper;

import clear.solution.test.dto.UserRequestDTO;
import clear.solution.test.dto.UserResponseDTO;
import clear.solution.test.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    User mapToUser(UserRequestDTO request);

    UserResponseDTO mapToUserResponse(User user);
}
