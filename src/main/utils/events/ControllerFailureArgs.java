package utils.events;

import utils.SenderType;

public class ControllerFailureArgs extends BasicSocketEventArg {

    public ControllerFailureArgs() {
        super(SenderType.Mediator, EventType.ChangeController);
    }
}
