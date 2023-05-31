package com.leonidov.cloud.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void homePage() {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/");

        try {
            MvcResult mvcResult = this.mockMvc.perform(
                    requestBuilder
                            .contentType("text/html;charset=UTF-8"))
                    .andExpectAll(
                            status().isOk()
                    ).andReturn();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}