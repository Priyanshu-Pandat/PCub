package com.dhurrah.service;

import com.dhurrah.entity.User;
import com.dhurrah.exceptions.UserException;
import com.dhurrah.model.UserDto;
import com.dhurrah.repositores.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Log4j2
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public boolean createUser(UserDto userDto,Integer userId) {
        User user = userRepo.findById(Long.valueOf(userId)).orElseThrow(
                () -> new UserException("User not found with ID: " , "USER_NOT_FOUND")
        );
        user.setName(userDto.getName());
        user.setAge(userDto.getAge());
        user.setGender(userDto.getGender());
        user.setEmail(userDto.getEmail());
        user.setProfileCompleted(true);
        userRepo.save(user); // Save updated user
        log.info("User updated successfully: {}", user);

        return true;

    }

    @Override
    public void updateUser(UserDto userDto, Integer userId) {

    }


    @Override
    public UserDto getUserById(long userId) {
        return null;
    }

    @Override
    public void deleteUserById(long userId) {

    }

    @Override
    public List<UserDto> getAllUser() {
        return List.of();
    }
}
