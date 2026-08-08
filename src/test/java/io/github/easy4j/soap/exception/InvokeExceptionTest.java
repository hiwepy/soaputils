package io.github.easy4j.soap.exception;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link InvokeException}.
 */
public class InvokeExceptionTest {

    @Test
    public void shouldCreateExceptionWithCodeMsgAndCause() {
        RuntimeException cause = new RuntimeException("root cause");
        InvokeException ex = new InvokeException("SOAP-001", "Server error", cause);
        assertEquals("SOAP-001", ex.getCode());
        assertEquals("Server error", ex.getMsg());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void shouldPreserveFaultCode() {
        RuntimeException cause = new RuntimeException("c");
        InvokeException ex = new InvokeException("SOAP-003", "msg", cause);
        assertEquals("SOAP-003", ex.getCode());
    }

    @Test
    public void shouldPreserveFaultMsg() {
        RuntimeException cause = new RuntimeException("c");
        InvokeException ex = new InvokeException("code", "fault message", cause);
        assertEquals("fault message", ex.getMsg());
    }
}
