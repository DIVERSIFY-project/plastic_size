package fr.inria.defuse;

import fr.inria.defuse.spoon.defusers.CounterAbstractVariableDefuser;
import fr.inria.defuse.spoon.defusers.TupleBuilderDefuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * Entry point of the defuse program
 * <p/>
 * <p/>
 * Created by marodrig on 26/03/2015.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        //I hate property files....
        Properties props = new Properties();
        props.load(Main.class.getClassLoader().getResourceAsStream("projects/allprojects.properties"));

        Collection<String> projects = new ArrayList<String>();
        for (String pName : props.getProperty("projects").split(",")) {
            projects.add("projects/" + pName.trim() + ".properties");
        }

        //Defuser p = new CounterAbstractVariableDefuser();
        Defuser p = new CounterAbstractVariableDefuser();
        p.setOutputOptions(props.getProperty("output-filter"));
        p.execute(projects);

    }
}


