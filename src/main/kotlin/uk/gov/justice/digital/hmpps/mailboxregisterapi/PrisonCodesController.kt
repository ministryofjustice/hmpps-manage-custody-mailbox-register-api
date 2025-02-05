package uk.gov.justice.digital.hmpps.mailboxregisterapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@PreAuthorize("hasRole('MANAGE_CUSTODY_MAILBOX_REGISTER_ADMIN')")
@RequestMapping(value = ["/prison-codes"], produces = ["application/json"])
class PrisonCodesController {
  @GetMapping(value = [""])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Provides a list of prison codes and names",
    description = "Provides a list of prison codes and names",
    security = [SecurityRequirement(name = "mailbox-register-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "201", description = "The offender management unit mailbox was created"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun listPrisonCodes() = mapOf("prisons" to PrisonCode.entries.associateWith { it.prisonName })
}
