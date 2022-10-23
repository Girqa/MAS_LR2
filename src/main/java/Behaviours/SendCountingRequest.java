package Behaviours;

import AdditionalClasses.JadePatternProvider;
import AdditionalClasses.ValuesContainer;
import Agents.FunctionAgent;
import Exceptions.InitiatorCantFindMaximum;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Отправка запросов на расчет значений функций других агентов, принятия ответов и оценка полученных результатов.
 * Fields:
 * - myAgent - агент с данным поведением
 * - tmpl - шаблон сообщения типа INFORM
 * - requestSender - составитель сообщения на  отправку запроса
 * - infoReceiver - парсер принимаемых ответов от агентов
 * - func - функция оптимизации (пока так, тк видимо пока не закончено одно поведение, второе ждет)
 */
public class SendCountingRequest extends TickerBehaviour {
    private FunctionAgent myAgent;
    private MessageTemplate tmpl;
    private Sender requestSender;
    private Receiver infoReceiver;
    private OptimizationFunction func;

    public SendCountingRequest(FunctionAgent a, long period, OptimizationFunction func) {
        super(a, period);
        myAgent = a;
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
        if (myAgent.isInitiator() && !myAgent.isFinished()) {
            // Посылаем запрос на расчет другим агентам
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            List<AID> agents = JadePatternProvider.getServiceProviders(myAgent, "Counter");
            for (AID agent: agents) {
                if (!agent.getLocalName().equals(myAgent.getLocalName())) {
                    msg.addReceiver(agent);
                }
            }
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
                        System.out.println(getAgent().getLocalName() + " " + response.getContent() + " from " + response.getSender().getLocalName());
                        HashMap<String, Double> msgVals = infoReceiver.parse(response.getContent());
                        container.addColumn(new double[]{
                                msgVals.get("f(curX-delta)"),
                                msgVals.get("f(curX)"),
                                msgVals.get("f(curX+delta)"),
                        });
                        /**
                         * НУЖНО ДОБАВИТЬ ТАЙМЕР НА СЛУЧАЙ ТУПНЯКА и выбрасывать ошибку
                         */
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
            System.out.println("----------------");
            int bestResultNumber = container.getBestResultNumber();
            switch (bestResultNumber) {
                case 0:
                    myAgent.setCurX(curX - delta);
                    myAgent.dismayedInitiator();
                    break;
                case 1:
                    if (delta < myAgent.getEps()) {
                        myAgent.setFinished(true);  // Если считать уже некуда -> останавливаем расчет
                        System.err.println("Best X value is " + curX);
                        /**
                         * Разослать сообщение агентам о необходимости завершить расчеты
                         */
                    } else {
                        myAgent.setDelta(delta / 2);
                        myAgent.dismayedInitiator();
                    }
                    break;
                case 2:
                    myAgent.setCurX(curX + delta);
                    myAgent.dismayedInitiator();
                    break;
                default:
                    throw new InitiatorCantFindMaximum("Получено невозможное значение номера лучшего результата.");
            }
        } else {
            block();
        }
    }
}
