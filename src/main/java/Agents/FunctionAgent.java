package Agents;

import Behaviours.ReceiveCountingRequest;
import Behaviours.SendCountingRequest;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;

/**
 * Класс агента. Реализует 4 поведения, соответствующие заданию.
 * Fields:
 * - initiator - является ли агент инициатором расчета
 * - curX - текущее на данной итерации расчета значение Х
 * - delta - ширина интервала слева и справа от Х
 */
public class FunctionAgent extends Agent {
    private boolean initiator;
    private double curX;
    private double delta;

    public FunctionAgent() {
        this.initiator = false;
        this.curX = 0.0;
        this.delta = 0.0;
    }

    @Override
    protected void setup() {
        System.out.println("Agent " + this.getLocalName() + " was started");
        // Подключаем всех агентов к топику
        String topic = "Topic";
        TopicManagementHelper topicHelper = null;
        AID jadeTopic = null;
        try {
            topicHelper = (TopicManagementHelper)
                    this.getHelper(TopicManagementHelper.SERVICE_NAME);
            jadeTopic = topicHelper.createTopic(topic);
            topicHelper.register(jadeTopic);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        // Первый агент объявляется инициатором
        if (this.getLocalName().equals("A1")) {
            makeInitiator();
            setCurX(0.0);
            setDelta(10.0);
        }
        // Остальные агенты обычные
        addBehaviour(new ReceiveCountingRequest(jadeTopic, null));
        addBehaviour(new SendCountingRequest(this, 1000, jadeTopic, d -> -d*d+5));
    }

    /**
     * Является ли агент инициатором расчета.
     * @return true - является, иначе false
     */
    public boolean isInitiator() {
        return initiator;
    }

    /**
     * Назначить агента инициатором расчета.
     */
    public void makeInitiator() {
        initiator = true;
    }

    /**
     * Сбросить поле инициации (агент больше не является инициатором)
     */
    public void dismayedInitiator() {
        initiator = false;
    }

    /**
     * Получить текущее значение Х
     * @return curX
     */
    public double getCurX() {
        return curX;
    }

    /**
     * Определить текущее значение Х
     * @param curX
     */
    public void setCurX(double curX) {
        this.curX = curX;
    }

    /**
     * Получить текущее значение delta
     * @return delta
     */
    public double getDelta() {
        return delta;
    }

    /**
     * Сеттер поля delta (поле должно быть неотрицательным)
     * @param delta
     */
    public void setDelta(double delta) {
        if (delta > 0) {
            this.delta = delta;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
