package com.javacorner.admin.web;

import com.javacorner.admin.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public boolean checkIfEmailExists(@RequestParam(value = "email", defaultValue = "") String email) {
        return userService.loadUserByEmail(email) != null;
    }
}
