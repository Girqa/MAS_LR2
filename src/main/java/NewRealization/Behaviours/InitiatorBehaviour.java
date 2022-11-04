package NewRealization.Behaviours;

import NewRealization.Behaviours.SubBehaviours.CheckTransferSuccessBehaviour;
import NewRealization.Behaviours.SubBehaviours.SendRequestBehaviour;
import NewRealization.Behaviours.SubBehaviours.TransferRequestBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

import java.util.Map;

public class InitiatorBehaviour extends FSMBehaviour {
    public final String SEND="SEND", TRANSFER="TRANSFER", CHECK="CHECK", END="END";

    public InitiatorBehaviour(Map<String, Double> values) {
        registerFirstState(new SendRequestBehaviour(values), SEND);
        registerState(new TransferRequestBehaviour(values), TRANSFER);
        registerState(new CheckTransferSuccessBehaviour(getAgent(), 10, values), CHECK);
        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {return;}
        }, END);

        registerDefaultTransition(SEND, TRANSFER);
        registerDefaultTransition(TRANSFER, CHECK);
        registerTransition(CHECK, TRANSFER, 0);
        registerTransition(CHECK, END, 1);
    }
}
