package uk.gov.hmcts.cmc.claimstore.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.cmc.claimstore.documents.ClaimIssueReceiptService;
import uk.gov.hmcts.cmc.claimstore.documents.CountyCourtJudgmentPdfService;
import uk.gov.hmcts.cmc.claimstore.documents.DefendantPinLetterPdfService;
import uk.gov.hmcts.cmc.claimstore.documents.DefendantResponseReceiptService;
import uk.gov.hmcts.cmc.claimstore.documents.SealedClaimPdfService;
import uk.gov.hmcts.cmc.claimstore.documents.SettlementAgreementCopyService;
import uk.gov.hmcts.cmc.claimstore.documents.output.PDF;
import uk.gov.hmcts.cmc.claimstore.events.ccj.CountyCourtJudgmentEvent;
import uk.gov.hmcts.cmc.claimstore.events.claim.CitizenClaimIssuedEvent;
import uk.gov.hmcts.cmc.claimstore.events.offer.AgreementCountersignedEvent;
import uk.gov.hmcts.cmc.claimstore.events.response.DefendantResponseEvent;
import uk.gov.hmcts.cmc.claimstore.events.settlement.CountersignSettlementAgreementEvent;
import uk.gov.hmcts.cmc.claimstore.events.solicitor.RepresentedClaimIssuedEvent;
import uk.gov.hmcts.cmc.claimstore.services.document.DocumentsService;
import uk.gov.hmcts.cmc.domain.models.Claim;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.requireNonNull;
import static uk.gov.hmcts.cmc.claimstore.utils.DocumentNameUtils.buildClaimIssueReceiptFileBaseName;
import static uk.gov.hmcts.cmc.claimstore.utils.DocumentNameUtils.buildDefendantLetterFileBaseName;
import static uk.gov.hmcts.cmc.claimstore.utils.DocumentNameUtils.buildRequestForJudgementFileBaseName;
import static uk.gov.hmcts.cmc.claimstore.utils.DocumentNameUtils.buildResponseFileBaseName;
import static uk.gov.hmcts.cmc.claimstore.utils.DocumentNameUtils.buildSealedClaimFileBaseName;
import static uk.gov.hmcts.cmc.claimstore.utils.DocumentNameUtils.buildSettlementReachedFileBaseName;
import static uk.gov.hmcts.cmc.domain.models.ClaimDocumentType.CCJ_REQUEST;
import static uk.gov.hmcts.cmc.domain.models.ClaimDocumentType.CLAIM_ISSUE_RECEIPT;
import static uk.gov.hmcts.cmc.domain.models.ClaimDocumentType.DEFENDANT_PIN_LETTER;
import static uk.gov.hmcts.cmc.domain.models.ClaimDocumentType.DEFENDANT_RESPONSE_RECEIPT;
import static uk.gov.hmcts.cmc.domain.models.ClaimDocumentType.SEALED_CLAIM;
import static uk.gov.hmcts.cmc.domain.models.ClaimDocumentType.SETTLEMENT_AGREEMENT;

@Component
@ConditionalOnProperty(prefix = "document_management", name = "url")
public class DocumentUploadHandler {

    private static final Logger logger = LoggerFactory.getLogger(DocumentUploadHandler.class);

    private final DocumentsService documentService;
    private final ApplicationEventPublisher publisher;
    private final DefendantResponseReceiptService defendantResponseReceiptService;
    private final CountyCourtJudgmentPdfService countyCourtJudgmentPdfService;
    private final SettlementAgreementCopyService settlementAgreementCopyService;
    private final SealedClaimPdfService sealedClaimPdfService;
    private final ClaimIssueReceiptService claimIssueReceiptService;
    private final DefendantPinLetterPdfService defendantPinLetterPdfService;

    @SuppressWarnings("squid:S00107")
    @Autowired
    public DocumentUploadHandler(ApplicationEventPublisher publisher,
                                 DefendantResponseReceiptService defendantResponseReceiptService,
                                 CountyCourtJudgmentPdfService countyCourtJudgmentPdfService,
                                 SettlementAgreementCopyService settlementAgreementCopyService,
                                 SealedClaimPdfService sealedClaimPdfService,
                                 ClaimIssueReceiptService claimIssueReceiptService,
                                 DefendantPinLetterPdfService defendantPinLetterPdfService,
                                 DocumentsService documentService) {
        this.publisher = publisher;
        this.defendantResponseReceiptService = defendantResponseReceiptService;
        this.countyCourtJudgmentPdfService = countyCourtJudgmentPdfService;
        this.settlementAgreementCopyService = settlementAgreementCopyService;
        this.sealedClaimPdfService = sealedClaimPdfService;
        this.claimIssueReceiptService = claimIssueReceiptService;
        this.defendantPinLetterPdfService = defendantPinLetterPdfService;
        this.documentService = documentService;
    }

