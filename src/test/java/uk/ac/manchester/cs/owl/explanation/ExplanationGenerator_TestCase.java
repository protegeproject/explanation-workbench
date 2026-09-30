package uk.ac.manchester.cs.owl.explanation;

import org.junit.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ExplanationGenerator_TestCase {

    @Test
    public void shouldExplainInferredSubclassAxiom() {
        OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        OWLClass a = dataFactory.getOWLClass(IRI.create("urn:test#A"));
        OWLClass b = dataFactory.getOWLClass(IRI.create("urn:test#B"));
        OWLClass d = dataFactory.getOWLClass(IRI.create("urn:test#D"));
        OWLClass e = dataFactory.getOWLClass(IRI.create("urn:test#E"));
        OWLObjectProperty hasPart = dataFactory.getOWLObjectProperty(IRI.create("urn:test#hasPart"));

        Set<OWLAxiom> axioms = new HashSet<>();
        axioms.add(dataFactory.getOWLSubClassOfAxiom(b, a));
        axioms.add(dataFactory.getOWLEquivalentClassesAxiom(
                d,
                dataFactory.getOWLObjectSomeValuesFrom(hasPart, a)));
        axioms.add(dataFactory.getOWLSubClassOfAxiom(
                e,
                dataFactory.getOWLObjectSomeValuesFrom(hasPart, b)));

        OWLAxiom entailment = dataFactory.getOWLSubClassOfAxiom(e, d);
        ExplanationGenerator<OWLAxiom> generator = ExplanationManager
                .createExplanationGeneratorFactory(
                        new ReasonerFactory(),
                        OWLManager::createOWLOntologyManager)
                .createExplanationGenerator(axioms);

        Set<Explanation<OWLAxiom>> explanations = generator.getExplanations(entailment, 1);

        assertEquals(1, explanations.size());
        assertEquals(axioms, explanations.iterator().next().getAxioms());
    }
}
