package uk.ac.manchester.cs.owl.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/03/2012
 */
public class JustificationGeneratorProgressDialog extends JDialog {

    private ExplanationProgressPanel panel = new ExplanationProgressPanel();

    private ExplanationProgressMonitor<OWLAxiom> progressMonitor;
    
    public JustificationGeneratorProgressDialog(Frame owner) {
        super(owner, "Computing explanations", true);
        setContentPane(panel);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dlgSize = getSize();
        setLocation(screenSize.width / 2 - dlgSize.width / 2, screenSize.height / 2 - dlgSize.height / 2);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        progressMonitor = new JustificationGeneratorProgressDialogMonitor();
    }



    public void reset() {
        panel.reset();
    }

    public ExplanationProgressMonitor<OWLAxiom> getProgressMonitor() {
        return progressMonitor;
    }

    private class JustificationGeneratorProgressDialogMonitor implements ExplanationProgressMonitor<OWLAxiom> {

        public void foundExplanation(ExplanationGenerator<OWLAxiom> owlAxiomExplanationGenerator, Explanation<OWLAxiom> explanation, Set<Explanation<OWLAxiom>> explanations) {
            panel.foundExplanation(owlAxiomExplanationGenerator, explanation, explanations);
        }

        public boolean isCancelled() {
            return panel.isCancelled();
        }
    }
}
