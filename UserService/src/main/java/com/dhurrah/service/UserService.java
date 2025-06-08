package com.dhurrah.service;

import com.dhurrah.model.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
  void createUser(UserDto userDto,Integer userId);
  void updateUser(UserDto userDto,Integer userId);
  UserDto getUserById(long userId);
  void deleteUserById(long userId);
  List<UserDto> getAllUser();
}
