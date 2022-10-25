package NewRealization.Behaviours.SubBehaviours;

import FunctionInterfaces.OptimizationFunction;
import Mail.Classes.CountingReceiver;
import Mail.Classes.CountingSender;
import Mail.Interfaces.Receiver;
import Mail.Interfaces.Sender;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

/**
 * Принятие запроса от другого агента на расчет значения функции, заданной агенту.
 * Fields:
 * - func - оптимизируемая функция
 * - reqTmpl - шаблон принятия сообщений типа REQUEST
 * - msgReceiver - обработчик сообщений
 * - msgSender - отправщих сообщений
 */
public class ReciveRequestBehaviour extends Behaviour {
    private OptimizationFunction func;
    private MessageTemplate reqTmpl;
    private Receiver msgReceiver;
    private Sender msgSender;

    /**
     * Параметризированный конструктор поведения
     * @param func - рассчетная функция
     */
    public ReciveRequestBehaviour(OptimizationFunction func) {
        this.func = func;
    }

    /**
     * Шаблоны принятия и отправки сообщений. Принимаем только запросы.
     */
    @Override
    public void onStart() {
        super.onStart();
        reqTmpl = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        msgReceiver = new CountingReceiver(new String[]{"x", "delta"}, "/");
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
                double x = paramsMap.get("x");  // <- внимательно с именами параметра
                double delta = paramsMap.get("delta");  // <- если меняем имя -> меняем конструктор msgReceiver
                // Создаем сообщение типа INFORM
                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);// Отправлять будем инициатору
                reply.addReceiver(msg.getSender());
                // Расчет значений
                double y1 = func.execute(x-delta);
                double y2 = func.execute(x);
                double y3 = func.execute(x+delta);
                // Положили результаты расчетов в сообщение
                String content = msgSender.prepareMsg(new Double[]{y1, y2, y3});
                System.out.println(getAgent().getLocalName() + " sends " + content);
                reply.setContent(content);
                getAgent().send(reply);
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
