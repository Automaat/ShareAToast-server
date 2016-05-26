package com.sat.web.controllers

import com.sat.model.User
import com.sat.service.UserService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Ignore
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest extends Specification {

    def userService = Mock(UserService)
    def userController = new UserController(userService);

    def mockMvc = MockMvcBuilders.standaloneSetup(userController).build()

    @Ignore
    def "user created correct"() {
        when:
        def response = mockMvc.perform(post('/api/users')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"name": "Jan",' +
                '"password": "xxx", ' +
                '"email": "Jan@go.pl"}')
        )

        then:
        response.andExpect(status().isCreated())
    }

    @Ignore
    def "no data"() {
        when:
        def response = mockMvc.perform(post('/user/register')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"name": "",' +
                '"password": "", ' +
                '"email": ""}')
        )

        then:
        response.andExpect(status().isBadRequest())
    }

    @Ignore
    def "wrong email"() {
        when:
        def response = mockMvc.perform(post('/user/register')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"name": "XYZ",' +
                '"password": "xyz", ' +
                '"email": "matmat.pl"}')
        )

        then:
        response.andExpect(status().isBadRequest())
    }

    @Ignore
    def "email already in use"() {
        given:
        userRepository.findOneByEmail("mat@mat.pl") >> Optional.of(new User(name: "Mateusz", mail: "mat@mat.pl", password: "aaa"));
        userRepository.findOneByUsername("XYZ") >> new Optional<>();

        when:
        def response = mockMvc.perform(post('/user/register')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"name": "XYZ",' +
                '"password": "xyz", ' +
                '"email": "mat@mat.pl"}')
        )

        then:
        response.andExpect(status().isBadRequest())
    }

    @Ignore
    def "name already in use"() {
        given:
        userRepository.findOneByEmail("mat@mat.pl") >> new Optional<>();
        userRepository.findOneByUsername("XYZ") >> Optional.of(new User(name: "XYZ", mail: "xyz@mat.pl", password: "aaa"));

        when:
        def response = mockMvc.perform(post('/user/register')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"name": "XYZ",' +
                '"password": "xyz", ' +
                '"email": "mat@mat.pl"}')
        )

        then:
        response.andExpect(status().isBadRequest())
    }
}
