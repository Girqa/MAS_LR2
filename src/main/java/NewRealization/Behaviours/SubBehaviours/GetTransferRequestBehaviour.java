package NewRealization.Behaviours.SubBehaviours;

import Mail.Classes.CountingReceiver;
import NewRealization.Behaviours.InitiatorBehaviour;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Map;

public class GetTransferRequestBehaviour extends Behaviour {
    private MessageTemplate tmpl;
    private CountingReceiver receiver;
    @Override
    public void onStart() {
        tmpl = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        receiver = new CountingReceiver(new String[]{"x", "delta"}, "/");
    }

    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(tmpl);
        if (msg != null) {
            Map<String, Double> params = receiver.parse(msg.getContent());
            getAgent().addBehaviour(new InitiatorBehaviour(params));
            System.out.println(getAgent().getLocalName() + " is initiator with params: " + params);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
