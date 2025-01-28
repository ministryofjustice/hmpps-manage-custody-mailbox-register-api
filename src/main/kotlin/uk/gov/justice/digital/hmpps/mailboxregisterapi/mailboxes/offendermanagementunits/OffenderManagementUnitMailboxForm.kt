package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.mailboxregisterapi.PrisonCode

@Schema(description = "Fields to enable the creation and updating of a OffenderManagementUnitMailbox entity")
class OffenderManagementUnitMailboxForm(
  @Schema(description = "The OMUs email address", example = "omu@justice.gov.uk")
  @field:NotBlank
  @field:Email
  var emailAddress: String? = "",

  @Schema(description = "A name to help recognise the OMU", example = "Carlisle")
  var name: String? = null,

  @Schema(description = "The prison code of the OMU", example = "LEI")
  @field:NotNull(message = "must not be blank")
  var prisonCode: PrisonCode? = null,

  @Schema(description = "The role / activity of the mailbox", example = "CVL")
  @field:NotNull(message = "must not be blank")
  var role: OffenderManagementUnitRole? = null,
) {
  fun asEntity(): OffenderManagementUnitMailbox {
    return OffenderManagementUnitMailbox(
      emailAddress = emailAddress,
      name = name,
      prisonCode = prisonCode,
      role = role,
    )
  }
}
