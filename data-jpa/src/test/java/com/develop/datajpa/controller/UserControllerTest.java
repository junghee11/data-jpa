package com.develop.datajpa.controller;

import com.develop.datajpa.request.user.UserSignUpRequest;
import com.develop.datajpa.service.user.UserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("user - 회원가입 test")
    void signUpUserTest() throws Exception {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUserId("test");
        request.setPassword("uiosek35#@");
        request.setName("홍길동");
        request.setNickname("테스트계정");
        request.setPhone("01000000000");
        request.setIp("123.4567.4567");

        given(userService.userSignUp(request)).willReturn(
            Map.of("message", "회원가입이 완료되었습니다")
        );

        Gson gson = new Gson();
        String content = gson.toJson(request);

        mockMvc.perform(post("/user/signup")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());
    }

}
