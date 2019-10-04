package uk.gov.hmcts.cmc.claimstore.services.ccd.callbacks.ioc;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.cmc.ccd.mapper.CaseMapper;
import uk.gov.hmcts.cmc.claimstore.services.IssueDateCalculator;
import uk.gov.hmcts.cmc.claimstore.services.ResponseDeadlineCalculator;
import uk.gov.hmcts.cmc.claimstore.services.ccd.callbacks.CallbackParams;
import uk.gov.hmcts.cmc.claimstore.services.ccd.callbacks.CallbackType;
import uk.gov.hmcts.cmc.claimstore.utils.CaseDetailsConverter;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.domain.models.Payment;
import uk.gov.hmcts.cmc.domain.models.PaymentStatus;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleClaim;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.payments.client.models.LinkDto;
import uk.gov.hmcts.reform.payments.client.models.LinksDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.cmc.ccd.domain.CaseEvent.INITIATE_CLAIM_PAYMENT_CITIZEN;
import static uk.gov.hmcts.cmc.domain.models.ChannelType.CITIZEN;

@RunWith(MockitoJUnitRunner.class)
public class InitiatePaymentCallbackHandlerTest {
    private static final String BEARER_TOKEN = "Bearer let me in";
    private static final String NEXT_URL = "http://nexturl.test";
    private static final Long CASE_ID = 3L;

    @Mock
    private CaseDetailsConverter caseDetailsConverter;
    @Mock
    private IssueDateCalculator issueDateCalculator;
    @Mock
    private ResponseDeadlineCalculator responseDeadlineCalculator;
    @Mock
    private CaseMapper caseMapper;
    @Mock
    private PaymentsService paymentsService;
    @Captor
    private ArgumentCaptor<Claim> claimArgumentCaptor;

    private CallbackRequest callbackRequest;

    private InitiatePaymentCallbackHandler handler;

    @Before
    public void setUp() {
        handler = new InitiatePaymentCallbackHandler(
            paymentsService,
            caseDetailsConverter,
            caseMapper,
            issueDateCalculator,
            responseDeadlineCalculator);
        callbackRequest = CallbackRequest
            .builder()
            .eventId(INITIATE_CLAIM_PAYMENT_CITIZEN.getValue())
            .caseDetails(CaseDetails.builder()
                .id(CASE_ID)
                .build())
            .build();
    }

    @Test
    public void shouldCreatePaymentOnAboutToSubmitEvent() throws URISyntaxException {
        Claim claim = SampleClaim.getDefault();
        when(caseDetailsConverter.extractClaim(any(CaseDetails.class)))
            .thenReturn(claim);

        PaymentDto expectedPayment = PaymentDto.builder()
            .amount(BigDecimal.TEN)
            .reference("reference")
            .status("success")
            .dateCreated(OffsetDateTime.parse("2017-02-03T10:15:30+01:00"))
            .links(LinksDto.builder()
                .nextUrl(
                    LinkDto.builder()
                        .href(new URI(NEXT_URL))
                        .build()
                ).build())
            .build();

        when(paymentsService.createPayment(
            eq(BEARER_TOKEN),
            any(Claim.class)))
            .thenReturn(expectedPayment);

        LocalDate date = LocalDate.now();
        when(issueDateCalculator.calculateIssueDay(any(LocalDateTime.class))).thenReturn(date);
        when(responseDeadlineCalculator.calculateResponseDeadline(any(LocalDate.class))).thenReturn(date);

        CallbackParams callbackParams = CallbackParams.builder()
            .type(CallbackType.ABOUT_TO_SUBMIT)
            .request(callbackRequest)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, BEARER_TOKEN))
            .build();

        handler.handle(callbackParams);

        verify(caseMapper).to(claimArgumentCaptor.capture());

        Claim toBeSaved = claimArgumentCaptor.getValue();
        assertThat(toBeSaved.getId()).isEqualTo(CASE_ID);
        assertThat(toBeSaved.getCcdCaseId()).isEqualTo(CASE_ID);
        assertThat(toBeSaved.getIssuedOn()).isEqualTo(date);
        assertThat(toBeSaved.getResponseDeadline()).isEqualTo(date);
        assertThat(toBeSaved.getChannel()).isEqualTo(Optional.of(CITIZEN));

        Payment payment = toBeSaved.getClaimData().getPayment();
        assertThat(payment.getAmount()).isEqualTo(expectedPayment.getAmount());
        assertThat(payment.getDateCreated()).isEqualTo(expectedPayment.getDateCreated().toLocalDate().toString());
        assertThat(payment.getId()).isEqualTo(expectedPayment.getId());
        assertThat(payment.getReference()).isEqualTo(expectedPayment.getReference());
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.fromValue(expectedPayment.getStatus()));
        assertThat(payment.getNextUrl()).isEqualTo(expectedPayment.getLinks().getNextUrl().getHref().toString());

    }
}