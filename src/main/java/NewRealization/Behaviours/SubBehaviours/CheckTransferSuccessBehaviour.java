package NewRealization.Behaviours.SubBehaviours;

import AdditionalClasses.ValuesContainer;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Map;

public class CheckTransferSuccessBehaviour extends WakerBehaviour {
    private int state;
    private Map<String, Double> values;
    public CheckTransferSuccessBehaviour(Agent a, long timeout, Map<String, Double> values) {
        super(a, timeout);
        this.values = values;
    }
    @Override
    protected void onWake() {
        ACLMessage msg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
        if (msg != null){
            state = 1;
        } else if (values.get("delta") < values.get("eps")){
            state = 1;
        } else {
            state = 0;
        }
    }
    @Override
    public int onEnd() {
        return state;
    }
}
