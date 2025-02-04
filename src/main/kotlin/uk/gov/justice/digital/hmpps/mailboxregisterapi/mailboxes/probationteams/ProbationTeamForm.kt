package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailbox
import java.util.UUID

@Schema(description = "Fields to enable the creation and updating of a ProbationTeam entity")
class ProbationTeamForm(
  @Schema(description = "The team code of the Probation Team", example = "123")
  @field:NotNull(message = "must not be blank")
  val teamCode: String? = "",

  @Schema(description = "The email address of the Probation Team", example = "probation.team@justice.gov.uk")
  @field:NotNull(message = "must not be blank")
  @field:Email
  val emailAddress: String? = "",

  @Schema(description = "The ID of the local delivery unit mailbox that is linked to this team", example = "56f6fcc6-5bd1-4a37-b0fd-9ece7bd9a8c4")
  @field:NotNull(message = "must not be blank")
  val localDeliveryUnitMailboxId: String? = "",
) {
  fun asEntity(): ProbationTeam {
    return ProbationTeam(
      teamCode = teamCode,
      emailAddress = emailAddress,
      localDeliveryUnitMailbox = LocalDeliveryUnitMailbox(
        id = UUID.fromString(localDeliveryUnitMailboxId),
      ),
    )
  }
}
