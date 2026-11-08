package com.example.foobar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FoobarTest {
    
    private final Foobar foobar = new Foobar();

    @Test
    void test() {
        assertEquals("foobar", foobar.process());
    }

}


/*  mvn -f pom.xml -pl foobar test  */