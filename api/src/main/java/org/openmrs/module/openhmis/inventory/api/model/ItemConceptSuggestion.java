package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.Concept;
import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

public class ItemConceptSuggestion extends BaseSerializableOpenmrsMetadata {

    private Item item;
    private Concept concept;
    private boolean conceptAccepted;

    public ItemConceptSuggestion() {}

    public ItemConceptSuggestion(Item item, Concept concept, boolean conceptAccepted) {
        this.item = item;
        this.concept = concept;
        this.conceptAccepted = conceptAccepted;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public boolean isConceptAccepted() {
        return conceptAccepted;
    }

    public void setConceptAccepted(Boolean conceptAccepted) {
        this.conceptAccepted = conceptAccepted == null ? false : conceptAccepted;
    }

    public Integer getItemId() {
        return item.getId();
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer id) {

    }

}
