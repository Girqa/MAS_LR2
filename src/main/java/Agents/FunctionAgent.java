package Agents;

import Behaviours.ReceiveCountingRequest;
import Behaviours.SendCountingRequest;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;

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

        if (this.getLocalName().equals("A1")) {
            makeInitiator();
            setCurX(0.0);
            setDelta(10.0);
        }

        addBehaviour(new ReceiveCountingRequest(jadeTopic, null));
        addBehaviour(new SendCountingRequest(this, 1000, jadeTopic));
    }

    public boolean isInitiator() {
        return initiator;
    }

    public void makeInitiator() {
        initiator = true;
    }

    public void dismayedInitiator() {
        initiator = false;
    }

    public double getCurX() {
        return curX;
    }

    public void setCurX(double curX) {
        this.curX = curX;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        if (delta > 0) {
            this.delta = delta;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
