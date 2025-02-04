package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ProbationTeamsRepository : JpaRepository<ProbationTeam, UUID> {
  fun findByEmailAddressIgnoreCase(email: String): Optional<ProbationTeam>
}
