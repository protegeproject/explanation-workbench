package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/03/2012
 */
public class JustificationBasedExplanationServiceImpl extends ExplanationService {

    @Override
    public void initialise() throws Exception {
    }

    @Override
    public boolean hasExplanation(OWLAxiom axiom) {
        return axiom instanceof OWLLogicalAxiom;
    }

    @Override
    public ExplanationResult explain(OWLAxiom entailment) {
        WorkbenchPanel workbenchPanel = new WorkbenchPanel(getOWLEditorKit(), entailment);
        return new WorkbenchPanelExplanationResult(workbenchPanel);
    }

    public void dispose() throws Exception {

    }
}
