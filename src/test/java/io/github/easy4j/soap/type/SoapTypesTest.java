package io.github.easy4j.soap.type;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link SoapTypes} enum.
 */
public class SoapTypesTest {

    @Test
    public void shouldResolveStringTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(String.class);
        assertTrue(type instanceof StringSoapType);
    }

    @Test
    public void shouldResolveIntegerTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(Integer.class);
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveIntPrimitiveByBean() {
        SoapType type = SoapTypes.getTypeByBean(Integer.TYPE);
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveLongTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(Long.class);
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveDoubleTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(Double.class);
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveFloatTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(Float.class);
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveShortTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(Short.class);
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveBigDecimalTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(java.math.BigDecimal.class);
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveDateTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(java.util.Date.class);
        assertTrue(type instanceof DateSoapType);
    }

    @Test
    public void shouldResolveCalendarTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(java.util.Calendar.class);
        assertTrue(type instanceof DateSoapType);
    }

    @Test
    public void shouldResolveListTypeByBean() {
        SoapType type = SoapTypes.getTypeByBean(java.util.List.class);
        assertTrue(type instanceof ListSoapType);
    }

    @Test
    public void shouldFallbackToBeanTypeForUnknownClass() {
        SoapType type = SoapTypes.getTypeByBean(java.io.File.class);
        assertTrue(type instanceof BeanSoapType);
    }

    @Test
    public void shouldFallbackToBeanTypeForNullClass() {
        SoapType type = SoapTypes.getTypeByBean(null);
        assertTrue(type instanceof BeanSoapType);
    }

    @Test
    public void shouldResolveStringTypeBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("string");
        assertTrue(type instanceof StringSoapType);
    }

    @Test
    public void shouldResolveIntTypeBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("int");
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveLongTypeBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("long");
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveShortTypeBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("short");
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveIntegerTypeBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("integer");
        assertTrue(type instanceof NumberSoapType);
    }

    @Test
    public void shouldResolveDateTypeBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("date");
        assertTrue(type instanceof DateSoapType);
    }

    @Test
    public void shouldResolveDateTimeTypeBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("dateTime");
        assertTrue(type instanceof DateSoapType);
    }

    @Test
    public void shouldResolveAnyTypeBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("anyType");
        assertTrue(type instanceof BeanSoapType);
    }

    @Test
    public void shouldResolveListPatternBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("List{string}");
        assertTrue(type instanceof ListSoapType);
    }

    @Test
    public void shouldResolveListEmptyPatternBySoap() {
        SoapType type = SoapTypes.getTypeBySoap("List{}");
        assertTrue(type instanceof ListSoapType);
    }

    @Test
    public void shouldFallbackToBeanTypeForUnknownSoapType() {
        SoapType type = SoapTypes.getTypeBySoap("unknown");
        assertTrue(type instanceof BeanSoapType);
    }

    @Test
    public void shouldFallbackToBeanTypeForNullSoapType() {
        SoapType type = SoapTypes.getTypeBySoap(null);
        assertTrue(type instanceof BeanSoapType);
    }

    @Test
    public void shouldReturnCorrectNameForEachConstant() {
        assertNotNull(SoapTypes.string.getName());
        assertNotNull(SoapTypes.number.getName());
        assertNotNull(SoapTypes.date.getName());
        assertNotNull(SoapTypes.bean.getName());
        assertNotNull(SoapTypes.list.getName());
    }

    @Test
    public void shouldReturnCorrectSoapTypeForEachConstant() {
        assertTrue(SoapTypes.string.getSoapType() instanceof StringSoapType);
        assertTrue(SoapTypes.number.getSoapType() instanceof NumberSoapType);
        assertTrue(SoapTypes.date.getSoapType() instanceof DateSoapType);
        assertTrue(SoapTypes.bean.getSoapType() instanceof BeanSoapType);
        assertTrue(SoapTypes.list.getSoapType() instanceof ListSoapType);
    }

    @Test
    public void shouldReturnEnumNameFromToString() {
        assertEquals("string", SoapTypes.string.toString());
        assertEquals("number", SoapTypes.number.toString());
        assertEquals("date", SoapTypes.date.toString());
        assertEquals("bean", SoapTypes.bean.toString());
        assertEquals("list", SoapTypes.list.toString());
    }
}
