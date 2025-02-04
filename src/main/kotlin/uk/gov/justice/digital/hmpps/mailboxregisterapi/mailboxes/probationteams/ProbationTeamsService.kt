package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams

import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.servlet.resource.NoResourceFoundException
import uk.gov.justice.digital.hmpps.mailboxregisterapi.FailedValidationException
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailboxService
import java.util.UUID

@Service
class ProbationTeamsService(
  private val repository: ProbationTeamsRepository,
  private val localDeliveryUnitMailboxService: LocalDeliveryUnitMailboxService,
) {
  fun createProbationTeam(probationTeamForm: ProbationTeamForm): ProbationTeam {
    if (!localDeliveryUnitMailboxService.mailboxExists(UUID.fromString(probationTeamForm.localDeliveryUnitMailboxId))) {
      throw FailedValidationException(mapOf("localDeliveryUnitMailboxId" to "must be a valid LDU"))
    }

    return repository.saveAndFlush(probationTeamForm.asEntity())
  }

  fun byId(id: UUID): ProbationTeam {
    return repository.findById(id).orElseThrow { NoResourceFoundException(HttpMethod.GET, id.toString()) }
  }
}
