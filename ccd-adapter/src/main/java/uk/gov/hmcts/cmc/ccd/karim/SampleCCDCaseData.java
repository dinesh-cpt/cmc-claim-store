package uk.gov.hmcts.cmc.ccd.karim;

import com.google.common.collect.ImmutableList;
import uk.gov.hmcts.cmc.ccd.domain.CCDAddress;
import uk.gov.hmcts.cmc.ccd.domain.CCDAmountRow;
import uk.gov.hmcts.cmc.ccd.domain.CCDApplicant;
import uk.gov.hmcts.cmc.ccd.domain.CCDCase;
import uk.gov.hmcts.cmc.ccd.domain.CCDClaimDocument;
import uk.gov.hmcts.cmc.ccd.domain.CCDClaimDocumentType;
import uk.gov.hmcts.cmc.ccd.domain.CCDClaimSubmissionOperationIndicators;
import uk.gov.hmcts.cmc.ccd.domain.CCDCollectionElement;
import uk.gov.hmcts.cmc.ccd.domain.CCDDocument;
import uk.gov.hmcts.cmc.ccd.domain.CCDInterestDateType;
import uk.gov.hmcts.cmc.ccd.domain.CCDInterestEndDateType;
import uk.gov.hmcts.cmc.ccd.domain.CCDInterestType;
import uk.gov.hmcts.cmc.ccd.domain.CCDParty;
import uk.gov.hmcts.cmc.ccd.domain.CCDPaymentIntention;
import uk.gov.hmcts.cmc.ccd.domain.CCDPaymentOption;
import uk.gov.hmcts.cmc.ccd.domain.CCDPaymentSchedule;
import uk.gov.hmcts.cmc.ccd.domain.CCDTelephone;
import uk.gov.hmcts.cmc.ccd.domain.CCDTimelineEvent;
import uk.gov.hmcts.cmc.ccd.domain.claimantresponse.CCDCourtDetermination;
import uk.gov.hmcts.cmc.ccd.domain.claimantresponse.CCDFormaliseOption;
import uk.gov.hmcts.cmc.ccd.domain.claimantresponse.CCDResponseAcceptation;
import uk.gov.hmcts.cmc.ccd.domain.claimantresponse.CCDResponseRejection;
import uk.gov.hmcts.cmc.ccd.domain.defendant.CCDRespondent;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDBankAccount;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDChildCategory;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDCourtOrder;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDDebt;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDDisabilityStatus;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDExpense;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDIncome;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDLivingPartner;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDPriorityDebt;
import uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDStatementOfMeans;
import uk.gov.hmcts.cmc.ccd.domain.directionsquestionnaire.CCDDirectionsQuestionnaire;
import uk.gov.hmcts.cmc.ccd.domain.evidence.CCDEvidenceRow;
import uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDDirectionPartyType;
import uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDHearingCourtType;
import uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDHearingDurationType;
import uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOrderDirection;
import uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOrderGenerationData;
import uk.gov.hmcts.cmc.domain.utils.LocalDateTimeFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static uk.gov.hmcts.cmc.ccd.domain.AmountType.BREAK_DOWN;
import static uk.gov.hmcts.cmc.ccd.domain.AmountType.RANGE;
import static uk.gov.hmcts.cmc.ccd.domain.CCDPartyType.COMPANY;
import static uk.gov.hmcts.cmc.ccd.domain.CCDPartyType.INDIVIDUAL;
import static uk.gov.hmcts.cmc.ccd.domain.CCDPartyType.ORGANISATION;
import static uk.gov.hmcts.cmc.ccd.domain.CCDPartyType.SOLE_TRADER;
import static uk.gov.hmcts.cmc.ccd.domain.CCDYesNoOption.NO;
import static uk.gov.hmcts.cmc.ccd.domain.CCDYesNoOption.YES;
import static uk.gov.hmcts.cmc.ccd.domain.claimantresponse.CCDDecisionType.COURT;
import static uk.gov.hmcts.cmc.ccd.domain.claimantresponse.CCDFormaliseOption.SETTLEMENT;
import static uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDAgeGroupType.UNDER_11;
import static uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDBankAccountType.SAVINGS_ACCOUNT;
import static uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDDisabilityStatus.SEVERE;
import static uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDExpenseType.COUNCIL_TAX;
import static uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDIncomeType.JOB;
import static uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDPaymentFrequency.MONTH;
import static uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDPriorityDebtType.ELECTRICITY;
import static uk.gov.hmcts.cmc.ccd.domain.defendant.statementofmeans.CCDResidenceType.JOINT_OWN_HOME;
import static uk.gov.hmcts.cmc.ccd.domain.evidence.CCDEvidenceType.EXPERT_WITNESS;
import static uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOrderDirectionType.DOCUMENTS;
import static uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOrderDirectionType.EXPERT_REPORT_PERMISSION;
import static uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOrderDirectionType.EYEWITNESS;
import static uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOrderDirectionType.OTHER;
import static uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOtherDirectionHeaderType.UPLOAD;
import static uk.gov.hmcts.cmc.ccd.karim.SampleCCDClaimSubmissionOperationIndicators.defaultCCDClaimSubmissionOperationIndicators;
import static uk.gov.hmcts.cmc.ccd.karim.SampleCCDClaimSubmissionOperationIndicators.defaultCCDClaimSubmissionOperationIndicatorsAllYes;
import static uk.gov.hmcts.cmc.domain.models.particulars.DamagesExpectation.MORE_THAN_THOUSAND_POUNDS;
import static uk.gov.hmcts.cmc.domain.models.particulars.DamagesExpectation.THOUSAND_POUNDS_OR_LESS;