    @EventListener
    public void uploadDocument(CitizenClaimIssuedEvent event) {
        Claim claim = event.getClaim();
        requireNonNull(claim, "Claim must not be null");
        PDF sealedClaim = new PDF(buildSealedClaimFileBaseName(event.getClaim().getReferenceNumber()),
            sealedClaimPdfService.createPdf(event.getClaim()),
            SEALED_CLAIM);
        PDF defendantLetter = new PDF(buildDefendantLetterFileBaseName(event.getClaim().getReferenceNumber()),
            defendantPinLetterPdfService.createPdf(event.getClaim(), event.getPin()),
            DEFENDANT_PIN_LETTER);
        PDF claimIssueReceipt = new PDF(buildClaimIssueReceiptFileBaseName(event.getClaim().getReferenceNumber()),
            claimIssueReceiptService.createPdf(event.getClaim()),
            CLAIM_ISSUE_RECEIPT);
        uploadToDocumentManagement(event.getClaim(),
            event.getAuthorisation(),
            sealedClaim,
            defendantLetter,
            claimIssueReceipt);
    }

    @EventListener
    public void uploadDocument(RepresentedClaimIssuedEvent event) {
        Claim claim = event.getClaim();
        requireNonNull(claim, "Claim must not be null");
        PDF sealedClaim = new PDF(buildSealedClaimFileBaseName(event.getClaim().getReferenceNumber()),
            sealedClaimPdfService.createPdf(event.getClaim()),
            SEALED_CLAIM);
        uploadToDocumentManagement(event.getClaim(),
            event.getAuthorisation(),
            sealedClaim);
    }

    @EventListener
    public void uploadDocument(DefendantResponseEvent event) {
        Claim claim = event.getClaim();
        requireNonNull(claim, "Claim must not be null");
        if (!claim.getResponse().isPresent()) {
            throw new IllegalArgumentException("Response must be present");
        }
        PDF defendantResponseDocument = new PDF(buildResponseFileBaseName(claim.getReferenceNumber()),
            defendantResponseReceiptService.createPdf(claim),
            DEFENDANT_RESPONSE_RECEIPT);
        uploadToDocumentManagement(claim, event.getAuthorization(), defendantResponseDocument);
    }

    @EventListener
    public void uploadDocument(CountyCourtJudgmentEvent event) {
        Claim claim = event.getClaim();
        requireNonNull(claim, "Claim must not be null");
        PDF document = new PDF(buildRequestForJudgementFileBaseName(claim.getReferenceNumber(),
            claim.getClaimData().getDefendant().getName()),
            countyCourtJudgmentPdfService.createPdf(claim),
            CCJ_REQUEST);
        uploadToDocumentManagement(claim, event.getAuthorisation(), document);
    }

    @EventListener
    public void uploadDocument(AgreementCountersignedEvent event) {
        Claim claim = event.getClaim();
        requireNonNull(claim, "Claim must not be null");
        PDF document = new PDF(buildSettlementReachedFileBaseName(claim.getReferenceNumber()),
            settlementAgreementCopyService.createPdf(claim),
            SETTLEMENT_AGREEMENT);
        uploadToDocumentManagement(claim, event.getAuthorisation(), document);
    }

    @EventListener
    public void uploadDocument(CountersignSettlementAgreementEvent event) {
        Claim claim = event.getClaim();
        requireNonNull(claim, "Claim must not be null");
        PDF document = new PDF(buildSettlementReachedFileBaseName(claim.getReferenceNumber()),
            settlementAgreementCopyService.createPdf(claim),
            SETTLEMENT_AGREEMENT);
        uploadToDocumentManagement(claim, event.getAuthorisation(), document);
    }

    private void uploadToDocumentManagement(Claim claim, String authorisation, PDF... documents) {
        final List<PDF> documentList = newArrayList(documents);
        documentList.forEach(document -> {
            try {
                documentService.uploadToDocumentManagement(document,
                    authorisation,
                    claim);
            } catch (Exception ex) {
                logger.warn(String.format("unable to upload document %s into document management",
                    document.getFilename()), ex);
            }
        });
    }
}