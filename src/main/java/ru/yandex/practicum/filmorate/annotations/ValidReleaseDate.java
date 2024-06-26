package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.annotations.constraints.DateConstraintValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateConstraintValidator.class)
public @interface ValidReleaseDate {

    String message() default "{Film release date cannot be before in 28.12.1895}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
