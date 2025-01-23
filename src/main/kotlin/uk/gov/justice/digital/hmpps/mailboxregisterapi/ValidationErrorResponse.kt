package uk.gov.justice.digital.hmpps.mailboxregisterapi

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "A response containing validation errors for each field")
data class ValidationErrorResponse(
  @Schema(description = "The HTTP status code, will be 400", example = "400")
  val status: Int,

  @Schema(
    description = "A map of fieldName to the validation error for that field",
    example = "{\"emailAddress\": \"Invalid format for email address\", \"name\": \"Must start with ab uppercase letter\"}",
  )
  val errors: Map<String, String?>,

  @Schema(description = "A description of the error", example = "Validation Failed")
  val message: String,
)
