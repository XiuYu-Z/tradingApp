package persistence.relations;

import java.io.IOException;
import java.util.List;

public interface MapsRelations {

    /**
     * Retrieves the relation.
     *
     * @param relationName     The name of the relation we are looking for
     * @param requestingEntity The entity whose associates we are looking for
     * @param subjectEntity    The associated entities
     * @param <T>              A class that extends HasRelations
     * @return A list of associated subject entities
     * @throws IOException An IOException
     */
    <T extends HasRelations> List<T> get(String relationName, HasRelations requestingEntity,
                                         Class<T> subjectEntity) throws IOException;

}