public class SampleCCDCaseData {

    public static final String AMOUNT = "12398";

    //Utility class
    private SampleCCDCaseData() {
    }

    private static CCDCase.CCDCaseBuilder ccdBuilderWithDefault() {

        return CCDCase.builder()
            .id(1L)
            .submittedOn(LocalDateTime.of(2017, 11, 01, 10, 15, 30))
            .issuedOn(LocalDate.of(2017, 11, 15))
            .submitterEmail("my@email.com")
            .submitterId("123")
            .previousServiceCaseReference("ref no")
            .externalId(UUID.randomUUID().toString())
            .features("admissions")
            .amountType(BREAK_DOWN)
            .housingDisrepairCostOfRepairDamages(MORE_THAN_THOUSAND_POUNDS.name())
            .housingDisrepairOtherDamages(THOUSAND_POUNDS_OR_LESS.name())
            .personalInjuryGeneralDamages(MORE_THAN_THOUSAND_POUNDS.name())
            .sotSignerName("name")
            .sotSignerRole("role")
            .externalReferenceNumber("external ref")
            .externalId(UUID.randomUUID().toString())
            .feeCode("X1202")
            .feeAmountInPennies("100")
            .reason("Reason for the case")
            .preferredCourt("London Court")
            .interestType(CCDInterestType.DIFFERENT)
            .interestReason("reason")
            .interestRate(BigDecimal.valueOf(2))
            .interestBreakDownAmount("21000")
            .interestBreakDownExplanation("Explanation")
            .interestStartDateReason("start date reason")
            .interestDateType(CCDInterestDateType.CUSTOM)
            .interestClaimStartDate(LocalDate.now())
            .interestSpecificDailyAmount("1000")
            .interestEndDateType(CCDInterestEndDateType.SUBMISSION)
            .paymentStatus("success")
            .paymentDateCreated(LocalDate.of(2019, 01, 01))
            .paymentId("PaymentId")
            .paymentAmount("400000")
            .paymentReference("RC-1524-6488-1670-7520")
            .claimSubmissionOperationIndicators(defaultCCDClaimSubmissionOperationIndicators)
            .timeline(singletonList(CCDCollectionElement.<CCDTimelineEvent>builder()
                .value(CCDTimelineEvent.builder().date("some Date").description("description of event").build())
                .build()))
            .evidence(singletonList(CCDCollectionElement.<CCDEvidenceRow>builder()
                .value(CCDEvidenceRow.builder().type(EXPERT_WITNESS).description("description of evidence").build())
                .build()))
            .caseDocuments(ImmutableList.of(CCDCollectionElement.<CCDClaimDocument>builder()
                .value(CCDClaimDocument.builder()
                    .documentLink(CCDDocument.builder()
                        .documentUrl("http://some-url")
                        .documentBinaryUrl("http://some-url")
                        .documentFileName("document file name")
                        .build()
                    )
                    .documentType(CCDClaimDocumentType.SEALED_CLAIM)
                    .size(3457L)
                    .createdDatetime(LocalDateTimeFactory.nowInUTC())
                    .build())
                .build())
            );
    }

