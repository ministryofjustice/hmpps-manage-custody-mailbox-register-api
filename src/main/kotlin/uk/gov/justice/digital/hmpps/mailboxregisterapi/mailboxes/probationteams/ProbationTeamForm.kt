package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailbox
import java.util.*

class ProbationTeamForm(
  @field:NotNull(message = "must not be blank")
  val teamCode: String? = "",
  @field:NotNull(message = "must not be blank") @field:Email
  val emailAddress: String? = "",
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
