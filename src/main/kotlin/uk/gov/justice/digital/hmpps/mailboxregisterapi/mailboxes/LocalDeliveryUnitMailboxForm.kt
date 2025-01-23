package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "Fields to enable the creation and updating of a LocalDeliveryUnitMailbox entity")
class LocalDeliveryUnitMailboxForm(
  @Schema(description = "The unit code of the LDU", example = "N60ABC")
  @field:NotBlank
  var unitCode: String? = "",

  @Schema(description = "The probation area code of the LDU")
  @field:NotBlank
  var areaCode: String? = "",

  @Schema(description = "The LDUs email address", example = "ldu@justice.gov.uk")
  @field:NotBlank
  @field:Email
  var emailAddress: String? = "",

  @Schema(description = "The country that the LDU resides in", example = "England")
  var country: String? = null,

  @Schema(description = "A name to help recognise the LDU", example = "Carlisle")
  var name: String? = null,
) {
  fun asEntity(): LocalDeliveryUnitMailbox {
    return LocalDeliveryUnitMailbox(
      unitCode = unitCode,
      areaCode = areaCode,
      emailAddress = emailAddress,
      country = country,
      name = name,
    )
  }
}
