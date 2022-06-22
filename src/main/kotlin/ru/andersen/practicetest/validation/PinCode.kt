package ru.andersen.practicetest.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Constraint(validatedBy = [PinCodeValidator::class])
annotation class PinCode(
    val message: String = "Pin code format is incorrect",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PinCodeValidator : ConstraintValidator<PinCode, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext?): Boolean {
        val regex = Regex("^[0-9]{4}$")
        return regex.matches(value)
    }
}