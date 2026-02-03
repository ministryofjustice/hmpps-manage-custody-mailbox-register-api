package uk.gov.justice.digital.hmpps.mailboxregisterapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.hmpps.kotlin.auth.healthWebClient
import java.time.Duration

@Configuration
class WebClientConfiguration(
  @Value("\${hmpps-auth.url}") val hmppsAuthBaseUri: String,
  @Value("\${api.health-timeout:2s}") val healthTimeout: Duration,
  @Value("\${api.timeout:20s}") val timeout: Duration,
) {
  @Bean
  fun webClientBuilder(): WebClient.Builder = WebClient.builder()

  // HMPPS Auth health ping is required if your service calls HMPPS Auth to get a token to call other services
  // TODO: Remove the health ping if no call outs to other services are made
  @Bean
  fun hmppsAuthHealthWebClient(): WebClient = webClientBuilder().healthWebClient(hmppsAuthBaseUri, healthTimeout)
}
