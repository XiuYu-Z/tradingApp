package usecases.query;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Abstract Class of Fetcher: executes a Abstract Query and returns the results in the specified format of the query.
 * Can execute a number of pre-defined filters, such as whether the item is approve or not etc.
 * Can return a list of objects.
 */
abstract public class AbstractFetcher {

    /**
     * Run the fetcher.
     *
     * @param query An AbstractQueryBuilder class that holds the query
     * @throws IOException An IOException
     */
    protected void fetch(AbstractQueryBuilder query) throws IOException {
        //Only fetch once
        if (!query.hasBeenFetched()) {
            this.reset();
            this.all();
            //Then we run filter methods
            try {
                this.filterBoolean(query);
                this.filterInteger(query);
                this.filterList(query);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                //We do nothing if there's no such filter.
            }
            query.setFetched(true);
        }
    }

    /**
     * A method that resets the filter and the query.
     *
     * @throws IOException An IOException
     */
    abstract protected void reset();

    /**
     * A method that returns all of the instances of the type that we are fetching
     *
     * @throws IOException An IOException
     */
    abstract protected void all() throws IOException;


/********************************************************************************************************
 *
 * Methods that decide which filters to run
 *
 *********************************************************************************************************/

    /**
     * Runs boolean filters.
     *
     * @param query AbstractQueryBuilder
     * @throws NoSuchMethodException     If there isn't a method with the name
     * @throws IllegalAccessException    If there's an illegal access exception during the invocation of the filter.
     * @throws InvocationTargetException If there's an exception thrown during invocation of the method.
     */
    protected void filterBoolean(AbstractQueryBuilder query)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        for (Map.Entry<String, Boolean> entry : query.getBooleanFilters().entrySet()) {
            if (entry.getValue() != null) {
                this.getClass().getDeclaredMethod(entry.getKey()).invoke(this);
            }
        }

    }


    /**
     * Runs integer filters.
     *
     * @param query AbstractQueryBuilder
     * @throws NoSuchMethodException     If there isn't a method with the name
     * @throws IllegalAccessException    If there's an illegal access exception during the invocation of the filter.
     * @throws InvocationTargetException If there's an exception thrown during invocation of the method.
     */
    protected void filterInteger(AbstractQueryBuilder query)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        for (Map.Entry<String, Integer> entry : query.getIntegerFilters().entrySet()) {
            if (entry.getValue() != null) {
                this.getClass().getDeclaredMethod(entry.getKey(), Integer.class).invoke(this, entry.getValue());
            }
        }

    }


    /**
     * Runs list filters.
     *
     * @param query AbstractQueryBuilder
     * @throws NoSuchMethodException     If there isn't a method with the name
     * @throws IllegalAccessException    If there's an illegal access exception during the invocation of the filter.
     * @throws InvocationTargetException If there's an exception thrown during invocation of the method.
     */
    protected void filterList(AbstractQueryBuilder query)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        for (Map.Entry<String, List<String>> entry : query.getListFilters().entrySet()) {
            if (entry.getValue() != null) {
                this.getClass().getDeclaredMethod(entry.getKey(), List.class).invoke(this, entry.getValue());
            }
        }

    }


}
