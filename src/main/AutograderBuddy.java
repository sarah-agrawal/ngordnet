package main;

import browser.NgordnetQueryHandler;

public class AutograderBuddy {
    /** Returns a HyponymHandler */
    //builds a working handler directly from file paths (no browser/server needed)
    public static NgordnetQueryHandler getHyponymsHandler(
            String wordHistoryFile, String yearHistoryFile,
            String synsetFile, String hyponymFile) {

        NGramMap ngm = new NGramMap(wordHistoryFile, yearHistoryFile);
        WordNet w = new WordNet(synsetFile, hyponymFile);
        return new HyponymsHandler(w, ngm);
    }
}
