package entities;

import persistence.relations.HasRelations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag extends AbstractBaseEntity implements HasRelations {

    /**
     * The unique id of this tag.
     */
    private int tagId;

    /**
     * The name of this tag
     */
    private String tagName;

    /**
     * The list of item ids associated with this tag
     */
    private List<Integer> itemIds = new ArrayList<>();

    /**
     * Create a new tag.
     *
     * @param tagName The name of this tag.
     */
    public Tag(String tagName) {
        this.tagId = 0;
        this.tagName = tagName;
    }

    /**
     * Return the id of the Tag
     *
     * @return The primary key of this tag.
     */
    @Override
    public int getKey() {
        return this.tagId;
    }

    /**
     * set the id of the tag
     *
     * @param id an integer representing the unique id of this entity.
     */
    @Override
    public void setKey(int id) {
        this.tagId = id;
    }

    /**
     * get the name of the tag
     *
     * @return the name of the tag
     */
    public String getTagName() {
        return tagName;
    }


    /**
     * associate the item with the tag
     *
     * @param itemId The item id of the item to associate with this tag.
     */
    public void associateItem(int itemId) {
        if (!this.itemIds.contains(itemId)) {
            this.itemIds.add(itemId);
        }
    }


    /**
     * A string representation of this entity.
     *
     * @return A string representation of this entity.
     */
    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                ", itemIds=" + itemIds +
                '}';
    }

    /**
     * define the relation between tag and item
     *
     * @return A map defining the relations.
     */
    @Override
    public Map<String, List<Integer>> getDefinedRelations() {
        Map<String, List<Integer>> relationMap = new HashMap<>();
        relationMap.put("items", this.itemIds);
        return relationMap;
    }

}