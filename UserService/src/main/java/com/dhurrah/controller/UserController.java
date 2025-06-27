package com.dhurrah.controller;

import com.cloudinary.Api;
import com.dhurrah.model.ApiResponse;
import com.dhurrah.model.UserDto;
import com.dhurrah.model.Response;
import com.dhurrah.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/profile-setup/add")
    public ResponseEntity<ApiResponse<Boolean>> createUser(@Valid @RequestBody UserDto userDto, @RequestHeader("X-User-Id")  Integer userId) {
        log.info("creating User with this id :  {}", userId);
      boolean isUserSaved =   this.userService.createUser(userDto,userId);
        return ResponseEntity.ok(new ApiResponse<>(true, isUserSaved, "User Created successfully"));


    }

}
