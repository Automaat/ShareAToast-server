package com.sat.service

import com.sat.model.User
import com.sat.repositories.UserRepository
import com.sat.web.BadRequestException
import com.sat.web.ErrorInfo
import com.sat.web.dto.DTOMappers
import com.sat.web.dto.UserDTO
import spock.lang.Specification

import static com.sat.web.dto.DTOMappers.*

class UserServiceTest extends Specification {

    def userRepository = Mock(UserRepository)
    def userService = new UserService(userRepository);

    def someUser = User.builder()
            .name("Janusz")
            .email("janusz@wp.pl")
            .password("123zxc")
            .build()

    def "method getUserById should return DTO when user exist"() {
        given:
        userRepository.findOneById(_) >> { Optional.of(someUser) }

        when:
        def userDTO = userService.getUserWithID(someUser.getId())

        then:
        userDTO.equals(parseToDTO(someUser))
    }

    def "method getUserByName should throw BadRequestException when user doesn't exist"() {
        given:
        userRepository.findOneById(_) >> { Optional.empty() }

        when:
        userService.getUserWithID("123")

        then:
        thrown(BadRequestException)
    }
}
