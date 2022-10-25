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

public class TransferRequestBehaviour extends OneShotBehaviour {
    private Map<String, Double> values;
    private CountingSender sender;
    private Behaviour initiatorBehaviour;
    private double eps;
    public TransferRequestBehaviour(Map<String, Double> values, Behaviour initiatorBehaviour, double eps) {
        this.values = values;
        this.initiatorBehaviour = initiatorBehaviour;
        this.eps = eps;
        sender = new CountingSender("/");
    }

    @Override
    public void action() {
        // Если расчет окончен
        if (values.get("delta") < eps) {
            System.err.println("Расчет окончен");
            System.err.println("Результат: x=" + values.get("x"));
        } else {
            // Выбираем одного агента, которому направим результат
            List<AID> agents = JadePatternProvider.getServiceProviders(myAgent, "Counter");
            AID receiverAgent = agents.get(new Random().nextInt(agents.size()));
            // Отправляем сообщение
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);  // Повысить
            String content = sender.prepareMsg(new Double[]{
                    values.get("x"), values.get("delta")
            });
            System.out.println(getAgent().getLocalName() + " -> " + content + " to " + receiverAgent.getLocalName());
            msg.setContent(content);
            msg.addReceiver(receiverAgent);
            getAgent().send(msg);
            // Перестаем быть инициатором
            getAgent().removeBehaviour(initiatorBehaviour);
            System.out.println("-------");
        }
    }
}
