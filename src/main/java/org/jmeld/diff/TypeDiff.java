package org.jmeld.diff;

/**
* Created by alberto on 16/11/14.
*/
public enum TypeDiff {
    ADD("Add"),
    DELETE("Del"),
    CHANGE("Change");

    String description;

    private TypeDiff(String description){
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
