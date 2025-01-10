package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasRole('ROLE_TEMPLATE_KOTLIN__UI')")
@RequestMapping(value = ["/local-delivery-unit-mailboxes"], produces = ["application/json"])
class LocalDeliveryUnitMailboxesController(
  private val localDeliveryUnitMailboxRepository: LocalDeliveryUnitMailboxRepository
) {
  @PostMapping(value = [""])
  fun create(@RequestBody newMailbox: LocalDeliveryUnitMailbox) {
    localDeliveryUnitMailboxRepository.saveAndFlush(newMailbox)
  }
}