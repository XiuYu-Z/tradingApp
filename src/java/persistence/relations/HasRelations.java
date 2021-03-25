package persistence.relations;

import persistence.Persistable;

import java.io.IOException;
import java.util.List;
import java.util.Map;


//Maps relations between persistable instances
public interface HasRelations extends Persistable {

    /**
     * Gets the relations of an entity.
     * For example, an item can be associated with an owner (many-to-one).
     * For another example, an item can be associated with many wishlists,
     * and a wishlist can have many tags (many-to-many)
     *
     * @param relationMapper A concrete instance that maps relations
     * @param relationName   The name of the relation we are looking for
     * @param subjectEntity  The entity class of the relation
     * @param <T>            needs to extend HasRelations
     * @return A list of the subject entity
     * @throws IOException An IOException
     */
    default <T extends HasRelations> List<T> relation(MapsRelations relationMapper, String relationName, Class<T> subjectEntity) throws IOException {
        return relationMapper.get(relationName, this, subjectEntity);
    }


    /**
     * Each string should represent a relation to this particular entity.
     * Each list of integers should represent the unique id's of the other entity that should be associated with this particular entity.
     * <p>
     * For example {"items", [1,5,8,9]}
     * <p>
     * This means that the items with unique id 1,5,8,9 should be associated with this entity.
     *
     * @return Map of relations
     */
    Map<String, List<Integer>> getDefinedRelations();


}
