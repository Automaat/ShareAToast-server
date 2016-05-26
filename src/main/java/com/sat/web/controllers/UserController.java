package com.sat.web.controllers;

import com.sat.model.User;
import com.sat.security.SecurityUtil;
import com.sat.service.UserService;
import com.sat.web.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/users", method = POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {

        userService.createUser(userDTO);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }


    @RequestMapping(value = "/users/{id}", method = GET)
    public ResponseEntity<?> getUserById(@PathVariable(value = "id") String id) {
        return new ResponseEntity<>(userService.getUserWithName(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/users", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO) {

        userService.updateUser(userDTO);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @RequestMapping(value = "/users/name={name}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserByName(@PathVariable(value = "name") String name) {

        UserDTO userFromDB = userService.getUserWithName(name);

        return new ResponseEntity<>(userFromDB, HttpStatus.OK);
    }

    @RequestMapping(value = "/me", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getLoggedUser(){

        UserDTO currentlyLoggedUser = userService.getCurrentlyLoggedUser();

        return new ResponseEntity<UserDTO>(currentlyLoggedUser, HttpStatus.OK);
    }

}
