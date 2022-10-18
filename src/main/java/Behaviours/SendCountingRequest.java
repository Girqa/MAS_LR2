package Behaviours;

import Agents.FunctionAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Отправка запросов на расчет значений функций других агентов, принятия ответов и оценка полученных результатов
 */
public class SendCountingRequest extends TickerBehaviour {
    private FunctionAgent myAgent;
    private AID topic;
    private MessageTemplate tmpl;

    public SendCountingRequest(FunctionAgent a, long period, AID topic) {
        super(a, period);
        myAgent = a;
        this.topic = topic;
    }

    /**
     * Шаблон принятия сообщений. Принимаем только INFORM
     */
    @Override
    public void onStart() {
        super.onStart();
        tmpl = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
    }

    @Override
    protected void onTick() {
        if (myAgent.isInitiator()) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            double x = myAgent.getCurX();
            double delta = myAgent.getDelta();

            msg.addReceiver(topic);
            msg.setContent(Double.toString(x-delta));
            myAgent.send(msg);

            ACLMessage response = myAgent.receive(tmpl);

            int responsesCount = 0;
            double sum = 0.0;

            while (responsesCount < 3) {
                response = myAgent.receive();
                if (response != null) {
                    System.out.println(response.getContent());
                    try {
                        sum += Double.parseDouble(response.getContent());
                        responsesCount++;
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            System.out.println(sum);
            System.out.println("SSDA");
            myAgent.dismayedInitiator();
        }
    }
}