    public static CCDResponseAcceptation getResponseAcceptation(CCDFormaliseOption formaliseOption) {
        return CCDResponseAcceptation.builder()
            .amountPaid(AMOUNT)
            .claimantPaymentIntention(getCCDPaymentIntention())
            .submittedOn(LocalDateTimeFactory.nowInLocalZone())
            .formaliseOption(formaliseOption)
            .build();
    }

    public static CCDResponseAcceptation getResponseAcceptationWithClaimantPaymentIntentionImmediately() {
        return CCDResponseAcceptation.builder()
            .amountPaid(AMOUNT)
            .claimantPaymentIntention(getCCDPaymentIntentionImmediately())
            .submittedOn(LocalDateTimeFactory.nowInLocalZone())
            .formaliseOption(SETTLEMENT)
            .build();
    }

    public static CCDResponseAcceptation getResponseAcceptationWithClaimantPaymentIntentionPayBySetDate() {
        return CCDResponseAcceptation.builder()
            .amountPaid(AMOUNT)
            .claimantPaymentIntention(getCCDPaymentIntentionPayBySetDate())
            .submittedOn(LocalDateTimeFactory.nowInLocalZone())
            .formaliseOption(SETTLEMENT)
            .build();
    }

    public static CCDResponseRejection getResponseRejection() {
        return CCDResponseRejection.builder()
            .amountPaid(AMOUNT)
            .submittedOn(LocalDateTimeFactory.nowInLocalZone())
            .freeMediationOption(YES)
            .mediationPhoneNumber(CCDTelephone.builder().telephoneNumber("07999999999").build())
            .mediationContactPerson("Mediation Contact Person")
            .reason("Rejection Reason")
            .build();
    }

    public static CCDCourtDetermination getCCDCourtDetermination() {
        return CCDCourtDetermination.builder()
            .rejectionReason("Rejection reason")
            .courtIntention(getCCDPaymentIntention())
            .courtDecision(getCCDPaymentIntention())
            .disposableIncome("30000")
            .decisionType(COURT)
            .build();
    }

    public static CCDCourtDetermination getCCDCourtDeterminationImmediately() {
        return CCDCourtDetermination.builder()
            .rejectionReason("Rejection reason")
            .courtIntention(getCCDPaymentIntentionImmediately())
            .courtDecision(getCCDPaymentIntention())
            .disposableIncome("30000")
            .decisionType(COURT)
            .build();
    }

    public static CCDCourtDetermination getCCDCourtDeterminationPayBySetDate() {
        return CCDCourtDetermination.builder()
            .rejectionReason("Rejection reason")
            .courtIntention(getCCDPaymentIntentionPayBySetDate())
            .courtDecision(getCCDPaymentIntentionPayBySetDate())
            .disposableIncome("30000")
            .decisionType(COURT)
            .build();
    }

    public static CCDPaymentIntention getCCDPaymentIntention() {
        return CCDPaymentIntention.builder()
            .paymentDate(LocalDate.of(2017, 10, 12))
            .paymentOption(CCDPaymentOption.INSTALMENTS)
            .firstPaymentDate(LocalDate.of(2017, 10, 12))
            .instalmentAmount(AMOUNT)
            .paymentSchedule(CCDPaymentSchedule.EACH_WEEK)
            .completionDate(LocalDate.of(2018, 10, 12))
            .build();
    }

