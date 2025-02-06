package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams

import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.servlet.resource.NoResourceFoundException
import uk.gov.justice.digital.hmpps.mailboxregisterapi.FailedValidationException
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailbox
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailboxService
import java.util.UUID

@Service
class ProbationTeamsService(
  private val repository: ProbationTeamsRepository,
  private val localDeliveryUnitMailboxService: LocalDeliveryUnitMailboxService,
) {
  fun createProbationTeam(probationTeamForm: ProbationTeamForm): ProbationTeam {
    if (existingLocalDeliveryUnitMailbox(probationTeamForm) == null) {
      throw FailedValidationException(mapOf("localDeliveryUnitMailboxId" to "must be a valid LDU"))
    }

    return repository.saveAndFlush(probationTeamForm.asEntity())
  }

  fun byId(id: UUID): ProbationTeam {
    return repository.findById(id).orElseThrow { NoResourceFoundException(HttpMethod.GET, id.toString()) }
  }

  fun updateProbationTeam(id: UUID, probationTeamForm: ProbationTeamForm): ProbationTeam {
    if (existingLocalDeliveryUnitMailbox(probationTeamForm) == null) {
      throw FailedValidationException(mapOf("localDeliveryUnitMailboxId" to "must be a valid LDU"))
    }

    val existingProbationTeam = byId(id)

    existingProbationTeam.apply {
      teamCode = probationTeamForm.teamCode
      localDeliveryUnitMailbox = LocalDeliveryUnitMailbox(id = UUID.fromString(probationTeamForm.localDeliveryUnitMailboxId))
      emailAddress = probationTeamForm.emailAddress
    }

    return repository.save(existingProbationTeam)
  }

  fun all(): List<ProbationTeam> = repository.findAll()

  fun deleteById(id: UUID) = repository.deleteById(id)

  private fun existingLocalDeliveryUnitMailbox(probationTeamForm: ProbationTeamForm): LocalDeliveryUnitMailbox? {
    val id = UUID.fromString(probationTeamForm.localDeliveryUnitMailboxId)
    return localDeliveryUnitMailboxService.runCatching { getMailboxById(id) }.getOrNull()
  }
}
