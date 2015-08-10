package fr.inria.defuse;

import java.util.Collection;

/**
 * Created by marodrig on 01/04/2015.
 */
public interface Defuser {

    /**
     * Collection of property files located in the resources folder of the project
     * @param propertyFiles Path to property files located on the resources folder
     */
    public abstract void execute(Collection<String> propertyFiles);

    /**
     * Sets the options of the output
     * @param property
     */
    void setOutputOptions(String property);
}
