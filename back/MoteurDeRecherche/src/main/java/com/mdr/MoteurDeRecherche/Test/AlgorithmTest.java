package com.mdr.MoteurDeRecherche.Test;

import com.mdr.MoteurDeRecherche.Utils.Algorithms;
import junit.framework.TestCase;
import java.util.HashSet;
import java.util.Set;

public class AlgorithmTest extends TestCase {


    /**
     *  If the books are totaly different, the distance of jaccard is 1.0
     * @throws Exception
     */
    public void testDistanceJaccardDifferentContent() throws Exception {
        Set<String> p1= new HashSet<String>();
        Set<String> p2= new HashSet<String>();

        p1.add("bonjour");
        p1.add("monsieur");
        p1.add("Dupont");

        p2.add("bonsoir");
        p2.add("madame");
        p2.add("Hyvette");


        assertEquals(1.0, Algorithms.distanceJaccard(p1,p2));
    }

    /**
     *  If the books are the same, the distance of jaccard is 0.0
     * @throws Exception
     */
    public void testDistanceJaccardSameContent() throws Exception {
        Set<String> p1= new HashSet<String>();
        Set<String> p2= new HashSet<String>();

        p1.add("bonjour");
        p1.add("monsieur");
        p1.add("Dupont");

        p2.add("bonjour");
        p2.add("monsieur");
        p2.add("Dupont");


        assertEquals(0.0, Algorithms.distanceJaccard(p1,p1));
    }
}
