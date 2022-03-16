package com.interview.authservice.mapper;


import com.interview.authservice.entity.User;
import com.interview.authservice.inboun.http.model.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto mapUserEntityToDto(User createdUser);
}