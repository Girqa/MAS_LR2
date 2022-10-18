package AgentsFactory;

import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.List;

public class AgentFactory {

    public static void createAgents(List<AgentDescription> list){

        ProfileImpl profile = new ProfileImpl();
        profile.setParameter("gui", "true");
        jade.core.Runtime.instance().setCloseVM(true);

        //Часть активирующая топики
        profile.setParameter("services", " jade.core.messaging.TopicManagementService");

        AgentContainer mainContainer = jade.core.Runtime.instance().createMainContainer(profile);
        try {
            mainContainer.start();
            try {

                //Создание агентов в рамках одной платформы
                for (AgentDescription agentDescription : list) {
                    AgentController newAgent = mainContainer.createNewAgent(agentDescription.getAgentName(), agentDescription.getAClass().getName(), new Object[]{});
                    newAgent.start();
                }

            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        } catch (ControllerException e) {
            e.printStackTrace();
        }

    }

}
