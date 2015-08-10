package fr.inria.defuse;

import org.eclipse.sisu.Description;

import java.util.Properties;

/**
 * A custom configuration class
 *
 * Created by marodrig on 27/03/2015.
 */
public class InputConfiguration extends Properties {

    private static final java.lang.String DESCRIPTION = "description";
    private static String PROJECT = "project";

    private static String SRC = "src";

    /**
     * Get the sources project path
     * @return A string
     */
    public String getSrcPath() {
       return  getProjectPath() + "/" + getProperty(SRC);
    }


    /**
     * Gets the project path
     * @return
     */
    public String getProjectPath() {
        return getProperty(PROJECT);
    }

    public String getDescription() {
        return getProperty(DESCRIPTION);
    }
}
