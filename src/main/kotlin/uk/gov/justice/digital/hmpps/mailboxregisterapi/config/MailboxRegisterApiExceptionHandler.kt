package uk.gov.justice.digital.hmpps.mailboxregisterapi.config

import jakarta.validation.ValidationException
import org.hibernate.exception.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import tools.jackson.databind.exc.InvalidFormatException
import uk.gov.justice.digital.hmpps.mailboxregisterapi.FailedValidationException
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ValidationErrorResponse
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.UNIT_CODE_UNIQUE_CONSTRAINT
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestControllerAdvice
class MailboxRegisterApiExceptionHandler {
  @ExceptionHandler(ValidationException::class)
  fun handleValidationException(e: ValidationException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(BAD_REQUEST)
    .body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = "Validation failure: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.info("Validation exception: {}", e.message) }

  @ExceptionHandler(DataIntegrityViolationException::class)
  fun handleValidationException(e: DataIntegrityViolationException): ResponseEntity<ValidationErrorResponse> {
    if (e.cause !is ConstraintViolationException) throw e

    val errors = when (val constraintName = (e.cause as ConstraintViolationException).constraintName) {
      UNIT_CODE_UNIQUE_CONSTRAINT -> mapOf("unitCode" to "Unit Code already exists")
      else -> mapOf("error" to "Database constraint violation: $constraintName")
    }

    return ResponseEntity.status(BAD_REQUEST)
      .body(
        ValidationErrorResponse(
          errors = errors,
          message = "Constraint violation",
          status = BAD_REQUEST.value(),
        ),
      )
  }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> = ResponseEntity
    .badRequest()
    .body(
      ValidationErrorResponse(
        errors = e.bindingResult.fieldErrors.associate { it.field to it.defaultMessage },
        message = "Validation failed",
        status = BAD_REQUEST.value(),
      ),
    )

  @ExceptionHandler(FailedValidationException::class)
  fun handleValidationException(e: FailedValidationException): ResponseEntity<ValidationErrorResponse> = ResponseEntity
    .badRequest()
    .body(
      ValidationErrorResponse(
        errors = e.responseBody,
        message = "Validation failed",
        status = BAD_REQUEST.value(),
      ),
    )

  @ExceptionHandler(HttpMessageNotReadableException::class)
  fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ValidationErrorResponse> {
    val cause = e.cause
    return if (cause is InvalidFormatException && cause.targetType.isEnum) {
      handleInvalidEnumValues(cause)
    } else {
      throw e
    }
  }

  private fun handleInvalidEnumValues(invalidFormatException: InvalidFormatException): ResponseEntity<ValidationErrorResponse> {
    val fieldName = invalidFormatException.path[0].propertyName
    val correctValues = invalidFormatException.targetType.enumConstants.joinToString(",")
    val message = "must be one of $correctValues"

    return ResponseEntity
      .badRequest()
      .body(
        ValidationErrorResponse(
          errors = mapOf(fieldName to message),
          message = "Validation failed",
          status = BAD_REQUEST.value(),
        ),
      )
  }

  @ExceptionHandler(NoResourceFoundException::class)
  fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(NOT_FOUND)
    .body(
      ErrorResponse(
        status = NOT_FOUND,
        userMessage = "No resource found failure: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.info("No resource found exception: {}", e.message) }

  @ExceptionHandler(AccessDeniedException::class)
  fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(FORBIDDEN)
    .body(
      ErrorResponse(
        status = FORBIDDEN,
        userMessage = "Forbidden: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.debug("Forbidden (403) returned: {}", e.message) }

  @ExceptionHandler(Exception::class)
  fun handleException(e: Exception): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(INTERNAL_SERVER_ERROR)
    .body(
      ErrorResponse(
        status = INTERNAL_SERVER_ERROR,
        userMessage = "Unexpected error: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.error("Unexpected exception", e) }

  private companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
