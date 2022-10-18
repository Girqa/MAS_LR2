import AgentsFactory.AgentFactory;
import AgentsFactory.AgentDescription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Agents.FunctionAgent;

public class Main {
    public static void main(String[] args) {
        List<AgentDescription> agentsDescs = Arrays.asList(
                new AgentDescription("A1", FunctionAgent.class),
                new AgentDescription("A2", FunctionAgent.class),
                new AgentDescription("A3", FunctionAgent.class)
        );
        AgentFactory.createAgents(agentsDescs);
    }
}
