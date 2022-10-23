package Agents;

import java.lang.Math;
import AdditionalClasses.JadePatternProvider;
import Behaviours.ReceiveCountingRequest;
import Behaviours.ReceiveInitCountingMessage;
import Behaviours.SendCountingRequest;
import Behaviours.TransferInitCounting;
import FunctionInterfaces.OptimizationFunction;
import jade.core.Agent;

/**
 * Класс агента. Реализует 4 поведения, соответствующие заданию.
 * Fields:
 * - initiator - является ли агент инициатором расчета
 * - transferInitiation - необходимо ли передать очередь агенту
 * - curX - текущее на данной итерации расчета значение Х
 * - delta - ширина интервала слева и справа от Х
 * - eps - необходимая точность
 */
public class FunctionAgent extends Agent {
    private boolean initiator;
    private boolean transferInitiation;
    private double curX;
    private double delta;
    private final double eps;
    private boolean finished;

    public FunctionAgent() {
        this.initiator = false;
        this.curX = 0.0;
        this.delta = 0.0;
        this.eps = 0.01;
        this.finished = false;
    }

    @Override
    protected void setup() {
        System.out.println("Agent " + this.getLocalName() + " was started");
        // Объявляем агентов считаводами
        JadePatternProvider.registerYellowPage(this, "Counter");
        // Первый агент объявляется инициатором
        setCurX(100);
        setDelta(1);
        if (this.getLocalName().equals("A1")) {
            makeInitiator();
            addBehaviours(100, d -> -d*d+5);
        } else if (this.getLocalName().equals("A2")) {
            addBehaviours(100, d -> 2*d+5);
        } else {
            addBehaviours(100, d -> Math.sin(d));
        }
    }

    /**
     * Добавляет назначенные агенту поведения
     * @param period интервалы между опросами агентов
     * @param func оптимизируемая функция агента
     */
    private void addBehaviours(int period, OptimizationFunction func) {
        addBehaviour(new ReceiveCountingRequest(this, func));
        addBehaviour(new SendCountingRequest(this, period, func));
        addBehaviour(new TransferInitCounting(this));
        addBehaviour(new ReceiveInitCountingMessage(this));
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
        setTransferInitiation(true);
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

    /**
     * Нужно ли передать очередь расчета?
     * @return true - если нужно, иначе false
     */
    public boolean isTransferInitiation() {
        return transferInitiation;
    }

    public void setTransferInitiation(boolean transferInitiation) {
        this.transferInitiation = transferInitiation;
    }

    public double getEps() {
        return eps;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
