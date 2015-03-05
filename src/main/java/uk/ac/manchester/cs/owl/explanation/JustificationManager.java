package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.core.editorkit.EditorKitManager;
import org.protege.editor.core.ui.workspace.WorkspaceManager;
import org.semanticweb.owl.explanation.api.*;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGenerator;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGeneratorFactory;
import org.semanticweb.owlapi.model.*;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.OWLModelManager;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.awt.*;

import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import uk.ac.manchester.cs.owl.explanation.ExplanationProgressPanel;

import javax.swing.*;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge The University Of Manchester Information Management Group Date:
 * 03-Oct-2008
 * Manages aspects of explanation in Protege 4.
 */
public class JustificationManager implements Disposable, OWLReasonerProvider {

    private ExecutorService executorService;

    private final OWLOntologyChangeListener ontologyChangeListener;

    public static final String KEY = "uk.ac.manchester.cs.owl.explanation";

    private OWLModelManager modelManager;

    private CachingRootDerivedGenerator rootDerivedGenerator;

    private List<ExplanationManagerListener> listeners;

    private int explanationLimit;

    private boolean findAllExplanations;

//    private Map<OWLAxiom, Set<Explanation<OWLAxiom>>> regularExplanationCache = new HashMap<OWLAxiom, Set<Explanation<OWLAxiom>>>();
//
//    private Map<OWLAxiom, Set<Explanation<OWLAxiom>>> laconicExplanationCache = new HashMap<OWLAxiom, Set<Explanation<OWLAxiom>>>();

    private JustificationCacheManager justificationCacheManager = new JustificationCacheManager();
    
    private JustificationGeneratorProgressDialog progressDialog;

//    private ExplanationProgressPanel panel = new ExplanationProgressPanel();

