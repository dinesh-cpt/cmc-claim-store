package uk.gov.hmcts.cmc.claimstore.services.ccd.callbacks;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.cmc.ccd.domain.CaseEvent;
import uk.gov.hmcts.cmc.ccd.mapper.CaseMapper;
import uk.gov.hmcts.cmc.claimstore.idam.models.UserDetails;
import uk.gov.hmcts.cmc.claimstore.services.DirectionsQuestionnaireDeadlineCalculator;
import uk.gov.hmcts.cmc.claimstore.services.notifications.DefendantResponseNotificationService;
import uk.gov.hmcts.cmc.claimstore.utils.CaseDetailsConverter;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleClaim;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HWF Remiitance Fee calculator Handler ")
class HWFFeeRemittedCallbackHandlerTest {

    private static final String AUTHORISATION = "Bearer: aaaa";
    private static final String DOC_URL = "http://success.test";
    private static final String DOC_URL_BINARY = "http://success.test/binary";
    private HWFFeeRemittedCallbackHandler handler;
    private CallbackParams callbackParams;
    private CallbackRequest callbackRequest;
    @Mock
    private DirectionsQuestionnaireDeadlineCalculator deadlineCalculator;

    @Mock
    private CaseMapper caseMapper;

    @Mock
    private CaseDetailsConverter caseDetailsConverter;

    @Mock
    private DefendantResponseNotificationService defendantResponseNotificationService;

    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        handler = new HWFFeeRemittedCallbackHandler(caseDetailsConverter, deadlineCalculator, caseMapper);
        callbackRequest = CallbackRequest
            .builder()
            .caseDetails(CaseDetails.builder().data(Collections.emptyMap()).build())
            .eventId(CaseEvent.HWF_FULL_REMISSION_GRANTED.getValue())
            .build();
        callbackParams = CallbackParams.builder()
            .type(CallbackType.ABOUT_TO_SUBMIT)
            .request(callbackRequest)
            .params(ImmutableMap.of(CallbackParams.Params.BEARER_TOKEN, AUTHORISATION))
            .build();
    }

    @Test
    public void shouldUpdateFeeRemitted() {
        Claim claim = SampleClaim.getClaimWithFullAdmission();
        when(caseDetailsConverter.extractClaim(any(CaseDetails.class))).thenReturn(claim);
        Map<String, Object> mappedCaseData = new HashMap<>();
        when(caseDetailsConverter.convertToMap(caseMapper.to(claim)))
            .thenReturn(mappedCaseData);
        AboutToStartOrSubmitCallbackResponse response = (AboutToStartOrSubmitCallbackResponse)
            handler.handle(callbackParams);
        assertEquals("4000", response.getData().get("feeRemitted"));
    }
}