    private static CCDPaymentIntention getCCDPaymentIntentionImmediately() {
        return CCDPaymentIntention.builder()
            .paymentDate(LocalDate.now())
            .paymentOption(CCDPaymentOption.INSTALMENTS)
            .build();
    }

    private static CCDPaymentIntention getCCDPaymentIntentionPayBySetDate() {
        return CCDPaymentIntention.builder()
            .paymentDate(LocalDate.now().plusDays(10))
            .paymentOption(CCDPaymentOption.INSTALMENTS)
            .build();
    }

    public static List<CCDCollectionElement<CCDAmountRow>> getAmountBreakDown() {
        return singletonList(CCDCollectionElement.<CCDAmountRow>builder().value(CCDAmountRow.builder()
            .amount("5000")
            .reason("payment")
            .build()).build());
    }

    public static CCDAddress getCCDAddress() {
        return CCDAddress.builder()
            .addressLine1("line1")
            .addressLine2("line2")
            .addressLine3("line3")
            .postTown("city")
            .postCode("postcode")
            .build();
    }

    public static CCDRespondent getCCDRespondentIndividual() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDRespondent.builder()
            .partyName("Mary Richards")
            .claimantProvidedDetail(
                CCDParty.builder()
                    .type(INDIVIDUAL)
                    .primaryAddress(ccdAddress)
                    .dateOfBirth(LocalDate.of(1950, 01, 01))
                    .correspondenceAddress(ccdAddress)
                    .build())
            .partyDetail(
                CCDParty.builder()
                    .emailAddress("defendant@email.test")
                    .build()
            )
            .claimantProvidedPartyName("Individual")
            .claimantProvidedRepresentativeOrganisationAddress(ccdAddress)
            .claimantProvidedRepresentativeOrganisationName("My Org")
            .claimantProvidedRepresentativeOrganisationPhone("07987654321")
            .claimantProvidedRepresentativeOrganisationPhone("my@email.com")
            .claimantProvidedRepresentativeOrganisationDxAddress("dx123")
            .responseDeadline(LocalDate.of(2019, 01, 21))
            .build();
    }

    public static CCDRespondent getCCDRespondentIndividualWithDefault() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDRespondent.builder()
            .partyName("Mary Richards")
            .claimantProvidedDetail(
                CCDParty.builder()
                    .type(INDIVIDUAL)
                    .primaryAddress(ccdAddress)
                    .dateOfBirth(LocalDate.of(1950, 01, 01))
                    .correspondenceAddress(ccdAddress)
                    .build())
            .partyDetail(
                CCDParty.builder()
                    .emailAddress("defendant@email.test")
                    .build()
            )
            .claimantProvidedPartyName("Individual")
            .responseDeadline(LocalDate.of(2019, 01, 21))
            .build();
    }

    public static CCDRespondent getCCDRespondentOrganisation() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDRespondent.builder()
            .claimantProvidedDetail(CCDParty.builder()
                .type(ORGANISATION)
                .primaryAddress(ccdAddress)
                .correspondenceAddress(ccdAddress)
                .contactPerson("MR. Hyde")
                .companiesHouseNumber("12345678")
                .build()
            )
            .claimantProvidedPartyName("Organisation")
            .claimantProvidedRepresentativeOrganisationAddress(ccdAddress)
            .claimantProvidedRepresentativeOrganisationName("My Org")
            .claimantProvidedRepresentativeOrganisationPhone("07987654321")
            .claimantProvidedRepresentativeOrganisationPhone("my@email.com")
            .claimantProvidedRepresentativeOrganisationDxAddress("dx123")
            .build();
    }

    public static CCDRespondent getCCDRespondentCompany() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDRespondent.builder()
            .claimantProvidedDetail(CCDParty.builder()
                .type(COMPANY)
                .primaryAddress(ccdAddress)
                .correspondenceAddress(ccdAddress)
                .contactPerson("MR. Hyde")
                .build())
            .claimantProvidedPartyName("Abc Ltd")
            .claimantProvidedRepresentativeOrganisationAddress(ccdAddress)
            .claimantProvidedRepresentativeOrganisationName("My Org")
            .claimantProvidedRepresentativeOrganisationPhone("07987654321")
            .claimantProvidedRepresentativeOrganisationPhone("my@email.com")
            .claimantProvidedRepresentativeOrganisationDxAddress("dx123")
            .build();
    }

    public static CCDRespondent getCCDRespondentSoleTrader() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDRespondent.builder()
            .claimantProvidedDetail(CCDParty.builder()
                .type(SOLE_TRADER)
                .primaryAddress(ccdAddress)
                .title("Mr.")
                .businessName("My Trade")
                .correspondenceAddress(ccdAddress)
                .build()
            )
            .claimantProvidedPartyName("SoleTrader")
            .claimantProvidedRepresentativeOrganisationAddress(ccdAddress)
            .claimantProvidedRepresentativeOrganisationName("My Org")
            .claimantProvidedRepresentativeOrganisationPhone("07987654321")
            .claimantProvidedRepresentativeOrganisationPhone("my@email.com")
            .claimantProvidedRepresentativeOrganisationDxAddress("dx123")
            .build();
    }

    public static CCDApplicant getCCDApplicantIndividual() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDApplicant.builder()
            .partyDetail(CCDParty.builder()
                .type(INDIVIDUAL)
                .primaryAddress(ccdAddress)
                .emailAddress("claimant@email.test")
                .telephoneNumber(CCDTelephone.builder().telephoneNumber("07987654321").build())
                .dateOfBirth(LocalDate.of(1950, 01, 01))
                .correspondenceAddress(ccdAddress)
                .build())
            .partyName("Individual")
            .representativeOrganisationAddress(ccdAddress)
            .representativeOrganisationName("My Org")
            .representativeOrganisationPhone("07987654321")
            .representativeOrganisationEmail("my@email.com")
            .representativeOrganisationDxAddress("dx123")
            .build();
    }

    public static CCDApplicant getCCDApplicantIndividualWithDefault() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDApplicant.builder()
            .partyDetail(CCDParty.builder()
                .type(INDIVIDUAL)
                .primaryAddress(ccdAddress)
                .emailAddress("claimant@email.test")
                .telephoneNumber(CCDTelephone.builder().telephoneNumber("07987654321").build())
                .dateOfBirth(LocalDate.of(1950, 01, 01))
                .correspondenceAddress(ccdAddress)
                .build())
            .partyName("Individual")
            .build();
    }

    public static CCDRespondent getIndividualRespondentWithDQ() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDRespondent.builder()
            .partyName("Mary Richards")
            .claimantProvidedDetail(
                CCDParty.builder()
                    .type(INDIVIDUAL)
                    .primaryAddress(ccdAddress)
                    .dateOfBirth(LocalDate.of(1950, 01, 01))
                    .correspondenceAddress(ccdAddress)
                    .build())
            .claimantProvidedPartyName("Individual")
            .claimantProvidedRepresentativeOrganisationAddress(ccdAddress)
            .claimantProvidedRepresentativeOrganisationName("My Org")
            .claimantProvidedRepresentativeOrganisationPhone("07987654321")
            .claimantProvidedRepresentativeOrganisationPhone("my@email.com")
            .claimantProvidedRepresentativeOrganisationDxAddress("dx123")
            .claimantResponse(CCDResponseRejection.builder()
                .directionsQuestionnaire(CCDDirectionsQuestionnaire.builder().build())
                .build())
            .directionsQuestionnaire(CCDDirectionsQuestionnaire
                .builder()
                .hearingLocation("Defendant Court")
                .exceptionalCircumstancesReason("As a defendant I like this court more")
                .build())
            .build();
    }

    public static CCDRespondent getIndividualRespondentWithDQInClaimantResponse() {
        CCDAddress ccdAddress = getCCDAddress();
        return CCDRespondent.builder()
            .partyName("Mary Richards")
            .claimantProvidedDetail(
                CCDParty.builder()
                    .type(INDIVIDUAL)
                    .primaryAddress(ccdAddress)
                    .dateOfBirth(LocalDate.of(1950, 01, 01))
                    .correspondenceAddress(ccdAddress)
                    .build())
            .claimantProvidedPartyName("Individual")
            .claimantProvidedRepresentativeOrganisationAddress(ccdAddress)
            .claimantProvidedRepresentativeOrganisationName("My Org")
            .claimantProvidedRepresentativeOrganisationPhone("07987654321")
            .claimantProvidedRepresentativeOrganisationPhone("my@email.com")
            .claimantProvidedRepresentativeOrganisationDxAddress("dx123")
            .claimantResponse(CCDResponseRejection.builder()
                .directionsQuestionnaire(CCDDirectionsQuestionnaire
                    .builder()
                    .hearingLocation("Claimant Court")
                    .exceptionalCircumstancesReason("As a claimant I like this court more").build())
                .build())
            .directionsQuestionnaire(CCDDirectionsQuestionnaire
                .builder()
                .build())
            .build();
    }

    public static CCDApplicant getCCDApplicantCompany() {
        CCDAddress ccdAddress = getCCDAddress();

        return CCDApplicant.builder()
            .partyDetail(CCDParty.builder()
                .type(COMPANY)
                .primaryAddress(ccdAddress)
                .correspondenceAddress(ccdAddress)
                .telephoneNumber(CCDTelephone.builder().telephoneNumber("07987654321").build())
                .contactPerson("MR. Hyde")
                .build()
            )
            .partyName("Abc Ltd")
            .representativeOrganisationAddress(ccdAddress)
            .representativeOrganisationName("My Org")
            .representativeOrganisationPhone("07987654321")
            .representativeOrganisationEmail("my@email.com")
            .representativeOrganisationDxAddress("dx123")
            .build();
    }

    public static CCDApplicant getCCDApplicantOrganisation() {
        CCDAddress ccdAddress = getCCDAddress();

        return CCDApplicant.builder()
            .partyDetail(CCDParty.builder()
                .type(ORGANISATION)
                .primaryAddress(ccdAddress)
                .correspondenceAddress(ccdAddress)
                .telephoneNumber(CCDTelephone.builder().telephoneNumber("07987654321").build())
                .contactPerson("MR. Hyde")
                .companiesHouseNumber("12345678")
                .build()
            )
            .partyName("Xyz & Co")
            .representativeOrganisationAddress(ccdAddress)
            .representativeOrganisationName("My Org")
            .representativeOrganisationPhone("07987654321")
            .representativeOrganisationEmail("my@email.com")
            .representativeOrganisationDxAddress("dx123")
            .build();
    }

    public static CCDApplicant getCCDApplicantSoleTrader() {
        CCDAddress ccdAddress = getCCDAddress();

        return CCDApplicant.builder()
            .partyDetail(CCDParty.builder()
                .type(SOLE_TRADER)
                .title("Mr.")
                .telephoneNumber(CCDTelephone.builder().telephoneNumber("07987654321").build())
                .primaryAddress(ccdAddress)
                .correspondenceAddress(ccdAddress)
                .businessName("My Trade")
                .build()
            )
            .partyName("Individual")
            .representativeOrganisationAddress(ccdAddress)
            .representativeOrganisationName("My Org")
            .representativeOrganisationPhone("07987654321")
            .representativeOrganisationEmail("my@email.com")
            .representativeOrganisationDxAddress("dx123")
            .build();
    }

    public static List<CCDCollectionElement<CCDApplicant>> getApplicants() {
        return singletonList(CCDCollectionElement.<CCDApplicant>builder()
            .id(UUID.randomUUID().toString())
            .value(getCCDApplicantIndividual()).build());
    }

    public static List<CCDCollectionElement<CCDRespondent>> getRespondents() {
        return singletonList(CCDCollectionElement.<CCDRespondent>builder()
            .id(UUID.randomUUID().toString())
            .value(getCCDRespondentIndividual()).build());
    }

    public static CCDCase getCCDLegalCase() {
        return CCDCase.builder()
            .id(1L)
            .submittedOn(LocalDateTime.of(2017, 11, 01, 10, 15, 30))
            .issuedOn(LocalDate.of(2017, 11, 15))
            .submitterEmail("my@email.com")
            .submitterId("123")
            .previousServiceCaseReference("ref no")
            .externalId(UUID.randomUUID().toString())
            .features("admissions")
            .amountType(RANGE)
            .amountLowerValue("5000")
            .amountHigherValue("50000")
            .housingDisrepairCostOfRepairDamages(MORE_THAN_THOUSAND_POUNDS.name())
            .housingDisrepairOtherDamages(THOUSAND_POUNDS_OR_LESS.name())
            .personalInjuryGeneralDamages(MORE_THAN_THOUSAND_POUNDS.name())
            .sotSignerName("name")
            .sotSignerRole("role")
            .externalReferenceNumber("external ref")
            .externalId(UUID.randomUUID().toString())
            .feeAccountNumber("PBA1234567")
            .feeAmountInPennies("75000")
            .feeCode("X1202")
            .reason("Reason for the case")
            .preferredCourt("London Court")
            .applicants(getApplicants())
            .respondents(getRespondents())
            .claimSubmissionOperationIndicators(defaultCCDClaimSubmissionOperationIndicatorsAllYes)
            .build();
    }

    public static CCDCase getCCDCitizenCase(List<CCDCollectionElement<CCDAmountRow>> amountBreakDown) {

        return ccdBuilderWithDefault()
            .amountBreakDown(amountBreakDown)
            .applicants(getApplicants())
            .respondents(getRespondents())
            .build();
    }

    public static CCDCase getCCDCitizenCaseWithDefault() {

        return ccdBuilderWithDefault()
            .amountType(RANGE)
            .amountLowerValue("5000")
            .amountHigherValue("50000")
            .applicants(getApplicants())
            .respondents(getRespondents())
            .build();
    }

    public static CCDOrderGenerationData getCCDOrderGenerationData() {
        return CCDOrderGenerationData.builder()
            .directionList(ImmutableList.of(
                DOCUMENTS, EYEWITNESS))
            .otherDirections(ImmutableList.of(
                CCDCollectionElement.<CCDOrderDirection>builder()
                    .value(
                        CCDOrderDirection.builder()
                            .extraOrderDirection(OTHER)
                            .directionComment("a direction")
                            .otherDirectionHeaders(UPLOAD)
                            .forParty(CCDDirectionPartyType.BOTH)
                            .sendBy(LocalDate.parse("2020-10-11"))
                            .build()
                    )
                    .build(),
                CCDCollectionElement.<CCDOrderDirection>builder()
                    .value(
                        CCDOrderDirection.builder()
                            .sendBy(LocalDate.parse("2020-10-11"))
                            .extraOrderDirection(EXPERT_REPORT_PERMISSION)
                            .forParty(CCDDirectionPartyType.BOTH)
                            .expertReports(
                                ImmutableList.of(
                                    CCDCollectionElement.<String>builder()
                                        .value("first")
                                        .build(),
                                    CCDCollectionElement.<String>builder()
                                        .value("second")
                                        .build(),
                                    CCDCollectionElement.<String>builder()
                                        .value("third")
                                        .build()))
                            .extraDocUploadList(
                                ImmutableList.of(
                                    CCDCollectionElement.<String>builder()
                                        .value("first document")
                                        .build(),
                                    CCDCollectionElement.<String>builder()
                                        .value("second document")
                                        .build()))
                            .build()
                    )
                    .build()))
            .paperDetermination(NO)
            .docUploadDeadline(LocalDate.parse("2020-10-11"))
            .eyewitnessUploadDeadline(LocalDate.parse("2020-10-11"))
            .hearingCourt(CCDHearingCourtType.BIRMINGHAM)
            .preferredCourtObjectingReason("I like this court more")
            .newRequestedCourt("Another court")
            .docUploadForParty(CCDDirectionPartyType.CLAIMANT)
            .extraDocUploadList(
                ImmutableList.of(
                    CCDCollectionElement.<String>builder()
                        .value("first document")
                        .build(),
                    CCDCollectionElement.<String>builder()
                        .value("second document")
                        .build()))
            .eyewitnessUploadForParty(CCDDirectionPartyType.DEFENDANT)
            .estimatedHearingDuration(CCDHearingDurationType.FOUR_HOURS)
            .build();
    }

    public static CCDStatementOfMeans getCCDStatementOfMeans() {
        return CCDStatementOfMeans.builder()
            .residenceType(JOINT_OWN_HOME)
            .residenceOtherDetail("other details")
            .noOfMaintainedChildren(1)
            .anyDisabledChildren(NO)
            .numberOfOtherDependants(1)
            .otherDependantDetails("other details")
            .otherDependantAnyDisabled(NO)
            .taxPaymentsReason("reason")
            .taxYouOwe("1000")
            .selfEmploymentJobTitle("Job title")
            .selfEmploymentAnnualTurnover("1000")
            .unEmployedNoOfMonths(2)
            .employmentDetails("Details")
            .unEmployedNoOfYears(0)
            .dependantChildren(singletonList(
                CCDCollectionElement.<CCDChildCategory>builder().value(CCDChildCategory.builder()
                    .ageGroupType(UNDER_11)
                    .numberOfChildren(2)
                    .numberOfResidentChildren(2)
                    .build()
                ).build()
            ))
            .incomes(singletonList(
                CCDCollectionElement.<CCDIncome>builder().value(CCDIncome.builder()
                    .type(JOB)
                    .frequency(MONTH)
                    .amountReceived("1000")
                    .build()
                ).build()
            ))
            .expenses(singletonList(
                CCDCollectionElement.<CCDExpense>builder().value(CCDExpense.builder()
                    .type(COUNCIL_TAX)
                    .frequency(MONTH)
                    .amountPaid("1000")
                    .build()
                ).build()
            ))
            .debts(singletonList(
                CCDCollectionElement.<CCDDebt>builder().value(CCDDebt.builder()
                    .totalOwed("1000")
                    .description("Reference")
                    .monthlyPayments("100")
                    .build()
                ).build()
            ))
            .bankAccounts(singletonList(
                CCDCollectionElement.<CCDBankAccount>builder().value(CCDBankAccount.builder()
                    .balance("10000")
                    .joint(NO)
                    .type(SAVINGS_ACCOUNT)
                    .build()
                ).build()
            ))
            .courtOrders(singletonList(
                CCDCollectionElement.<CCDCourtOrder>builder().value(CCDCourtOrder.builder()
                    .amountOwed("1000")
                    .claimNumber("Reference")
                    .monthlyInstalmentAmount("100")
                    .build()
                ).build()
            ))
            .priorityDebts(singletonList(
                CCDCollectionElement.<CCDPriorityDebt>builder().value(CCDPriorityDebt.builder()
                    .frequency(MONTH)
                    .amount(AMOUNT)
                    .type(ELECTRICITY)
                    .build()
                ).build()
            ))
            .carer(YES)
            .livingPartner(CCDLivingPartner.builder()
                .disability(SEVERE)
                .over18(YES)
                .pensioner(YES)
                .build()
            )
            .disabilityStatus(CCDDisabilityStatus.YES)
            .retired(NO)
            .build();
    }

    public static CCDCase getCCDCitizenCaseWithOperationIndicators(
        CCDClaimSubmissionOperationIndicators claimIndicatorSupplier) {
        List<CCDCollectionElement<CCDApplicant>> applicants
            = singletonList(CCDCollectionElement.<CCDApplicant>builder().value(getCCDApplicantIndividual()).build());
        List<CCDCollectionElement<CCDRespondent>> respondents
            = singletonList(CCDCollectionElement.<CCDRespondent>builder().value(getCCDRespondentIndividual()).build());

        return ccdBuilderWithDefault()
            .amountBreakDown(getAmountBreakDown())
            .applicants(applicants)
            .respondents(respondents)
            .claimSubmissionOperationIndicators(claimIndicatorSupplier)
            .build();
    }

    public static CCDAddress getAddress() {
        return CCDAddress.builder()
            .addressLine1("52")
            .addressLine2("Down Street")
            .addressLine3("Salford")
            .postTown("Manchester")
            .postCode("DF1 3LJ")
            .build();
    }
}