package uk.gov.hmcts.cmc.claimstore.services.ccd.callbacks;

import uk.gov.hmcts.cmc.claimstore.exceptions.CallbackException;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;

import java.util.Map;
import java.util.Optional;

public abstract class CallbackHandler {

    protected abstract Map<CallbackType, Callback> callbacks();

    public CallbackResponse handle(CallbackParams callbackParams) {
        return Optional.ofNullable(callbacks().get(callbackParams.getType()))
            .map(callback -> callback.execute(callbackParams))
            .orElseThrow(() -> new CallbackException(
                String.format("Callback for event %s, type %s not implemented",
                    callbackParams.getRequest().getEventId(),
                    callbackParams.getType())));
    }
}