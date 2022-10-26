package NewRealization.Behaviours.SubBehaviours;

import Mail.Classes.CountingReceiver;
import NewRealization.Behaviours.InitiatorBehaviour;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Map;

/**
 * Поведение принятия запроса на инициацию расчетов
 */
public class GetTransferRequestBehaviour extends Behaviour {
    private MessageTemplate tmpl;
    private CountingReceiver receiver;

    /**
     * Определяем шаблон принимаемых сообщений и параметры ресивера
     */
    @Override
    public void onStart() {
        tmpl = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        receiver = new CountingReceiver(new String[]{"x", "delta", "eps"}, "/");
    }

    /**
     * Если получен запрос на передачу прав инициатора - становится инициатором.
     * Отправляет подтверждение операции.
     */
    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(tmpl);
        if (msg != null) {
            // Становление инициатором
            Map<String, Double> params = receiver.parse(msg.getContent());
            getAgent().addBehaviour(new InitiatorBehaviour(params));
            System.out.println(getAgent().getLocalName() + " is initiator with params: " + params);
            // Отправка подтверждения
            ACLMessage reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            AID prevInitiator = msg.getSender();
            reply.addReceiver(prevInitiator);
            reply.setContent("accepted");
            getAgent().send(reply);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
