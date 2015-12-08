package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 19/03/2012
 */
public class JustificationFrameExplanationDisplay extends JPanel implements ExplanationDisplay, AxiomSelectionListener {

    private Explanation<OWLAxiom> explanation;
    
    private JustificationFrame frame;

    private final JustificationFrameList frameList;

    private OWLEditorKit editorKit;

    private Explanation<OWLAxiom> lacExp;

    private WorkbenchManager workbenchManager;

    private AxiomSelectionModel axiomSelectionModel;
    
    private boolean transmittingSelectionToModel = false;

    public JustificationFrameExplanationDisplay(OWLEditorKit editorKit, AxiomSelectionModel selectionModel, WorkbenchManager workbenchManager, Explanation<OWLAxiom> explanation) {
        this.editorKit = editorKit;
        this.workbenchManager = workbenchManager;
        this.axiomSelectionModel = selectionModel;
        this.explanation = explanation;
        frame = new JustificationFrame(editorKit);
        setLayout(new BorderLayout());
        frameList =  new JustificationFrameList(editorKit, selectionModel, workbenchManager, frame);
        add(frameList, BorderLayout.NORTH);
        frame.setRootObject(explanation);
        frameList.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));

        frameList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                transmitSelectionToModel();
            }
        });

        axiomSelectionModel.addAxiomSelectionListener(new AxiomSelectionListener() {
            public void axiomAdded(AxiomSelectionModel source, OWLAxiom axiom) {
                respondToAxiomSelectionChange();
            }

            public void axiomRemoved(AxiomSelectionModel source, OWLAxiom axiom) {
                respondToAxiomSelectionChange();
            }
        });
    }

    private void respondToAxiomSelectionChange() {
        if(!transmittingSelectionToModel) {
            frameList.clearSelection();
            frameList.repaint(frameList.getVisibleRect());
        }
        frameList.repaint(frameList.getVisibleRect());
    }


    private void transmitSelectionToModel() {
        try {
            transmittingSelectionToModel = true;
            for(int i = 1; i < frameList.getModel().getSize(); i++) {
                Object element = frameList.getModel().getElementAt(i);
                if(element instanceof JustificationFrameSectionRow) {
                    JustificationFrameSectionRow row = (JustificationFrameSectionRow) element;
                    OWLAxiom ax = row.getAxiom();
                    axiomSelectionModel.setAxiomSelected(ax, frameList.isSelectedIndex(i));
                }
            }
        }
        finally {
            transmittingSelectionToModel = false;
        }
    }



    public Explanation<OWLAxiom> getExplanation() {
        return explanation;
    }

    public void dispose() {
        frame.dispose();
    }

    public void setDisplayLaconicExplanation(boolean b) {
        if (b) {
            Explanation<OWLAxiom> lacExp = getLaconicExplanation();
            if (lacExp != null) {
                frame.setRootObject(lacExp);
            }
        }
        else {
            frame.setRootObject(explanation);
        }
    }

    private Explanation<OWLAxiom> getLaconicExplanation() {
        if(lacExp != null) {
            return lacExp;
        }
        return workbenchManager.getJustificationManager().getLaconicJustification(explanation);
    }

    public void axiomAdded(AxiomSelectionModel source, OWLAxiom axiom) {
        System.out.println("SEL: " + axiom);
    }

    public void axiomRemoved(AxiomSelectionModel source, OWLAxiom axiom) {
    }
}
