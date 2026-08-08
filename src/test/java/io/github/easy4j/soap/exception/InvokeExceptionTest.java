package io.github.easy4j.soap.exception;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link InvokeException}.
 */
public class InvokeExceptionTest {

    @Test
    public void shouldCreateExceptionWithCodeAndMsg() {
        InvokeException ex = new InvokeException("SOAP-001", "Server error");
        assertEquals("SOAP-001", ex.getCode());
        assertEquals("Server error", ex.getMsg());
        assertNull(ex.getCause());
    }

    @Test
    public void shouldCreateExceptionWithCause() {
        RuntimeException cause = new RuntimeException("root cause");
        InvokeException ex = new InvokeException("SOAP-002", "Timeout", cause);
        assertEquals("SOAP-002", ex.getCode());
        assertEquals("Timeout", ex.getMsg());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void shouldHandleNullCode() {
        InvokeException ex = new InvokeException(null, "msg");
        assertNull(ex.getCode());
        assertEquals("msg", ex.getMsg());
    }

    @Test
    public void shouldHandleNullMsg() {
        InvokeException ex = new InvokeException("code", null);
        assertEquals("code", ex.getCode());
        assertNull(ex.getMsg());
    }
}
