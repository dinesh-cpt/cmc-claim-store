package uk.gov.hmcts.cmc.ccd.domain;

import lombok.Builder;
import lombok.Value;
import uk.gov.hmcts.cmc.ccd.domain.defendant.CCDDefenceType;
import uk.gov.hmcts.cmc.ccd.domain.defendant.CCDPartyStatement;
import uk.gov.hmcts.cmc.ccd.domain.defendant.CCDResponseType;
import uk.gov.hmcts.cmc.ccd.domain.defendant.CCDStatementOfMeans;
import uk.gov.hmcts.cmc.ccd.domain.evidence.CCDEvidenceRow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class CCDDefendant {
    private String letterHolderId;

    private CCDPartyType claimantProvidedType;
    private String claimantProvidedEmail;
    private CCDAddress claimantProvidedServiceAddress;
    private String claimantProvidedName;
    private String claimantProvidedPhoneNumber;
    private CCDAddress claimantProvidedAddress;
    private CCDAddress claimantProvidedCorrespondenceAddress;
    private LocalDate claimantProvidedDateOfBirth;
    private String claimantProvidedContactPerson;
    private String claimantProvidedCompaniesHouseNumber;
    private String claimantProvidedTitle;
    private String claimantProvidedBusinessName;

    private String representativeOrganisationName;
    private CCDAddress representativeOrganisationAddress;
    private String representativeOrganisationPhone;
    private String representativeOrganisationEmail;
    private String representativeOrganisationDxAddress;

    private CCDPartyType partyType;
    private String partyTitle;
    private String partyName;
    private LocalDate partyDateOfBirth;
    private String partyPhone;
    private String partyEmail;
    private CCDAddress partyAddress;
    private CCDAddress partyCorrespondenceAddress;
    private String partyBusinessName;
    private String partyContactPerson;
    private String partyCompaniesHouseNumber;
    private CCDAddress partyServiceAddress;

    private LocalDateTime responseSubmittedDateTime;
    private LocalDate responseDeadline;
    private CCDResponseType responseType;
    private BigDecimal responseAmount;
    private LocalDate paymentDeclarationPaidDate;
    private String paymentDeclarationExplanation;
    private List<CCDCollectionElement<CCDTimelineEvent>> defendantTimeLineEvents;
    private String defendantTimeLineComment;
    private List<CCDCollectionElement<CCDEvidenceRow>> responseEvidenceRows;
    private String responseEvidenceComment;
    private CCDDefenceType responseDefenceType;
    private String responseDefence;
    private CCDYesNoOption responseFreeMediationOption;
    private CCDYesNoOption responseMoreTimeNeededOption;
    private String responseDefendantSOTSignerName;
    private String responseDefendantSOTSignerRole;

    private CCDPaymentIntention defendantPaymentIntention;
    private CCDStatementOfMeans statementOfMeans;

    private LocalDate paidInFullDate;

    private List<CCDCollectionElement<CCDPartyStatement>> settlementPartyStatements;
    private LocalDate settlementReachedAt;

    private CCJRequest countyCourtJudgement;
    private LocalDate ccjRequestedDate;
}
