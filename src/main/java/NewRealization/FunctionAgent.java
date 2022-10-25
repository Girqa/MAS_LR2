package NewRealization;

import AdditionalClasses.JadePatternProvider;
import Annotations.AutoRunnableAgent;
import NewRealization.Behaviours.CounterBehaviour;
import NewRealization.Behaviours.InitiatorBehaviour;
import jade.core.Agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@AutoRunnableAgent(name = "A", count = 3)
public class FunctionAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Agent " + this.getLocalName() + " was started");
        // Объявляем агентов считаводами
        JadePatternProvider.registerYellowPage(this, "Counter");
        // Первый агент объявляется инициатором
        if (this.getLocalName().endsWith("1")) {
            Map<String, Double> values = new HashMap<>();
            values.put("x", new Random().nextDouble()*10-5);
            values.put("delta", new Random().nextDouble()*10-5);
            addBehaviour(new InitiatorBehaviour(values));
            addBehaviour(new CounterBehaviour(this, d -> -d*d+5));
        } else if (this.getLocalName().endsWith("2")) {
            addBehaviour(new CounterBehaviour(this, d -> 2*d+5));
        } else {
            addBehaviour(new CounterBehaviour(this, Math::sin));
        }
    }
}
