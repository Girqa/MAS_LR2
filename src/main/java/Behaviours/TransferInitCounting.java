package Behaviours;

import AdditionalClasses.JadePatternProvider;
import Agents.FunctionAgent;
import Mail.Classes.CountingSender;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TransferInitCounting extends Behaviour {
    private FunctionAgent myAgent;

    public TransferInitCounting(FunctionAgent myAgent) {
        this.myAgent = myAgent;
    }

    @Override
    public void action() {
        if (myAgent.isTransferInitiation()) {
            // Получили агентов считаводов и выбрали случайного из них
            List<AID> counters = JadePatternProvider.getServiceProviders(myAgent, "Counter");
            int randomAgent = new Random().nextInt(counters.size());
            // Сообщили случайному агенту, что его время пришло
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);  // Повысить
            CountingSender sender = new CountingSender("/");
            System.out.println(getAgent().getLocalName() + " -> " + myAgent.getCurX());
            String content = sender.prepareMsg(new Double[]{myAgent.getCurX(), myAgent.getDelta()});
            msg.setContent(content);
            AID receiver = new AID(counters.get(randomAgent).getLocalName(), false);
            msg.addReceiver(receiver);
            // Сняли флажок необходимости передачи прав инициации расчтеов
            myAgent.setTransferInitiation(false);
            getAgent().send(msg);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return myAgent.isFinished();
    }
}
