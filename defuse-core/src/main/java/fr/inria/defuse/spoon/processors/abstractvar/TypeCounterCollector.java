package fr.inria.defuse.spoon.processors.abstractvar;

import org.apache.log4j.Logger;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Collect types of variables
 * <p/>
 * Created by marodrig on 27/03/2015.
 */
public class TypeCounterCollector extends AbstractProcessor<CtVariable> {

    int iteration = 0;

    public String getProjectName() {
        return projectName;
    }

    private final String projectName;

    public static class TypeCounter {
        public HashSet<String> inheritors = new HashSet<>();
        public int variables = 0;

        public int getInheritSize() {
            return inheritors.size();
        }
    }

    final static Logger logger = Logger.getLogger(TypeCounterCollector.class);

    public HashMap<String, TypeCounter> getCounter() {
        return counter;
    }

    private HashMap<String, TypeCounter> counter;

    public TypeCounterCollector(String p) {
        counter = new HashMap<>();
        this.projectName = p;
    }

    @Override
    public void process(CtVariable ctVariable) {
        try {

            //Obtain the type of the variable
            CtTypeReference inheritor = ctVariable.getType();
            if (inheritor == null && ctVariable.getDefaultExpression() != null)
                inheritor = ctVariable.getDefaultExpression().getType();
            if (inheritor == null) {
                logger.error("Cannot find type of variable " + ctVariable);
                return;
            }
            //Obtain the component of the array
            while (inheritor instanceof CtArrayTypeReference)
                inheritor = ((CtArrayTypeReference) inheritor).getComponentType();


            //Count interfaces
            try {
                for (Object t : inheritor.getSuperInterfaces())
                    countInheritor((CtTypeReference) t, inheritor);
            } catch (SpoonException ex) {
                logger.warn("Unable to register interfaces of type " + inheritor);
            }

            //Augment the variable count for the given type
            if (!counter.containsKey(inheritor.getQualifiedName())) {
                TypeCounter tc = new TypeCounter();
                counter.put(inheritor.getQualifiedName(), tc);
                tc.variables++;
            } else counter.get(inheritor.getQualifiedName()).variables++;

            //Register inheritor
            try {
                CtTypeReference r = inheritor.getSuperclass();
                while (r != null) {
                    countInheritor(r, inheritor);
                    r = r.getSuperclass();
                }
            } catch (SpoonException ex) {
                logger.warn("Unable to register all superclasses of type " + inheritor);
            }

            iteration++;

        } catch (Exception e) {
            logger.warn("Exception at iteration " + iteration + ": " + e.getMessage());
        }
    }

    /**
     * Register that a given type r has an inheritor
     *
     * @param r
     * @param inheritor
     */
    public void countInheritor(CtTypeReference r, CtTypeReference inheritor) {
        try {
            String inQN = inheritor.getQualifiedName();
            String rQN = r.getQualifiedName();

            if (!counter.containsKey(rQN)) counter.put(rQN, new TypeCounter());
            HashSet<String> in = counter.get(rQN).inheritors;
            if (!in.contains(inQN)) in.add(inQN);

        } catch (NullPointerException ex) {
            logger.warn("Count Inheritor: " + ex.getMessage());
        }
    }

}
