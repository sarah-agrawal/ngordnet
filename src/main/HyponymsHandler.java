package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;

import java.util.*;

public class HyponymsHandler extends NgordnetQueryHandler {
    private WordNet w;
    private NGramMap ngm;

    public HyponymsHandler(WordNet w, NGramMap ngm) {
        this.w = w;
        this.ngm = ngm; //frequency-counting logic
    }

    @Override
    public String handle(NgordnetQuery q) {
        Set<String> commonHyponyms = w.traverse(q.words());    //candidates, ALWAYS computed regardless of k
        Map<String, Double> totalCounts = new TreeMap<>();     //word -> total occurrences over the range

        //k == 0
        if (q.k() == 0) {
            return commonHyponyms.toString();
        }

        //k != 0
        //get map of total count of each word b/w years
        for (String s : commonHyponyms) {
            TreeMap<Integer, Double> ts = ngm.countHistory(s, q.startYear(), q.endYear());
            double total = 0;
            for (Integer i : ts.keySet()) {
                total += ts.get(i);
            }
            if (total == 0) {
                continue;       //never occurred in this range -> not a valid candidate
            }
            totalCounts.put(s, total);
        }

        //get highest count in map, add to kCommonHyp, remove from totalCounts, continue k times
        Set<String> kCommonHyponyms = new TreeSet<>(); //alphabetized automatically
        for (int i = 0; i < q.k(); i++) {
            String highestWord = null;
            double highestCount = 0;

            //in case k > total hyponyms
            if (totalCounts.isEmpty()) {
                break;      //fewer than k candidates qualify -> stop early, return what we have
            }

            for (String k : totalCounts.keySet()) {
                if (totalCounts.get(k) > highestCount) {
                    highestCount = totalCounts.get(k);
                    highestWord = k;
                }
            }

            kCommonHyponyms.add(highestWord);
            totalCounts.remove(highestWord);    //so it can't be picked again next round
        }
        return kCommonHyponyms.toString();
    }
}
