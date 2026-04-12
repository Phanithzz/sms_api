package com.sms.smsApi.mapper;


import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.dto.UserResponseDto;
import com.sms.smsApi.model.User;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for User entity conversions.
 *
 * Handles mapping between User entities and DTOs with proper null handling
 * and custom mapping logic for sensitive fields.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Maps UserCreateRequest to User entity.
     *
     * @param request the create request DTO
     * @return new User entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "username", source = "username")
    User toEntity(RegistrationDto request);

    /**
     * Maps User entity to UserResponse DTO.
     *
     * @param user the User entity
     * @return UserResponse DTO
     */
    @Mapping(target = "username", source = "actualUsername")
    UserResponseDto toResponse(User user);

    /**
     * Maps list of User entities to list of UserResponse DTOs.
     *
     * @param users list of User entities
     * @return list of UserResponse DTOs
     */
    List<UserResponseDto> toResponseList(List<User> users);

    /**
     * Updates existing User entity with data from UserUpdateRequest.
     *
     * @param request the update request DTO
     * @param user    the existing User entity to update
     */
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateEntity(@MappingTarget User user, UserUpdateRequest request);
}

