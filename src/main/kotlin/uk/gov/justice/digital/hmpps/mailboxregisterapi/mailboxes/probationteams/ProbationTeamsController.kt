package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.mailboxregisterapi.HAS_READ_MAILBOXES
import uk.gov.justice.digital.hmpps.mailboxregisterapi.HAS_SYSTEM_USER
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ValidationErrorResponse
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLog
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import java.util.*

@RestController
@RequestMapping(value = ["/probation-teams"], produces = ["application/json"])
class ProbationTeamsController(
  private val probationTeamsService: ProbationTeamsService,
  private val auditLog: AuditLog,
) {
  @PreAuthorize(HAS_SYSTEM_USER)
  @PostMapping(value = [""])
  @ResponseStatus(code = HttpStatus.CREATED)
  @Operation(
    summary = "Creates a new local delivery unit mailbox",
    description = "Creates a new local delivery unit mailbox",
    security = [SecurityRequirement(name = "system-user-role")],
    responses = [
      ApiResponse(responseCode = "201", description = "The local delivery unit mailbox was created"),
      ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationErrorResponse::class))],
      ),
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
  fun create(@Valid @RequestBody probationTeamForm: ProbationTeamForm) = probationTeamsService.createProbationTeam(probationTeamForm)
    .also { auditLog.logCreationOf(it) }

  @PreAuthorize(HAS_SYSTEM_USER)
  @PutMapping(value = ["/{id}"])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Updates a local delivery unit mailbox",
    description = "Updates a local delivery unit mailbox",
    security = [SecurityRequirement(name = "system-user-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "The local delivery unit mailbox was updated"),
      ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationErrorResponse::class))],
      ),
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
      ApiResponse(
        responseCode = "404",
        description = "The local delivery unit mailbox was not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun update(@PathVariable(name = "id") id: UUID, @Valid @RequestBody probationTeamForm: ProbationTeamForm) = auditLog.logUpdatesTo(
    probationTeamsService.byId(id),
    probationTeamsService.updateProbationTeam(id, probationTeamForm),
  )

  @PreAuthorize(HAS_READ_MAILBOXES)
  @GetMapping(value = [""])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Lists all the probation teams",
    description = "Lists all the probation teams",
    security = [SecurityRequirement(name = "system-user-role"), SecurityRequirement(name = "mailboxes-ro-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "A list of probation teams"),
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
  fun list() = probationTeamsService.all()

  @PreAuthorize(HAS_READ_MAILBOXES)
  @GetMapping(value = ["/{id}"])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Gets a probation team by ID",
    description = "Gets a probation team by ID",
    security = [SecurityRequirement(name = "system-user-role"), SecurityRequirement(name = "mailboxes-ro-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "The requested probation team"),
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
      ApiResponse(
        responseCode = "404",
        description = "The local delivery unit mailbox was not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun getById(@PathVariable(name = "id") id: UUID) = probationTeamsService.byId(id)

  @PreAuthorize(HAS_SYSTEM_USER)
  @DeleteMapping(value = ["/{id}"])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Deletes a specified probation team",
    description = "Deletes a specified probation team",
    security = [SecurityRequirement(name = "system-user-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "The specified probation team was deleted"),
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
      ApiResponse(
        responseCode = "404",
        description = "The local delivery unit mailbox was not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun delete(@PathVariable(name = "id") id: UUID) = probationTeamsService.deleteById(id)
    .also { auditLog.logDeletionOf(ProbationTeam(id = id)) }
}
