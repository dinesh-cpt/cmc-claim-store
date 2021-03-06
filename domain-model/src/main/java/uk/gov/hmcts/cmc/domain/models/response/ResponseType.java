package uk.gov.hmcts.cmc.domain.models.response;

//NOTE: Enum names need to be identical to enum names in CCDResponseType
public enum ResponseType {
    FULL_DEFENCE("I reject all of the claim"),
    FULL_ADMISSION("I admit all of the claim"),
    PART_ADMISSION("I admit part of the claim");

    private final String description;

    ResponseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
