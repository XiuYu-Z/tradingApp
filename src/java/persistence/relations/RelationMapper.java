package persistence.relations;

import entities.Item;
import entities.Meeting;
import entities.Tag;
import entities.Transaction;
import persistence.PersistenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class maps the relations between one entity to another entity.
 * For example, an item may have many tags, and a tag may be associated with many items.
 * For another example, a transaction has a number of meetings, and a meeting will belong to a transaction.
 * <p>
 * Relations must be first registered in this class.
 * <p>
 * Once registered, an entity that implements HasRelations will able to use this following syntax to fetch all the tags associated with it.
 * itemEntity.relation(relationMapper, "tags", Tag.class)  will provide an ArrayList of Tags associated with this Item
 * tagEntity.relation(relationMapper, "items", Item.class) will provide an ArrayList of Items associated with the Tag
 */
public class RelationMapper implements MapsRelations {

    PersistenceInterface persistence;

    /**
     * Holds the reverse relation.
     * For example, we want to log that the reverse of all of the items with tag "x", is the all of the tags of item "y"
     * "Class Name": {"Relation Name": "Reverse Relation", "Relation Name 2": "Reverse Relation 2"}
     * "Tag":{"items": "Item.tags", "meetings": "Meeting.tags"}
     */
    public Map<String, Map<String, String>> reciprocalRelations = new HashMap<>();

    /**
     * Instanates an instance of this class.
     *
     * @param persistence A concrete class that persists data.
     */
    public RelationMapper(PersistenceInterface persistence) {
        this.persistence = persistence;
        this.build();
    }

    /**
     * Allows other classes to add relations
     *
     * @param firstClass         The first class type
     * @param firstRelationName  The relation name relative to the first class type
     * @param secondClass        The second class type
     * @param secondRelationName The relation name relative to the second class type
     */
    public void addRelation(Class<? extends HasRelations> firstClass, String firstRelationName,
                            Class<? extends HasRelations> secondClass, String secondRelationName) {

        this.buildReciprocalRelation(firstClass, firstRelationName, secondClass, secondRelationName);
        this.buildReciprocalRelation(secondClass, secondRelationName, firstClass, firstRelationName);

    }

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
    public <T extends HasRelations> List<T> get(String relationName, HasRelations requestingEntity,
                                                Class<T> subjectEntity) throws IOException {

        boolean requestingEntityHasRelation = requestingEntity.getDefinedRelations().containsKey(relationName);

        //Check if there are any maps in the calling class
        if (requestingEntityHasRelation)
            return this.getForwardRelation(requestingEntity, relationName, subjectEntity);

            //If not, we check if there are any relations in the subject class
        else return this.getReverseRelation(requestingEntity, relationName, subjectEntity);

    }

/********************************************************************************************************
 *
 * Helper Methods
 *
 *********************************************************************************************************/

    /**
     * Register all relations here
     */
    private void build() {
        this.addRelation(Item.class, "tags", Tag.class, "items");
        this.addRelation(Meeting.class, "transactions", Transaction.class, "meetings");
    }


    private <T extends HasRelations> List<T> getForwardRelation(HasRelations requestingEntity, String relationName,
                                                                Class<T> subjectEntity) throws IOException {
        List<Integer> ids = requestingEntity.getDefinedRelations().get(relationName);
        return this.persistence.get(ids, subjectEntity);
    }


    private <T extends HasRelations> List<T> getReverseRelation(HasRelations requestingEntity, String relationName,
                                                                Class<T> subjectEntity) throws IOException {

        //We get the reverse relation name
        String requestingName = requestingEntity.getClass().getName();
        String reverseRelationKey = this.getReverseRelationKey(requestingName, relationName);

        //Get all the subject entities
        List<T> candidates = this.persistence.all(subjectEntity);

        //We filter through all the instances of the subject entity to get the ones with the relations
        List<T> relations = new ArrayList<>();
        for (T candidate : candidates) {
            boolean isRelated = candidate.getDefinedRelations().get(reverseRelationKey).contains(requestingEntity.getKey());
            if (isRelated) relations.add(subjectEntity.cast(candidate));
        }

        return relations;
    }


    private String getReverseRelationKey(String requestingName, String relationKey) {
        String reverseRelationName = this.reciprocalRelations.get(requestingName).get(relationKey);
        return reverseRelationName.split(",")[1];
    }


    private void buildReciprocalRelation(Class<? extends HasRelations> firstClass, String firstRelationName,
                                         Class<? extends HasRelations> secondClass, String secondRelationName) {

        Map<String, String> reverseMaps1 = new HashMap<>();
        reverseMaps1.put(secondRelationName, firstClass.getName() + "," + firstRelationName);
        this.reciprocalRelations.put(secondClass.getName(), reverseMaps1);

    }


}
