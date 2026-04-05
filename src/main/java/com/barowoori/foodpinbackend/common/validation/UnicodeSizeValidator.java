package com.barowoori.foodpinbackend.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.text.BreakIterator;
import java.util.Locale;

public class UnicodeSizeValidator implements ConstraintValidator<UnicodeSize, CharSequence> {

    private int min;
    private int max;

    @Override
    public void initialize(UnicodeSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String text = value.toString();
        int length = countGraphemeClusters(text);
        return length >= this.min && length <= this.max;
    }

    private int countGraphemeClusters(String text) {
        BreakIterator iterator = BreakIterator.getCharacterInstance(Locale.ROOT);
        iterator.setText(text);

        int count = 0;
        int next = iterator.first();
        while (next != BreakIterator.DONE) {
            next = iterator.next();
            if (next != BreakIterator.DONE) {
                count++;
            }
        }
        return count;
    }
}
