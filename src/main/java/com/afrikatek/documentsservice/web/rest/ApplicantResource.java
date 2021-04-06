package com.afrikatek.documentsservice.web.rest;

import com.afrikatek.documentsservice.repository.ApplicantRepository;
import com.afrikatek.documentsservice.service.ApplicantService;
import com.afrikatek.documentsservice.service.DeclarationService;
import com.afrikatek.documentsservice.service.DemographicDetailsService;
import com.afrikatek.documentsservice.service.GuardianService;
import com.afrikatek.documentsservice.service.MarriageDetailsService;
import com.afrikatek.documentsservice.service.NextOfKeenService;
import com.afrikatek.documentsservice.service.dto.ApplicantDTO;
import com.afrikatek.documentsservice.service.dto.AppointmentSlotDTO;
import com.afrikatek.documentsservice.service.dto.DeclarationDTO;
import com.afrikatek.documentsservice.service.dto.DemographicDetailsDTO;
import com.afrikatek.documentsservice.service.dto.GuardianDTO;
import com.afrikatek.documentsservice.service.dto.MarriageDetailsDTO;
import com.afrikatek.documentsservice.service.dto.NextOfKeenDTO;
import com.afrikatek.documentsservice.service.dto.UserDTO;
import com.afrikatek.documentsservice.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.afrikatek.documentsservice.domain.Applicant}.
 */
@RestController
@RequestMapping("/api")
public class ApplicantResource {

    private final Logger log = LoggerFactory.getLogger(ApplicantResource.class);

    private static final String ENTITY_NAME = "applicant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApplicantService applicantService;

    private final ApplicantRepository applicantRepository;

    private final NextOfKeenService nextOfKeenService;

    public ApplicantResource(
        ApplicantService applicantService,
        ApplicantRepository applicantRepository,
        NextOfKeenService nextOfKeenService
    ) {
        this.applicantService = applicantService;
        this.applicantRepository = applicantRepository;
        this.nextOfKeenService = nextOfKeenService;
    }

    /**
     * {@code POST  /applicants} : Create a new applicant.
     *
     * @param applicantDTO the applicantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new applicantDTO, or with status {@code 400 (Bad Request)} if the applicant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/applicants")
    public ResponseEntity<ApplicantDTO> createApplicant(@Valid @RequestBody ApplicantDTO applicantDTO) throws URISyntaxException {
        log.debug("REST request to save Applicant : {}", applicantDTO);
        if (applicantDTO.getId() != null) {
            throw new BadRequestAlertException("A new applicant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ApplicantDTO result = applicantService.save(applicantDTO);
        return ResponseEntity
            .created(new URI("/api/applicants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /applicants/:id} : Updates an existing applicant.
     *
     * @param id the id of the applicantDTO to save.
     * @param applicantDTO the applicantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicantDTO,
     * or with status {@code 400 (Bad Request)} if the applicantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the applicantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/applicants/{id}")
    public ResponseEntity<ApplicantDTO> updateApplicant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApplicantDTO applicantDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Applicant : {}, {}", id, applicantDTO);
        if (applicantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ApplicantDTO result = applicantService.save(applicantDTO);

        /**
         * Finding other details
         */
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, applicantDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /applicants/:id} : Partial updates given fields of an existing applicant, field will ignore if it is null
     *
     * @param id the id of the applicantDTO to save.
     * @param applicantDTO the applicantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicantDTO,
     * or with status {@code 400 (Bad Request)} if the applicantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the applicantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the applicantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/applicants/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ApplicantDTO> partialUpdateApplicant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApplicantDTO applicantDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Applicant partially : {}, {}", id, applicantDTO);
        if (applicantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApplicantDTO> result = applicantService.partialUpdate(applicantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, applicantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /applicants} : get all the applicants.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applicants in body.
     */
    @GetMapping("/applicants")
    public ResponseEntity<List<ApplicantDTO>> getAllApplicants(Pageable pageable, @RequestParam(required = false) String filter) {
        if ("marriagedetails-is-null".equals(filter)) {
            log.debug("REST request to get all Applicants where marriageDetails is null");
            return new ResponseEntity<>(applicantService.findAllWhereMarriageDetailsIsNull(), HttpStatus.OK);
        }
        if ("nextofkeen-is-null".equals(filter)) {
            log.debug("REST request to get all Applicants where nextOfKeen is null");
            return new ResponseEntity<>(applicantService.findAllWhereNextOfKeenIsNull(), HttpStatus.OK);
        }
        log.debug("REST request to get a page of Applicants");
        Page<ApplicantDTO> page = applicantService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /applicants/:id} : get the "id" applicant.
     *
     * @param id the id of the applicantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the applicantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/applicants/{id}")
    public ResponseEntity<ApplicantDTO> getApplicant(@PathVariable Long id) {
        log.debug("REST request to get Applicant : {}", id);
        Optional<ApplicantDTO> applicantDTO = applicantService.findOne(id);

        return ResponseUtil.wrapOrNotFound(applicantDTO);
    }

    /**
     * {@code DELETE  /applicants/:id} : delete the "id" applicant.
     *
     * @param id the id of the applicantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/applicants/{id}")
    public ResponseEntity<Void> deleteApplicant(@PathVariable Long id) {
        log.debug("REST request to delete Applicant : {}", id);
        applicantService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
