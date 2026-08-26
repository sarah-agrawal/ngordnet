package main;

import edu.princeton.cs.algs4.In;
import java.util.*;

public class WordNet {

    /** need 2 directions of lookup
     * - wordToID: find a STARTING POINT from user input (word -> which bubble(s))
     * - idToSynset: WALK the graph once you have a bubble's id (id -> its actual contents)
     */
    private Map<String, List<Integer>> wordToID = new HashMap<>();  //word->list of ids of word (word can be in >1 bubble)
    private Map<Integer, Bubble> idToSynset = new HashMap<>();      //id->bubble (node w/ words + edges)

    public WordNet(String synsetFile, String hyponymFile) {
        //read file
        In inSynset = new In(synsetFile);       //id, synset words (space-separated), definition
        In inHyponym = new In(hyponymFile);     //id, hyponymId1, hyponymId2, ...

        while (inSynset.hasNextLine()) {

            //--- PASS 1: read synset file -> build every Bubble (words) + wordToID ---
            String nextLine = inSynset.readLine();
            String[] splitLine = nextLine.split(",");

            int id = Integer.parseInt(splitLine[0]);
            String[] word = splitLine[1].split(" ");    //split synset words into an array

            //create the bubble for this id, with its words, and store it - edges still empty for now
            idToSynset.put(id, new Bubble(Arrays.asList(word)));

            //register every word in this bubble so we can find this id later by word
            for (String w : word) {
                if (wordToID.containsKey(w)) {
                    wordToID.get(w).add(id); //word already seen elsewhere -> add this id too
                } else {
                    List<Integer> l = new ArrayList<>();
                    l.add(id);
                    wordToID.put(w, l); //first time seeing this word -> new list
                }
            }
        }

        //read hyponym file
        //--- PASS 2: read hyponym file -> attach edges to bubbles that ALREADY exist ---
        while (inHyponym.hasNextLine()) {

            String nextLine = inHyponym.readLine();
            String[] splitLine = nextLine.split(",");
            int synsetId = Integer.parseInt(splitLine[0]); //the bubble these edges belong to

            //add the hyponym ids to the given bubble
            for (int i = 1; i < splitLine.length; i++) {
                //look up the EXISTING bubble (built in pass 1) and append this edge
                idToSynset.get(synsetId).addId(Integer.parseInt(splitLine[i]));
            }
        }
    }

    // k == 0 case: hyponyms common to EVERY word in the list
    public Set<String> traverse(List<String> words) {
        Set<String> commonHyponyms = new TreeSet<>(); //the running answer, narrowed word by word
        Set<String> currentWords;                     //this word's own full hyponym set
        boolean firstWord = true;                     //first word has nothing to compare against yet

        for (String word : words) {
            currentWords = traverseOneWord(word); //no comparison possible yet; just store
            if (firstWord) {
                commonHyponyms = currentWords;
                firstWord = false;
            } else {
                //narrow commonHyponyms down to only words ALSO present in currentWords
                Set<String> currentCommon = commonHyponyms; //snapshot of the answer BEFORE this round
                commonHyponyms = new TreeSet<>();           //wipe it clean, ready to refill w/ survivors
                for (String w : currentCommon) {
                    if (currentWords.contains(w)) {
                        commonHyponyms.add(w);          //w survives - keep it
                    }
                    //else: w silently drops out, wasn't common to THIS word
                }
            }
        }
        return commonHyponyms;
    }

    //finds every hyponym of ONE word (ignores k, startYear, endYear entirely)
    public Set<String> traverseOneWord(String w) {
        Set<Integer> visitedIds = new HashSet<>();  //bubbles already fully processed, this call only
        Set<String> traversedWords = new TreeSet<>(); //collected words, alphabetized automatically

        //word never appears in the dataset at all -> no hyponyms, return empty rather than crash
        if (wordToID.get(w) == null) {
            return traversedWords;
        }

        List<Integer> id = wordToID.get(w); //could be MULTIPLE bubbles (word has multiple meanings)
        for (Integer i : id) {
            traverseHelper(i, visitedIds, traversedWords); //walk from EACH starting bubble
        }
        return traversedWords;
    }

    //recursively walks the graph starting at "id", collecting every reachable bubble's words
    public void traverseHelper(int id, Set<Integer> visitedIds, Set<String> traversedWords) {
        //already been here in THIS traversal -> stop, nothing more to do (prevents infinite loops / redo work)
        if (visitedIds.contains(id)) {
            return;
        }
        visitedIds.add(id); //mark THIS bubble visited before doing anything else

        List<String> synonyms = idToSynset.get(id).getSynonyms();
        List<Integer> hyponymsIds = idToSynset.get(id).getIds();
        traversedWords.addAll(synonyms);    //collect this bubble's words into the shared result set

        //recurse into every bubble this one points to; each call checks/marks itself independently
        for (Integer h : hyponymsIds) {
            traverseHelper(h, visitedIds, traversedWords);
        }
    }
}
