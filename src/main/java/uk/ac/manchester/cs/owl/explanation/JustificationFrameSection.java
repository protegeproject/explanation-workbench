package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Comparator;
import java.util.List;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 19/03/2012
 */
public class JustificationFrameSection extends AbstractOWLFrameSection<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom> {

    private static String LABEL = "Explanation for: ";

    private boolean filled = false;
    
    public JustificationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends Explanation<OWLAxiom>> owlFrame) {
        super(editorKit, LABEL, owlFrame);
    }



    @Override
    public String getLabel() {
        Explanation<OWLAxiom> explanation = getRootObject();
        if(explanation == null) {
            return "";
        }
        OWLModelManager modelManager = getOWLEditorKit().getOWLModelManager();
        String rendering = modelManager.getRendering(explanation.getEntailment()).replaceAll("\\s+", " ");
        return LABEL + rendering;
    }

    @Override
    protected OWLAxiom createAxiom(OWLAxiom object) {
        return object;
    }

    @Override
    public OWLObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }

    @Override
    protected void refill(OWLOntology ontology) {
        if(filled) {
            return;
        }
        filled = true;
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        Explanation<OWLAxiom> expl = getRootObject();
        List<OWLAxiom> formatting = formattingManager.getOrdering(expl);
        for(OWLAxiom ax : formatting) {
            int depth = formattingManager.getIndentation(expl, ax);
            JustificationFrameSectionRow row = new JustificationFrameSectionRow(getOWLEditorKit(), this, expl, ax, depth);
            addRow(row);
        }
    }

    @Override
    protected void clear() {
        filled = false;
    }

    public Comparator<OWLFrameSectionRow<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom>> getRowComparator() {
        return null;
    }

    @Override
    public boolean canAdd() {
        return false;
    }

    @Override
    public boolean canAcceptDrop(List<OWLObject> objects) {
        return false;
    }
}
