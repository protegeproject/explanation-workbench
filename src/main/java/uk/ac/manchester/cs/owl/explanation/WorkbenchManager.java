package uk.ac.manchester.cs.owl.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
import uk.ac.manchester.cs.owl.explanation.JustificationManager;
import uk.ac.manchester.cs.owl.explanation.JustificationType;

import java.util.Set;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 */
public class WorkbenchManager {

    private WorkbenchSettings workbenchSettings = new WorkbenchSettings();

    private JustificationManager justificationManager;

    private OWLAxiom entailment;
    
    public WorkbenchManager(JustificationManager justificationManager, OWLAxiom entailment) {
        this.justificationManager = justificationManager;
        this.entailment = entailment;
    }

    public WorkbenchSettings getWorkbenchSettings() {
        return workbenchSettings;
    }

    public OWLAxiom getEntailment() {
        return entailment;
    }

    public Set<Explanation<OWLAxiom>> getJustifications(OWLAxiom entailment) {
        JustificationType justificationType = workbenchSettings.getJustificationType();
        return justificationManager.getJustifications(entailment, justificationType);
    }

    public int getJustificationCount(OWLAxiom entailment) {
        JustificationType justificationType = workbenchSettings.getJustificationType();
        return justificationManager.getComputedExplanationCount(entailment, justificationType);
    }


    public JustificationManager getJustificationManager() {
        return justificationManager;
    }
    
    public int getPopularity(OWLAxiom axiom) {
        int count = 0;
        Set<Explanation<OWLAxiom>> justifications = justificationManager.getJustifications(entailment, workbenchSettings.getJustificationType());
        for(Explanation<OWLAxiom> justification : justifications) {
            if(justification.contains(axiom)) {
                count++;
            }
        }
        return count;
    }
    
}
