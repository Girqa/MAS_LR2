package NewRealization.Behaviours;

import NewRealization.Behaviours.SubBehaviours.SendRequestBehaviour;
import NewRealization.Behaviours.SubBehaviours.TransferRequestBehaviour;
import jade.core.behaviours.FSMBehaviour;

import java.util.Map;

public class InitiatorBehaviour extends FSMBehaviour {
    public final String SEND="SEND", TRANSFER="TRANSFER";

    public InitiatorBehaviour(Map<String, Double> values) {
        double eps = 0.01;
        registerFirstState(new SendRequestBehaviour(values), SEND);
        registerLastState(new TransferRequestBehaviour(values, this, eps), TRANSFER);
        registerDefaultTransition(SEND, TRANSFER);
    }
}
