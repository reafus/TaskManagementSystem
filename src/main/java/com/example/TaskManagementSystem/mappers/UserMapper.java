package com.example.TaskManagementSystem.mappers;

import com.example.TaskManagementSystem.dto.UserDTO;
import com.example.TaskManagementSystem.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    User toUser(UserDTO userDTO);
}
