package uk.gov.hmcts.cmc.claimstore.services.ccd.legaladvisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.cmc.ccd.domain.CCDCase;
import uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOrderDirectionType;
import uk.gov.hmcts.cmc.ccd.domain.legaladvisor.CCDOrderGenerationData;
import uk.gov.hmcts.cmc.claimstore.idam.models.UserDetails;

import java.time.Clock;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class DocAssemblyTemplateBodyMapper {

    private Clock clock;

    @Autowired
    public DocAssemblyTemplateBodyMapper(Clock clock) {
        this.clock = clock;
    }

    public DocAssemblyTemplateBody from(CCDCase ccdCase,
                                        CCDOrderGenerationData ccdOrderGenerationData,
                                        UserDetails userDetails) {
        return DocAssemblyTemplateBody.builder()
            .claimant(Party.builder()
                .partyName(ccdCase.getApplicants()
                    .get(0)
                    .getValue()
                    .getPartyName())
                .build())
            .defendant(Party.builder()
                .partyName(ccdCase.getRespondents()
                    .get(0)
                    .getValue()
                    .getPartyName())
                .build())
            .judicial(Judicial.builder()
                .firstName(userDetails.getForename())
                .lastName(userDetails.getSurname().orElse(""))
                .build())
            .currentDate(LocalDate.now(clock))
            .referenceNumber(ccdCase.getReferenceNumber())
            .hasFirstOrderDirections(
                ccdOrderGenerationData.getDirectionList().contains(CCDOrderDirectionType.DOCUMENTS))
            .docUploadDeadline(
                ccdOrderGenerationData.getDocUploadDeadline())
            .hasSecondOrderDirections(
                ccdOrderGenerationData.getDirectionList().contains(CCDOrderDirectionType.EYEWITNESS))
            .eyewitnessUploadDeadline(
                ccdOrderGenerationData.getEyewitnessUploadDeadline())
            .docUploadForParty(
                ccdOrderGenerationData.getDocUploadForParty())
            .eyewitnessUploadForParty(
                ccdOrderGenerationData.getEyewitnessUploadForParty())
            .hearingRequired(
                ccdOrderGenerationData.getHearingIsRequired().toBoolean())
            .preferredCourtName(
                "Some court")    // will be populated when the acceptance criterias are refined
            .preferredCourtAddress(
                "this is an address EC2Y 3ND")
            .estimatedHearingDuration(
                ccdOrderGenerationData.getEstimatedHearingDuration())
            .hearingStatement(
                ccdOrderGenerationData.getHearingStatement())
            .otherDirectionList(
                ccdOrderGenerationData.getOtherDirectionList().stream().map(
                    direction -> OtherDirection.builder()
                            .extraOrderDirection(direction.getExtraOrderDirection())
                            .directionComment(direction.getOtherDirection())
                            .forParty(direction.getForParty())
                            .sendBy(direction.getSendBy())
                            .build()
                ).collect(Collectors.toList()))
            .build();
    }
}