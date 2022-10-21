package com.helkor.project.buttons.Utils;
import java.util.Arrays;

public class ButtonVariant<T> extends Deck<T>{
    public <t extends Enum<t>> ButtonVariant(Class<T> enum_class, int pointer) {
        super(Arrays.asList(enum_class.getEnumConstants()), pointer);
    }
    public <t extends Enum<t>> ButtonVariant(Class<T> enum_class) {
        super(Arrays.asList(enum_class.getEnumConstants()));
        System.out.println(" love is " + Arrays.toString(enum_class.getEnumConstants()));
    }
}
