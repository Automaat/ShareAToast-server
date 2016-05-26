package com.sat.service;

import com.sat.model.Authority;
import com.sat.model.User;
import com.sat.repositories.UserRepository;
import com.sat.security.SecurityUtil;
import com.sat.web.BadRequestException;
import com.sat.web.ErrorInfo;
import com.sat.web.dto.DTOMappers;
import com.sat.web.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sat.web.ErrorInfo.USER_NOT_FOUND;
import static com.sat.web.dto.DTOMappers.parseUser;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(UserDTO userDTO) {
        User user = parseUser(userDTO);

        if (!isEmailValid(user.getEmail())) {
            throw new BadRequestException(ErrorInfo.MAIL_INVALID);
        }

        userRepository.findOneByName(userDTO.getName())
                .ifPresent(s -> {
                    throw new BadRequestException(ErrorInfo.LOGIN_ALREADY_IN_USE);
                });

        userRepository.findOneByEmail(userDTO.getEmail())
                .ifPresent(s -> {
                    throw new BadRequestException(ErrorInfo.MAIL_ALREADY_IN_USE);
                });

        userRepository.save(user);
    }

    public void updateUser(UserDTO userDTO) {
        Optional<User> userFromDb = userRepository.findOneByName(userDTO.getName());

        userFromDb.orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));

        userFromDb.ifPresent(user -> {
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());

            userRepository.save(user);
        });
    }

    public UserDTO getUserWithName(String name) throws BadRequestException {
        return userRepository.findOneByName(name)
                .map(DTOMappers::parseToDTO)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
    }

    public UserDTO getUserWithID (String id) throws BadRequestException {
        return userRepository.findOneById(id)
                .map(DTOMappers::parseToDTO)
                .orElseThrow( () -> new BadRequestException(USER_NOT_FOUND) );
    }

    public UserDTO getCurrentlyLoggedUser() {
        String currentUser = SecurityUtil.getCurrentUser();
        UserDTO userDTO = getUserWithName(currentUser);
        return userDTO;
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }

        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
