package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.explanation.io.InconsistentOntologyPlugin;
import org.protege.editor.owl.ui.explanation.io.InconsistentOntologyPluginInstance;
import org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSection;
import org.semanticweb.owl.explanation.api.ConsoleExplanationProgressMonitor;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import uk.ac.manchester.cs.owl.explanation.WorkbenchPanel;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/03/2012
 */
public class InconsistentOntologyExplanationPluginImpl implements InconsistentOntologyPluginInstance {

    private OWLEditorKit editorKit;
    
    public void setup(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    public void explain(OWLOntology ontology) {

        OWLModelManager owlModelManager = editorKit.getOWLModelManager();
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        OWLSubClassOfAxiom entailment = df.getOWLSubClassOfAxiom(df.getOWLThing(), df.getOWLNothing());
        final WorkbenchPanel panel = new WorkbenchPanel(editorKit, entailment);

        JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg =op.createDialog("Inconsistent ontology explanation");
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                panel.dispose();
            }
        });
        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.setVisible(true);


    }

    public void initialise() throws Exception {
    }

    public void dispose() throws Exception {
    }
}
