package com.javacorner.admin.web;

import com.javacorner.admin.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
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
