package com.sat.config.error

import com.fasterxml.jackson.databind.ObjectMapper
import com.sat.SaTApplication
import com.sat.web.ErrorInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringApplicationConfiguration(classes = SaTApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
class RestResponseExceptionHandlerTest extends Specification {

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc = null

    def objectMapper = new ObjectMapper()

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build()
    }

    def "handler should intercept BadRequestException when event is not found"() {
        when:
        def response = mockMvc.perform(delete('/api/events/XXXX')
                .contentType(APPLICATION_JSON))
        then:
        response
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(ErrorInfo.EVENT_NOT_FOUND)))
    }

    def "handler should intercept BadRequestException when mail is malformed"() {
        given:
        def addUserRequest = '{"name": "Jan",' +
                '"password": "xxx", ' +
                '"email": "there_is_no_at.pl"}'
        when:
        def response = mockMvc.perform(post('/api/users')
                .contentType(APPLICATION_JSON)
                .content(addUserRequest))

        then:
        response
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(ErrorInfo.MAIL_INVALID)))
    }

    def "handler should intercept BadRequestException when mail is used"() {
        given:
        def addUserRequest = '{"name": "Jan",' +
                '"password": "xxx", ' +
                '"email": "janusz@xxx.pl"}'
        when:
        def response = mockMvc.perform(post('/api/users')
                .contentType(APPLICATION_JSON)
                .content(addUserRequest))

        then:
        response
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(ErrorInfo.MAIL_ALREADY_IN_USE)))
    }

    def "handler should intercept BadRequestException when name is used"() {
        given:
        def addUserRequest = '{"name": "Janusz",' +
                '"password": "xxx", ' +
                '"email": "dkgjbsgk@xxx.pl"}'
        when:
        def response = mockMvc.perform(post('/api/users')
                .contentType(APPLICATION_JSON)
                .content(addUserRequest))

        then:
        response
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(ErrorInfo.LOGIN_ALREADY_IN_USE)))
    }
}
