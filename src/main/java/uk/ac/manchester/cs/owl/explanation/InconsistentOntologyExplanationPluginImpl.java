package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.explanation.io.InconsistentOntologyPluginInstance;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
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
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                panel.dispose();
                dlg.dispose();
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
