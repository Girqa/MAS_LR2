package NewRealization.Behaviours;

import FunctionInterfaces.OptimizationFunction;
import NewRealization.Behaviours.SubBehaviours.GetTransferRequestBehaviour;
import NewRealization.Behaviours.SubBehaviours.ReciveRequestBehaviour;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;

public class CounterBehaviour extends ParallelBehaviour {
    public CounterBehaviour(Agent a, OptimizationFunction func) {
        super(a, WHEN_ALL);

        addSubBehaviour(new ReciveRequestBehaviour(func));
        addSubBehaviour(new GetTransferRequestBehaviour());
    }
}
