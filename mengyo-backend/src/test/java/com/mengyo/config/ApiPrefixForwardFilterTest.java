package com.mengyo.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiPrefixForwardFilterTest {

    private ApiPrefixForwardFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ApiPrefixForwardFilter("/api");
    }

    @Test
    void shouldForwardApiPrefixedRequest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/rescue/tasks");
        request.setQueryString("page=1&size=10");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getForwardedUrl()).isEqualTo("/rescue/tasks?page=1&size=10");
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void shouldPassThroughNonPrefixedRequest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rescue/tasks");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getForwardedUrl()).isNull();
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void shouldForwardRootApiRequestToHome() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getForwardedUrl()).isEqualTo("/");
        assertThat(chain.getRequest()).isNull();
    }
}
