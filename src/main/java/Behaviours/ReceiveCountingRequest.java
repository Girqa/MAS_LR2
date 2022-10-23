package Behaviours;

import Agents.FunctionAgent;
import FunctionInterfaces.OptimizationFunction;
import Mail.Classes.CountingSender;
import Mail.Classes.CountingReceiver;
import Mail.Interfaces.Receiver;
import Mail.Interfaces.Sender;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

/**
 * Принятие запроса от другого агента на расчет значения функции, заданной агенту.
 * Fields:
 * - myAgent - агент
 * - func - оптимизируемая функция
 * - reqTmpl - шаблон принятия сообщений типа REQUEST
 * - msgReceiver - обработчик сообщений
 */
public class ReceiveCountingRequest extends Behaviour {
    private FunctionAgent myAgent;
    private OptimizationFunction func;
    private MessageTemplate reqTmpl;
    private Receiver msgReceiver;
    private Sender msgSender;

    /**
     * Параметризированный конструктор поведения
     * @param func - рассчетная функция
     */
    public ReceiveCountingRequest(FunctionAgent myAgent, OptimizationFunction func) {
        this.myAgent = myAgent;
        this.func = func;
    }

    /**
     * Шаблон принятия сообщений. Принимаем только запросы.
     */
    @Override
    public void onStart() {
        super.onStart();
        reqTmpl = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        /*
        Формат принимаемого сообщения "curX/delta"
         */
        msgReceiver = new CountingReceiver(new String[]{"curX", "delta"}, "/");
        /*
        Формат отправляемого сообщения "f1/f2/f3"
         */
        msgSender = new CountingSender("/");
    }

    /**
     * Если приняли сообщение соответсвующее шаблону -> пытаемся его спарсить -> провести расчет -> отправить результат
     */
    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(reqTmpl);
        if (msg != null) {
            try {
                // Парсим сообщение
                HashMap<String, Double> paramsMap = msgReceiver.parse(msg.getContent());
                double curX = paramsMap.get("curX");  // <- внимательно с именами параметра
                double delta = paramsMap.get("delta");  // <- если меняем имя -> меняем конструктор msgReceiver
                myAgent.setCurX(curX);
                myAgent.setDelta(delta);
                // Создаем сообщение типа INFORM
                ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                AID aid = new AID(msg.getSender().getName(), true);
                response.addReceiver(aid);  // Отправлять будем инициатору
                // Расчет значений
                double y1 = func.execute(curX-delta);
                double y2 = func.execute(curX);
                double y3 = func.execute(curX+delta);
                // Положили результаты расчетов в сообщение
                response.setContent(msgSender.prepareMsg(new Double[]{y1, y2, y3}));
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
        return myAgent.isFinished();
    }
}
