package com.benorim.ridepally.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new MockServletOutputStream(outputStream));
    }

    @Test
    void commence_Unauthorized_Returns401() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Unauthorized");

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = outputStream.toString();
        assertTrue(responseContent.contains("Unauthorized"));
        assertTrue(responseContent.contains("401"));
        assertTrue(responseContent.contains("/api/test"));
    }

    @Test
    void commence_NullMessage_ReturnsDefaultMessage() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn(null);

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = outputStream.toString();
        assertTrue(responseContent.contains("Unauthorized"));
        assertTrue(responseContent.contains("401"));
        assertTrue(responseContent.contains("/api/test"));
    }

    @Test
    void commence_IOException_HandlesException() throws IOException, ServletException {
        when(response.getOutputStream()).thenThrow(new IOException("Test IO Exception"));

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_ResponseContentType_IsApplicationJson() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api/test");

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void commence_ResponseStatus_IsUnauthorized() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api/test");

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private static class MockServletOutputStream extends jakarta.servlet.ServletOutputStream {
        private final OutputStream outputStream;

        public MockServletOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outputStream.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            outputStream.flush();
        }

        @Override
        public void close() throws IOException {
            outputStream.close();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
} 