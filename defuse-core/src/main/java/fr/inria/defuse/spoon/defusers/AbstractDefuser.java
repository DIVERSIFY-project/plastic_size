package fr.inria.defuse.spoon.defusers;

import fr.inria.defuse.Defuser;
import fr.inria.defuse.InputConfiguration;
import fr.inria.defuse.SpoonMetaFactory;
import fr.inria.diversify.buildSystem.maven.MavenDependencyResolver;
import org.apache.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by marodrig on 01/04/2015.
 */
public abstract class AbstractDefuser implements Defuser {

    final static Logger logger = Logger.getLogger(AbstractDefuser.class);

    protected String outputOptions;

    List<String> projectDescriptions;

    @Override
    public void setOutputOptions(String outputOptions) {
        this.outputOptions = outputOptions;
    }

    /**
     * Initialize all processors of the defuse program
     */
    protected abstract void initProcesors();

    /**
     * Numbers of processing iterations of this program
     * @return
     */
    protected abstract int iterationCount();

    /**
     * Returns the next processor
     * @param iteration
     * @param projectName
     * @return
     */
    protected abstract AbstractProcessor<?> nextProcessor(int iteration, String projectName);

    @Override
    public void execute(Collection<String> propertyFiles) {
        try {
            initProcesors();
            projectDescriptions = new ArrayList<>();

            //Collect data for all programs
            for (String p : propertyFiles) {
                InputConfiguration props = new InputConfiguration();
                props.load(CounterAbstractVariableDefuser.class.getClassLoader().getResourceAsStream(p));
                projectDescriptions.add(props.getDescription());

                logger.info("Project path: " + props.getProjectPath());
                logger.info("Src path: " + props.getSrcPath());
                logger.info("** Start defusing **" + p);

                MavenDependencyResolver resolver = new MavenDependencyResolver();
                resolver.DependencyResolver(props.getProjectPath() + "/pom.xml");
                Factory f = new SpoonMetaFactory().buildNewFactory(props.getSrcPath(), 7);

                ProcessingManager pm = new QueueProcessingManager(f);
                for (int j = 0; j < iterationCount(); j++) {
                    AbstractProcessor c = nextProcessor(j, props.getDescription());
                    pm.addProcessor(c);
                }
                pm.process();
                logger.info("** Defusing of " + p + " completed **");
            }

            //printHorizontal(counters, collectors);
            outputResults();
        } catch (Exception e) {
            logger.fatal("Unable to complete execution because " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Outputs results of the defuser program
     */
    protected abstract void outputResults();

}
