package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.LocalDeliveryUnitMailbox
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.LocalDeliveryUnitMailboxRepository

class CreatingMailboxesTest {
  @Nested
  inner class LduMailboxes : IntegrationTestBase() {
    @Autowired
    lateinit var localDeliveryUnitMailboxes: LocalDeliveryUnitMailboxRepository

    @Test
    fun `are created by submitting the correct details`() {
      val localDeliveryUnitMailbox = LocalDeliveryUnitMailbox(
        unitCode = "UNIT_CODE",
        areaCode = "AREA_CODE",
        name = "Mailbox Name",
        emailAddress = "ldu@example.com",
        country = "England"
      )

      webTestClient.post()
        .uri("/local-delivery-unit-mailboxes")
        .headers(setAuthorisation(roles = listOf("ROLE_TEMPLATE_KOTLIN__UI")))
        .bodyValue(localDeliveryUnitMailbox)
        .exchange()
        .expectStatus().isOk

      assertThat(localDeliveryUnitMailboxes.count()).isOne()
      val createdLocalDeliveryUnitMailbox = localDeliveryUnitMailboxes.findAll().first()
      assertThat(createdLocalDeliveryUnitMailbox.unitCode).isEqualTo("UNIT_CODE")
      assertThat(createdLocalDeliveryUnitMailbox.areaCode).isEqualTo("AREA_CODE")
      assertThat(createdLocalDeliveryUnitMailbox.name).isEqualTo("Mailbox Name")
      assertThat(createdLocalDeliveryUnitMailbox.emailAddress).isEqualTo("ldu@example.com")
      assertThat(createdLocalDeliveryUnitMailbox.country).isEqualTo("England")
    }

    @ParameterizedTest
    @ValueSource(strings = ["unitCode", "areaCode", "emailAddress"])
    fun `without a required field are not created`(nullFieldName: String) {
      val attributes = HashMap<String, String?>()
      attributes["unitCode"] = "UNIT_CODE"
      attributes["areaCode"] = "AREA_CODE"
      attributes["name"] = "Mailbox Name"
      attributes["emailAddress"] = "ldu@example.com"
      attributes["country"] = "England"
      attributes[nullFieldName] = null

      webTestClient.post()
        .uri("/local-delivery-unit-mailboxes")
        .headers(setAuthorisation(roles = listOf("ROLE_TEMPLATE_KOTLIN__UI")))
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(attributes))
        .exchange()
        .expectStatus()
//        .expectStatus().isBadRequest

      assertThat(localDeliveryUnitMailboxes.count()).isZero
    }
  }
}