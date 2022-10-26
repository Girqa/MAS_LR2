package NewRealization.Behaviours.SubBehaviours;

import AdditionalClasses.JadePatternProvider;
import AdditionalClasses.ValuesContainer;
import Exceptions.InitiatorCantFindMaximum;
import Mail.Classes.CountingReceiver;
import Mail.Classes.CountingSender;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendRequestBehaviour extends Behaviour {
    private List<AID> agents;
    private ValuesContainer container;
    private Map<String, Double> values;
    private MessageTemplate tmpl;
    private CountingSender sender;
    private CountingReceiver receiver;
    private boolean finished = false;
    public SendRequestBehaviour(Map<String, Double> values) {
        this.values = values;
        // Обработчик принимаемых сообщений
        receiver = new CountingReceiver(new String[]{"f(x-delta)", "f(x)", "f(x+delta)"}, "/");
        // Обработчик отправляемых сообщений
        sender = new CountingSender("/");
    }

    @Override
    public void onStart() {
        // Отправим запрос на расчет
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        // Создали наполнение сообщения
        String content = sender.prepareMsg(new Double[]{values.get("x"), values.get("delta")});
        request.setContent(content);
        // Получатели запросов
        agents = JadePatternProvider.getServiceProviders(getAgent(), "Counter");
        // Контейнер под ответы агентов
        container = new ValuesContainer(agents.size());
        // Добавили получателей в запрос
        agents.stream().forEach(request::addReceiver);
        getAgent().send(request);

        // Так же, задаем шаблон для принимаемых сообщений
        tmpl = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
    }

    @Override
    public void action() {
        ACLMessage response = getAgent().receive(tmpl);
        if (response != null && !container.isFull()) {
            System.out.println(getAgent().getLocalName() + " " + response.getContent() + " from " + response.getSender().getLocalName());
            HashMap<String, Double> msgVals = receiver.parse(response.getContent());
            container.addColumn(new double[]{
                    msgVals.get("f(x-delta)"),
                    msgVals.get("f(x)"),
                    msgVals.get("f(x+delta)"),
            });
        } else if(!finished && container.isFull()) {
            switch (container.getBestResultNumber()) {
                case 0:  // Уменьшение х
                    values.put("x", values.get("x") - values.get("delta"));
                    break;
                case 1:  // Уменьшение delta
                    values.put("delta", values.get("delta") / 2);
                    break;
                case 2:  // Увеличение х
                    values.put("x", values.get("x") + values.get("delta"));
                    break;
                default:
                    throw new InitiatorCantFindMaximum("Получено невозможное значение номера лучшего результата.");
            }
            finished = true;
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return finished;
    }
}
