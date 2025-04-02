package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "Fields to enable the creation and updating of a LocalDeliveryUnitMailbox entity")
class LocalDeliveryUnitMailboxForm(
  @Schema(description = "The unit code of the LDU", example = "N60ABC")
  @field:NotBlank
  @field:Pattern(regexp = "^[a-zA-Z0-9]+$", message = "must contain only letters and numbers")
  var unitCode: String? = "",

  @Schema(description = "The probation area code of the LDU")
  var areaCode: String? = null,

  @Schema(description = "The LDUs email address", example = "ldu@justice.gov.uk")
  @field:NotBlank
  @field:Email
  var emailAddress: String? = "",

  @Schema(description = "The country that the LDU resides in", example = "England")
  @field:NotBlank
  @field:Pattern(regexp = "England|Wales", message = "must be either 'England' or 'Wales'")
  var country: String? = null,

  @Schema(description = "A name to help recognise the LDU", example = "Carlisle")
  @field:NotBlank
  var name: String? = "",
) {
  fun asEntity(): LocalDeliveryUnitMailbox = LocalDeliveryUnitMailbox(
    unitCode = unitCode,
    areaCode = areaCode,
    emailAddress = emailAddress,
    country = country,
    name = name,
  )
}
