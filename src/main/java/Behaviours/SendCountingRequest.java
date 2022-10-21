package Behaviours;

import AdditionalClasses.ValuesContainer;
import Agents.FunctionAgent;
import FunctionInterfaces.OptimizationFunction;
import Mail.Classes.CountingReceiver;
import Mail.Classes.CountingSender;
import Mail.Interfaces.Receiver;
import Mail.Interfaces.Sender;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.awt.geom.CubicCurve2D;
import java.util.HashMap;

/**
 * Отправка запросов на расчет значений функций других агентов, принятия ответов и оценка полученных результатов.
 * Fields:
 * - myAgent - агент с данным поведением
 * - topic - топик
 * - tmpl - шаблон сообщения типа INFORM
 * - requestSender - составитель сообщения на  отправку запроса
 * - infoReceiver - парсер принимаемых ответов от агентов
 * - func - функция оптимизации (пока так, тк видимо пока не закончено одно поведение, второе ждет)
 */
public class SendCountingRequest extends TickerBehaviour {
    private FunctionAgent myAgent;
    private AID topic;
    private MessageTemplate tmpl;
    private Sender requestSender;
    private Receiver infoReceiver;
    private OptimizationFunction func;

    public SendCountingRequest(FunctionAgent a, long period, AID topic, OptimizationFunction func) {
        super(a, period);
        myAgent = a;
        this.topic = topic;
        this.func = func;
    }

    /**
     * Шаблон принятия сообщений. Принимаем только INFORM
     */
    @Override
    public void onStart() {
        super.onStart();
        tmpl = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        requestSender = new CountingSender("/");
        infoReceiver = new CountingReceiver(new String[]{"f(curX-delta)", "f(curX)", "f(curX+delta)"}, "/");
    }

    @Override
    protected void onTick() {
        // Если агент является инициатором расчета
        if (myAgent.isInitiator()) {
            // Посылаем запрос на расчет в топик
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(topic);

            double curX = myAgent.getCurX();
            double delta = myAgent.getDelta();
            msg.setContent(
                    requestSender.prepareMsg(new Double[]{curX, delta})
            );
            myAgent.send(msg);

            // Получаем ответы от агентов
            int responsesCount = 0;  // изначально ответов не было
            ValuesContainer container = new ValuesContainer(3);  // контейнер под ответы

            while (responsesCount < 2) {
                ACLMessage response = getAgent().receive(tmpl);
                if (response != null) {
                    try {
                        System.out.println("---test---");
                        System.out.println(response.getContent());
                        HashMap<String, Double> msgVals = infoReceiver.parse(response.getContent());
                        container.addColumn(new double[]{
                                msgVals.get("f(curX-delta)"),
                                msgVals.get("f(curX)"),
                                msgVals.get("f(curX+delta)"),
                        });
                        responsesCount++;
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        System.out.println(e.getStackTrace());
                    }
                }
            }
            // Расчеты инициатора
            container.addColumn(new double[]{
                    func.execute(curX-delta),
                    func.execute(curX),
                    func.execute(curX+delta)});

            myAgent.dismayedInitiator();
        }
    }
}
