import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import Annotations.AutoRunnableAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.Properties;
import org.reflections.Reflections;


public class Main {
    public static void main(String[] args) {
        Reflections r = new Reflections("NewRealization");

        Map<String, String> agents =new HashMap<>();

        Set<Class<?>> classes = r.getTypesAnnotatedWith(AutoRunnableAgent.class);
        for (Class<?> annotatedClass: classes) {
            AutoRunnableAgent anno = annotatedClass.getAnnotation(AutoRunnableAgent.class);
            System.out.println(annotatedClass.getPackageName());
            for (int i = 0; i < anno.count(); i++) {
                agents.put(anno.name()+(i+1), "NewRealization.FunctionAgent");
            }
        }

        Properties pp = parseCmdLineArgs(agents);
        ProfileImpl p = new ProfileImpl(pp);
        Runtime.instance().setCloseVM(true);
        Runtime.instance().createMainContainer(p);


    }

    private static Properties parseCmdLineArgs(Map<String, String> createdAgents) {
        Properties props = new Properties();
        props.setProperty("gui", "true");

        StringBuilder agents = new StringBuilder();

        for (Map.Entry<String, String> entry: createdAgents.entrySet()) {
            agents.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        props.setProperty("agents", agents.toString());
        return props;
    }
}
