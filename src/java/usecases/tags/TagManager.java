package usecases.tags;

import entities.Item;
import entities.Tag;
import persistence.PersistenceInterface;
import persistence.relations.MapsRelations;

import java.io.IOException;
import java.util.*;

/**
 * Manager of tag including methods to get information from tags or make changes to tags
 */
public class TagManager {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;
    private final MapsRelations relationalMapper;

    /**
     * Initializes this class.
     *
     * @param gateway          Objectsthat saves to persistence
     * @param relationalMapper Objects that maps relations
     */
    public TagManager(PersistenceInterface gateway, MapsRelations relationalMapper) {
        this.gateway = gateway;
        this.relationalMapper = relationalMapper;
    }


    /**
     * Tags an item with the relevant tag names.
     *
     * @param tagNames A list of tag names.
     * @param itemId   The unique item id
     * @return True iff tagging was successful
     * @throws IOException An IOException
     */
    public boolean tagItem(String tagNames, int itemId) throws IOException {
        List<String> tagList = stringParser(tagNames);
        this.createManyTags(tagList);
        this.associateItem(itemId, this.getTags(tagList));
        return true;
    }

    /**
     * Finds tag objects by their string representation.
     *
     * @param tagNames A list of tags names
     * @return A list of Tag objects
     */
    public List<Tag> getTags(List<String> tagNames) throws IOException {
        List<Tag> result = new ArrayList<>();
        for (String tagName : tagNames) {
            for (Tag tag : this.all()) {
                if (tag.getTagName().equals(tagName)) result.add(tag);
            }
        }
        return result;
    }

    /**
     * Get a map with item ids and its tags.
     *
     * @return A map with item ids and its tags.
     * @throws IOException An IOException
     */
    public Map<Integer, List<Tag>> getItemTagMap() throws IOException {
        List<Item> items = gateway.all(Item.class);
        Map<Integer, List<Tag>> result = new HashMap<>();
        for (Item item : items) {
            List<Tag> tags = item.relation(this.relationalMapper, "tags", Tag.class);
            result.put(item.getKey(), tags);
        }
        return result;
    }

    /**
     * Returns all tags.
     *
     * @return A list of all tag objects
     * @throws IOException An IOException.
     */
    public List<Tag> all() throws IOException {
        return gateway.all(Tag.class);
    }


/********************************************************************************************************
 *
 * Helper Methods
 *
 *********************************************************************************************************/

    /**
     * Creates one new tag.
     *
     * @param tagName A string representing a tag.
     * @throws IOException An IOException
     */
    private void createOneTag(String tagName) throws IOException {
        if (!tagName.isEmpty() && this.tagNotExist(tagName)) {
            gateway.create(new Tag(tagName.trim()), Tag.class);
        }
    }

    /**
     * Creates a bunch of tags.
     *
     * @param tagList A list of tags
     * @return True iff the tags were successfully created.
     * @throws IOException An IOException
     */
    private void createManyTags(List<String> tagList) throws IOException {
        for (String tag : tagList) {
            this.createOneTag(tag);
        }
    }

    /**
     * Associates item with tag.
     *
     * @param itemId the unique id of the item
     * @param tags   A list of tag objects
     * @throws IOException An IOException.
     */
    private void associateItem(int itemId, List<Tag> tags) throws IOException {
        for (Tag tag : tags) {
            tag.associateItem(itemId);
        }
        gateway.update(tags, Tag.class);
    }

    /**
     * parse a string of tags to a list of strings.
     *
     * @param tagName name of tag
     * @return list of string, where every string is a tag name
     */
    private List<String> stringParser(String tagName) {
        String arrTag[] = tagName.trim().split("\\s*,\\s*");
        return removeDuplicate(arrTag);
    }

    /**
     * Checks if the tagName already exists.
     *
     * @param tagName the string representation of this tag.
     * @return Whether this tag exists
     * @throws IOException An IOException.
     */
    private boolean tagNotExist(String tagName) throws IOException {
        List<Tag> tagList = gateway.all(Tag.class);
        for (Tag tag : tagList) {
            if (tag.getTagName().equals(tagName.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * A helper method that remove duplicate
     *
     * @param strArray A string array with duplicates
     * @return An array list without duplicates
     */
    private List<String> removeDuplicate(String[] strArray) {
        Set<String> hashSet = new HashSet<>(Arrays.asList(strArray));
        return new ArrayList<>(hashSet);
    }


}
