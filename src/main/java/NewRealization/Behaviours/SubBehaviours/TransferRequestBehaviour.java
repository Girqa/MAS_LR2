package NewRealization.Behaviours.SubBehaviours;

import AdditionalClasses.JadePatternProvider;
import Mail.Classes.CountingSender;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class TransferRequestBehaviour extends OneShotBehaviour {
    private Map<String, Double> values;
    private CountingSender sender;

    public TransferRequestBehaviour(Map<String, Double> values) {
        this.values = values;
        sender = new CountingSender("/");
    }

    @Override
    public void action() {
        // Если расчет окончен
        if (values.get("delta") < values.get("eps")) {
            System.err.println("Расчет окончен");
            System.err.println("Результат: x=" + values.get("x"));
        } else {
            // Выбираем одного агента, которому направим результат
            List<AID> agents = JadePatternProvider.getServiceProviders(myAgent, "Counter")
                    .stream()
                    .filter(a -> a.getLocalName().equals(getAgent().getLocalName()))
                    .collect(Collectors.toList());
            AID receiverAgent = agents.get(new Random().nextInt(agents.size()));
            // Отправляем сообщение
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);  // Повысить
            String content = sender.prepareMsg(new Double[]{
                    values.get("x"), values.get("delta"), values.get("eps")
            });
            System.out.println(getAgent().getLocalName() + " -> " + content + " to " + receiverAgent.getLocalName());
            msg.setContent(content);
            msg.addReceiver(receiverAgent);
            getAgent().send(msg);
            // Перестаем быть инициатором
            //getAgent().removeBehaviour(initiatorBehaviour);
            System.out.println("-------");
        }
    }
}
