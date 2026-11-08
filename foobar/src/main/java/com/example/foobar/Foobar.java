package com.example.foobar;

import java.util.HashSet;
import java.util.Set;

public class Foobar {
    public String process(String input) {

        Set<String> seen = new HashSet<>();
        StringBuilder output = new StringBuilder();

        String[] numbers = input.split(",");

        for (String value : numbers) {
            int number;

            try {
                number = Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: " + value, e);
            }

            if (output.length() > 0) {
                output.append(",");
            }

            if (number % 15 == 0) {
                output.append("foobar");
            } else if (number % 3 == 0) {
                output.append("foo");
            } else if (number % 5 == 0) {
                output.append("bar");
            } else {
                output.append(number);
            }

            if (seen.contains(value)) {
                output.append("-copy");
            } else {
                seen.add(value);
            }

        }
        return output.toString();
    }

}
