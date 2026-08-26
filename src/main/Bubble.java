package main;

import java.util.ArrayList;
import java.util.List;

//represents a single bubble with a synset
public class Bubble {

    private List<String> synonyms = new ArrayList<>();  //words in this synset
    private List<Integer> ids = new ArrayList<>();    //ids of the synsets this points to (its hyponyms)
    //stored as ids for unique identification (if stored as word can have duplicates)

    /** only takes words not ids since synset file (words) and hyponym files (edges)
     read separately (edges not known when bubble created) */
    public Bubble(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    /** appends 1 edge at a time;
    a bubble's edges can be spread across multiple separate lines in hyponym file,
    so keep calling as more lines for same link encountered */
    public void addId(int id) {
        this.ids.add(id);
    }

    /** getters for accessing vars in WordNet */
    public List<String> getSynonyms() {
        return synonyms;
    }

    public List<Integer> getIds() {
        return ids;
    }

}
