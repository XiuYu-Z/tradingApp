package controller.forms;

import java.util.List;

public class TagFilterForm {

    private List<String> tag;

    /**
     * Gets all the tags.
     *
     * @return All the tags.
     */
    public List<String> getTag() {
        return tag;
    }

    /**
     * Sets all the tags.
     *
     * @param tag All the tags.
     */
    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    /**
     * Adds one tag.
     *
     * @param tagName One tag name.
     */
    public void addTag(String tagName) {
        tag.add(tagName.trim());
    }

    /**
     * Removes a tag.
     *
     * @param tagName One tag name.
     */
    public void removeTag(String tagName) {
        tag.remove(tagName.trim());
    }


}