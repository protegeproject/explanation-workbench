package uk.ac.manchester.cs.owl.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.*;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 */
public class JustificationCache {
    
    private Map<OWLAxiom, Set<Explanation<OWLAxiom>>> cache = new HashMap<OWLAxiom, Set<Explanation<OWLAxiom>>>();

    public boolean contains(OWLAxiom entailment) {
        return cache.containsKey(entailment);
    }

    public Set<Explanation<OWLAxiom>> get(OWLAxiom entailment) {
        Set<Explanation<OWLAxiom>> explanations = cache.get(entailment);
        if(explanations == null) {
            return Collections.emptySet();
        }
        return new HashSet<Explanation<OWLAxiom>>(explanations);
    }

    public void put(Explanation<OWLAxiom> explanation) {
        Set<Explanation<OWLAxiom>> expls = cache.get(explanation.getEntailment());
        if(expls == null) {
            expls = new HashSet<Explanation<OWLAxiom>>();
            cache.put(explanation.getEntailment(), expls);
        }
        expls.add(explanation);
    }
    
    public void put(Set<Explanation<OWLAxiom>> explanations) {
        for(Explanation<OWLAxiom> expl : explanations) {
            put(expl);    
        }
    }
    
    public void clear() {
        cache.clear();
    }
    
    public void clear(OWLAxiom entailment) {
        cache.remove(entailment);
    }
    
    
}
