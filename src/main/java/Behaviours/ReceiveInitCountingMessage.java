package Behaviours;

import Agents.FunctionAgent;
import Mail.Classes.CountingReceiver;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

public class ReceiveInitCountingMessage extends Behaviour {
    private FunctionAgent myAgent;
    private MessageTemplate tmpl;
    public ReceiveInitCountingMessage(FunctionAgent myAgent) {
        this.myAgent = myAgent;
    }

    @Override
    public void onStart() {
        tmpl = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
    }

    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(tmpl);
        if (msg != null) {
            CountingReceiver receiver = new CountingReceiver(new String[]{"curX", "delta"}, "/");
            HashMap<String, Double> params = receiver.parse(msg.getContent());
            myAgent.setCurX(params.get("curX"));
            myAgent.setDelta(params.get("delta"));
            myAgent.makeInitiator();
            System.out.println(getAgent().getLocalName() + " is initiator");
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return myAgent.isFinished();
    }
}
