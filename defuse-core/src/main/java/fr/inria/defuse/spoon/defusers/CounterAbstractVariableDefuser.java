package fr.inria.defuse.spoon.defusers;

import fr.inria.defuse.Defuser;
import fr.inria.defuse.spoon.processors.abstractvar.CounterOfAbstractVariables;
import fr.inria.defuse.spoon.processors.abstractvar.TypeCounterCollector;
import fr.inria.defuse.spoon.processors.abstractvar.TypeCounterCollector.TypeCounter;
import org.apache.log4j.Logger;
import spoon.processing.AbstractProcessor;

import java.util.*;

/**
 * Created by marodrig on 01/04/2015.
 */
public class CounterAbstractVariableDefuser extends AbstractDefuser implements Defuser {
    final static Logger logger = Logger.getLogger(CounterAbstractVariableDefuser.class);

    ArrayList<CounterOfAbstractVariables> counters;
    ArrayList<TypeCounterCollector> collectors;

    @Override
    protected void initProcesors() {
        counters = new ArrayList<>();
        collectors = new ArrayList<>();
    }

    @Override
    protected int iterationCount() {
        return 2;
    }

    @Override
    protected AbstractProcessor<?> nextProcessor(int iteration, String projectName) {
        switch (iteration) {
            case 0:
                counters.add(new CounterOfAbstractVariables());
                return counters.get(counters.size() - 1);
            case 1:
                collectors.add(new TypeCounterCollector(projectName));
                return collectors.get(collectors.size() - 1);
        }
        throw new RuntimeException("Invalid iteration " + iteration);
    }


    @Override
    protected void outputResults() {
        for (TypeCounterCollector c : collectors) {
            System.out.println("//" + c.getProjectName());
            System.out.println("['Global', null, 0, 0],");
            int i = 0;
            for (Map.Entry<String, TypeCounter> e : c.getCounter().entrySet()) {
                TypeCounter tc = e.getValue();
                int inheritors = e.getKey().equals("java.lang.Object") ? 0 : tc.variables; //Ignore inheritors of object
                System.out.print("['" + e.getKey() +
                        "', 'Global'," + inheritors + "," + tc.getInheritSize() + "]");
                if (i < c.getCounter().size()) System.out.println(",");
                else System.out.println("]);");
                i++;
            }
        }
    }

    private static void printHorizontal(ArrayList<CounterOfAbstractVariables> counters,
                                        ArrayList<TypeCounterCollector> collectors) {
        logger.info("Project, Abstract var, Interface, Fails, Total");
        for (CounterOfAbstractVariables c : counters) {
            logger.info(c.getReport().toCVSLine());
        }
        logger.info("*****************************");
        logger.info("****   RESULTS     **********");
        logger.info("*****************************");


        String SEP = ",";

        ArrayList<Iterator<Map.Entry<String, TypeCounter>>> horizontalIterators = new ArrayList<>();

        Collections.sort(collectors, new Comparator<TypeCounterCollector>() {
            @Override
            public int compare(TypeCounterCollector o1, TypeCounterCollector o2) {
                return o1.getCounter().size() - o2.getCounter().size();
            }
        });

        for (TypeCounterCollector collector : collectors) {
            horizontalIterators.add(collector.getCounter().entrySet().iterator());
            System.out.print(collector.getProjectName() + SEP + collector.getProjectName() + SEP);
        }
        System.out.println();
        boolean go = true;
        while (go) {
            go = false;
            int i = 0;
            for (Iterator<Map.Entry<String, TypeCounter>> it : horizontalIterators) {
                go |= it.hasNext();
                if (it.hasNext()) {
                    Map.Entry<String, TypeCounter> c = it.next();
                    //Search NON empty
                    while (it.hasNext() && (c.getValue().inheritors.size() == 0 || c.getValue().variables == 0))
                        c = it.next();
                    /*
                    int  k = (c.getValue().inheritors.size() + 1) * c.getValue().variables;
                    //Print non-empty*/
                    if (c.getValue().inheritors.size() > 0 && c.getValue().variables > 0)
                        System.out.print(c.getValue().inheritors.size() + SEP + c.getValue().variables);
                    else System.out.print(SEP);
                } else System.out.print(SEP);
                if (i < horizontalIterators.size() - 1) System.out.print(SEP);
                i++;
            }
            System.out.println("");
        }

        logger.info("*****************************");
    }


}
