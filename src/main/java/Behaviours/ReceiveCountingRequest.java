package Behaviours;

import FunctionInterfaces.OptimizationFunction;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Принятие запроса от другого агента на расчет значения функции, заданной агенту
 */
public class ReceiveCountingRequest extends Behaviour {
    private OptimizationFunction func;
    private AID topic;
    private MessageTemplate tmpl;

    /**
     * Параметризированный конструктор поведения
     * @param func - рассчетная функция
     */
    public ReceiveCountingRequest(AID topic, OptimizationFunction func) {
        this.topic = topic;
        this.func = func;
    }

    /**
     * Шаблон принятия сообщений. Принимаем только запросы
     */
    @Override
    public void onStart() {
        super.onStart();
        tmpl = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
    }

    /**
     * Если приняли сообщение соответсвующее шаблону -> пытаемся его спарсить, провести расчет и отправить обратно
     */
    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(tmpl);
        if (msg != null) {
            try {
                System.out.println(getAgent().getLocalName() + " received " + msg.getContent() + " from " + msg.getSender().getLocalName());
                double x = Double.parseDouble(msg.getContent());
                ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                AID aid = new AID(msg.getSender().getLocalName(), false);
                response.addReceiver(aid);
                // response.setContent(""+func.execute(x));
                response.setContent(Double.toString(x+2));
                getAgent().send(response);
            } catch (NumberFormatException ex) {
                System.out.println("Unable to convert request value to double");
            }
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