    private JustificationManager(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        rootDerivedGenerator = new CachingRootDerivedGenerator(modelManager);
        listeners = new ArrayList<ExplanationManagerListener>();
        explanationLimit = 2;
        findAllExplanations = true;
        progressDialog = new JustificationGeneratorProgressDialog(new JFrame());
        executorService = Executors.newSingleThreadExecutor();
        ontologyChangeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                justificationCacheManager.clear();
            }
        };
        modelManager.addOntologyChangeListener(ontologyChangeListener);
    }


    public OWLReasonerProvider getReasonerProvider() {
        return this;
    }

    public OWLReasonerFactory getReasonerFactory() {
        return new ProtegeOWLReasonerFactoryWrapper(modelManager.getOWLReasonerManager().getCurrentReasonerFactory());
    }

    public int getExplanationLimit() {
        return explanationLimit;
    }

    public void setExplanationLimit(int explanationLimit) {
        this.explanationLimit = explanationLimit;
        fireExplanationLimitChanged();
    }


    public boolean isFindAllExplanations() {
        return findAllExplanations;
    }


    public void setFindAllExplanations(boolean findAllExplanations) {
        this.findAllExplanations = findAllExplanations;
        fireExplanationLimitChanged();
    }


    public OWLReasoner getReasoner() {
        return modelManager.getReasoner();
    }


    /**
     * Gets the number of explanations that have actually been computed for an entailment
     * @param entailment The entailment
     * @param type The type of justification to be counted.
     * @return The number of computed explanations.  If no explanations have been computed this value
     *         will be -1.
     */
    public int getComputedExplanationCount(OWLAxiom entailment, JustificationType type) {
        JustificationCache cache = justificationCacheManager.getJustificationCache(type);
        if(cache.contains(entailment)) {
            return cache.get(entailment).size();
        }
        else {
            return  -1;
        }
    }

    public Set<Explanation<OWLAxiom>> getJustifications(OWLAxiom entailment, JustificationType type) throws ExplanationException {
        JustificationCache cache = justificationCacheManager.getJustificationCache(type);
        if (!cache.contains(entailment)) {
            Set<Explanation<OWLAxiom>> expls = computeJustifications(entailment, type);
            cache.put(expls);
        }
        return cache.get(entailment);
    }

    public Explanation<OWLAxiom> getLaconicJustification(Explanation<OWLAxiom> explanation) {
        Set<Explanation<OWLAxiom>> explanations = getLaconicExplanations(explanation, 1);
        if(explanations.isEmpty()) {
            return Explanation.getEmptyExplanation(explanation.getEntailment());
        }
        else {
            return explanations.iterator().next();
        }
    }


    private Set<Explanation<OWLAxiom>> computeJustifications(OWLAxiom entailment, JustificationType justificationType) throws ExplanationException {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ont : modelManager.getActiveOntologies()) {
            axioms.addAll(ont.getAxioms());
        }

        ExplanationGeneratorCallable callable = new ExplanationGeneratorCallable(axioms, entailment, justificationType);
        try {
            executorService.submit(callable);
        }
        catch (ExplanationGeneratorInterruptedException e) {
            System.err.println("Explanation generator terminated early by user");
        }
        progressDialog.reset();
        progressDialog.setVisible(true);

        HashSet<Explanation<OWLAxiom>> explanations = new HashSet<Explanation<OWLAxiom>>(callable.found);
        fireExplanationsComputed(entailment);
        return explanations;


    }

    private ExplanationGeneratorFactory<OWLAxiom> getCurrentExplanationGeneratorFactory(JustificationType type) {
        OWLReasoner reasoner = modelManager.getOWLReasonerManager().getCurrentReasoner();
        if(reasoner.isConsistent()) {
            if (type.equals(JustificationType.LACONIC)) {
                OWLReasonerFactory rf = getReasonerFactory();
                return ExplanationManager.createLaconicExplanationGeneratorFactory(rf, progressDialog.getProgressMonitor());
            }
            else {
                OWLReasonerFactory rf = getReasonerFactory();
                return ExplanationManager.createExplanationGeneratorFactory(rf, progressDialog.getProgressMonitor());
            }    
        }
        else {
            if (type.equals(JustificationType.LACONIC)) {
                OWLReasonerFactory rf = getReasonerFactory();
                InconsistentOntologyExplanationGeneratorFactory fac = new InconsistentOntologyExplanationGeneratorFactory(rf, Long.MAX_VALUE);
                return new LaconicExplanationGeneratorFactory<OWLAxiom>(fac);
            }
            else {
                OWLReasonerFactory rf = getReasonerFactory();
                return new InconsistentOntologyExplanationGeneratorFactory(rf, Long.MAX_VALUE);
            }
        }
        
    }

    public OWLOntologyManager getExplanationOntologyManager() {
        return modelManager.getOWLOntologyManager();
    }


    public Set<Explanation<OWLAxiom>> getLaconicExplanations(Explanation<OWLAxiom> explanation, int limit) throws ExplanationException {
        return computeLaconicExplanations(explanation, limit);
    }


    private Set<Explanation<OWLAxiom>> computeLaconicExplanations(Explanation<OWLAxiom> explanation, int limit) throws ExplanationException {
        try {
            if(modelManager.getReasoner().isConsistent()) {
                OWLReasonerFactory rf = getReasonerFactory();
                ExplanationGenerator<OWLAxiom> g = org.semanticweb.owl.explanation.api.ExplanationManager.createLaconicExplanationGeneratorFactory(rf).createExplanationGenerator(explanation.getAxioms());
                return g.getExplanations(explanation.getEntailment(), limit);
            }
            else {
                OWLReasonerFactory rf = getReasonerFactory();
                InconsistentOntologyExplanationGeneratorFactory fac = new InconsistentOntologyExplanationGeneratorFactory(rf, Long.MAX_VALUE);
                LaconicExplanationGeneratorFactory<OWLAxiom> lacFac = new LaconicExplanationGeneratorFactory<OWLAxiom>(fac);
                ExplanationGenerator<OWLAxiom> g = lacFac.createExplanationGenerator(explanation.getAxioms());
                return g.getExplanations(explanation.getEntailment(), limit);
            }
        }
        catch (ExplanationException e) {
            throw new ExplanationException(e);
        }
    }


    public void dispose() {
        rootDerivedGenerator.dispose();
        modelManager.removeOntologyChangeListener(ontologyChangeListener);
    }


    public void addListener(ExplanationManagerListener lsnr) {
        listeners.add(lsnr);
    }

    public void removeListener(ExplanationManagerListener lsnr) {
        listeners.remove(lsnr);
    }

    protected void fireExplanationLimitChanged() {
        for (ExplanationManagerListener lsnr : new ArrayList<ExplanationManagerListener>(listeners)) {
            lsnr.explanationLimitChanged(this);
        }
    }

    protected void fireExplanationsComputed(OWLAxiom entailment) {
        for (ExplanationManagerListener lsnr : new ArrayList<ExplanationManagerListener>(listeners)) {
            lsnr.explanationsComputed(entailment);
        }
    }

    public static synchronized JustificationManager getExplanationManager(OWLModelManager modelManager) {
        JustificationManager m = modelManager.get(KEY);
        if (m == null) {
            m = new JustificationManager(modelManager);
            modelManager.put(KEY, m);
        }
        return m;
    }


    private class ExplanationGeneratorCallable implements Callable<Set<Explanation<OWLAxiom>>>, ExplanationProgressMonitor<OWLAxiom> {

        private Set<OWLAxiom> axioms;

        private OWLAxiom axiom;

        private int limit = Integer.MAX_VALUE;

        private Set<Explanation<OWLAxiom>> found = new HashSet<Explanation<OWLAxiom>>();

        private JustificationType justificationType;

        private ExplanationGeneratorCallable(Set<OWLAxiom> axioms, OWLAxiom axiom, JustificationType justificationType) {
            this.axioms = axioms;
            this.axiom = axiom;
            this.justificationType = justificationType;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        public Set<Explanation<OWLAxiom>> call() throws Exception {
            found.clear();
            ExplanationGeneratorFactory<OWLAxiom> factory = getCurrentExplanationGeneratorFactory(justificationType);
            ExplanationGenerator<OWLAxiom> delegate = factory.createExplanationGenerator(axioms, this);
            progressDialog.reset();
            try {
                if (findAllExplanations) {
                    delegate.getExplanations(axiom);
                }
                else {
                    delegate.getExplanations(axiom, limit);
                }
            }
            finally {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        progressDialog.setVisible(false);
                    }
                });
            }

            return found;
        }

        public void foundExplanation(ExplanationGenerator<OWLAxiom> explanationGenerator, Explanation<OWLAxiom> explanation, Set<Explanation<OWLAxiom>> explanations) {
            progressDialog.getProgressMonitor().foundExplanation(explanationGenerator, explanation, explanations);
            found.add(explanation);
        }

        public boolean isCancelled() {
            return progressDialog.getProgressMonitor().isCancelled();
        }
    }

}
