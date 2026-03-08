package za.co.pocketot.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;
import za.co.pocketot.repository.PractitionerRepository;
import za.co.pocketot.service.PractitionerService;
import za.co.pocketot.service.dto.PractitionerDTO;
import za.co.pocketot.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link za.co.pocketot.domain.Practitioner}.
 */
@RestController
@RequestMapping("/api/practitioners")
public class PractitionerResource {

    private static final Logger LOG = LoggerFactory.getLogger(PractitionerResource.class);

    private static final String ENTITY_NAME = "practitioner";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PractitionerService practitionerService;

    private final PractitionerRepository practitionerRepository;

    public PractitionerResource(PractitionerService practitionerService, PractitionerRepository practitionerRepository) {
        this.practitionerService = practitionerService;
        this.practitionerRepository = practitionerRepository;
    }

    /**
     * {@code POST  /practitioners} : Create a new practitioner.
     *
     * @param practitionerDTO the practitionerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new practitionerDTO, or with status {@code 400 (Bad Request)} if the practitioner has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PractitionerDTO>> createPractitioner(@RequestBody PractitionerDTO practitionerDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Practitioner : {}", practitionerDTO);
        if (practitionerDTO.getId() != null) {
            throw new BadRequestAlertException("A new practitioner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return practitionerService
            .save(practitionerDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/practitioners/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /practitioners/:id} : Updates an existing practitioner.
     *
     * @param id the id of the practitionerDTO to save.
     * @param practitionerDTO the practitionerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated practitionerDTO,
     * or with status {@code 400 (Bad Request)} if the practitionerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the practitionerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PractitionerDTO>> updatePractitioner(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PractitionerDTO practitionerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Practitioner : {}, {}", id, practitionerDTO);
        if (practitionerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, practitionerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return practitionerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return practitionerService
                    .update(practitionerDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /practitioners/:id} : Partial updates given fields of an existing practitioner, field will ignore if it is null
     *
     * @param id the id of the practitionerDTO to save.
     * @param practitionerDTO the practitionerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated practitionerDTO,
     * or with status {@code 400 (Bad Request)} if the practitionerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the practitionerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the practitionerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PractitionerDTO>> partialUpdatePractitioner(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PractitionerDTO practitionerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Practitioner partially : {}, {}", id, practitionerDTO);
        if (practitionerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, practitionerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return practitionerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PractitionerDTO> result = practitionerService.partialUpdate(practitionerDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /practitioners} : get all the practitioners.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of practitioners in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<PractitionerDTO>> getAllPractitioners() {
        LOG.debug("REST request to get all Practitioners");
        return practitionerService.findAll().collectList();
    }

    /**
     * {@code GET  /practitioners} : get all the practitioners as a stream.
     * @return the {@link Flux} of practitioners.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PractitionerDTO> getAllPractitionersAsStream() {
        LOG.debug("REST request to get all Practitioners as a stream");
        return practitionerService.findAll();
    }

    /**
     * {@code GET  /practitioners/:id} : get the "id" practitioner.
     *
     * @param id the id of the practitionerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the practitionerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PractitionerDTO>> getPractitioner(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Practitioner : {}", id);
        Mono<PractitionerDTO> practitionerDTO = practitionerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(practitionerDTO);
    }

    /**
     * {@code DELETE  /practitioners/:id} : delete the "id" practitioner.
     *
     * @param id the id of the practitionerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePractitioner(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Practitioner : {}", id);
        return practitionerService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
