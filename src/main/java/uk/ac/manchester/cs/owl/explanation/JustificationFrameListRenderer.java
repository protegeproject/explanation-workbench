package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameListRenderer;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 19/03/2012
 */
public class JustificationFrameListRenderer extends OWLFrameListRenderer {

    public JustificationFrameListRenderer(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
        setHighlightUnsatisfiableClasses(false);
        setHighlightUnsatisfiableProperties(false);
    }



    @Override
    protected OWLObject getIconObject(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return null;
    }
}
