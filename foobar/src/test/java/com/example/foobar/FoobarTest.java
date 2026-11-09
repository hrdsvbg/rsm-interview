package com.example.foobar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FoobarTest {

    private final Foobar foobar = new Foobar();

    @Test
    void test1() {
        assertEquals("1,2,foo,4,bar,foo,foobar", foobar.process("1,2,3,4,5,6,45"));
    }

    @Test
    void test2() {
        assertEquals("1,2,foo,1-copy,2-copy,foo-copy,1-copy,1-copy", foobar.process("1,2,3,1,2,3,1,1"));
    }

    @Test
    void test3() {
        assertEquals("1,1-copy,foo,foo-copy,bar,bar-copy,foobar,foobar-copy", foobar.process("1,1,3,3,5,5,45,45"));
    }

    @Test
    void zeroIsFoobar() {
        assertEquals("foobar", foobar.process("0"));
    }

    @Test
    void emptyInputReturnsEmptyString() {
        assertEquals("", foobar.process(""));
    }

    @Test
    void negativeNumbersAreProcessed() {
        assertEquals("1,-2,foo,4,bar,foo,foobar", foobar.process("1,-2,-3,4,5,6,-45"));
    }

    @Test
    void mustRejectNullInput() {
        assertThrows(IllegalArgumentException.class, () -> foobar.process(null));
    }

    @Test
    void mustRejectBlankInput() {
        assertThrows(IllegalArgumentException.class, () -> foobar.process(" "));
    }

    @Test
    void mustRejectOnMissingElement() {
        assertThrows(IllegalArgumentException.class, () -> foobar.process("1,,2"));
    }

    @Test
    void mustRejectNonNumericInput() {
        assertThrows(IllegalArgumentException.class, () -> foobar.process("a,b,c"));
    }

    @Test
    void mustRejectNonIntegerInput() {
        assertThrows(IllegalArgumentException.class, () -> foobar.process("3.4"));
    }

}

/* mvn -f pom.xml -pl foobar test */