package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationException;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.semanticweb.owlapi.model.OWLClassExpression;
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
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 04-Oct-2008<br><br>
 * <p/>
 * Displays a set of explanations
 */
public class WorkbenchPanel extends JPanel implements Disposable, OWLModelManagerListener, EntailmentSelectionListener, AxiomSelectionModel, ExplanationManagerListener {

    private OWLEditorKit editorKit;

    private JComponent explanationDisplayHolder;

    private JScrollPane scrollPane;

    private JSpinner maxExplanationsSelector = new JSpinner();

    private Collection<ExplanationDisplay> panels;

    private AxiomSelectionModelImpl selectionModel;


    private WorkbenchManager workbenchManager;


//    private JCheckBox showAllExplanationsCheckBox = new JCheckBox();


    public WorkbenchPanel(OWLEditorKit ek, OWLAxiom entailment) {
        this.editorKit = ek;
        JustificationManager justificationManager = JustificationManager.getExplanationManager(ek.getOWLModelManager());
        this.workbenchManager = new WorkbenchManager(justificationManager, entailment);
        setLayout(new BorderLayout());

        selectionModel = new AxiomSelectionModelImpl();

        panels = new ArrayList<ExplanationDisplay>();

        editorKit.getModelManager().addListener(this);
        explanationDisplayHolder = new Box(BoxLayout.Y_AXIS);

        JPanel pan = new HolderPanel(new BorderLayout());
        pan.add(explanationDisplayHolder, BorderLayout.NORTH);
        scrollPane = new JScrollPane(pan);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(null);
        scrollPane.setOpaque(false);


        JPanel rhsPanel = new JPanel(new BorderLayout(7, 7));
        JPanel explanationListPanel = new JPanel(new BorderLayout());
        explanationListPanel.add(scrollPane);
        explanationListPanel.setMinimumSize(new Dimension(10, 10));


        JComponent headerPanel = createHeaderPanel();
        JPanel headerPanelHolder = new JPanel(new BorderLayout());
        headerPanelHolder.add(headerPanel, BorderLayout.WEST);
        explanationListPanel.add(headerPanelHolder, BorderLayout.NORTH);

        rhsPanel.add(explanationListPanel);
        add(rhsPanel);

        refill();
    }

    private JComponent createHeaderPanel() {

        GridBagLayout layout = new GridBagLayout();

        JComponent headerPanel = new JPanel(layout);

        final WorkbenchSettings workbenchSettings = workbenchManager.getWorkbenchSettings();
        JRadioButton regularButton = new JRadioButton(new AbstractAction("Show regular justifications") {
            public void actionPerformed(ActionEvent e) {
                workbenchSettings.setJustificationType(JustificationType.REGULAR);
                refill();
            }
        });
        regularButton.setSelected(true);
        headerPanel.add(regularButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 30), 0, 0));

        JRadioButton laconicButton = new JRadioButton(new AbstractAction("Show laconic justifications") {
            public void actionPerformed(ActionEvent e) {
                workbenchSettings.setJustificationType(JustificationType.LACONIC);
                refill();
            }
        });
        headerPanel.add(laconicButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 30), 0, 0));


        ButtonGroup bg = new ButtonGroup();
        bg.add(regularButton);
        bg.add(laconicButton);


        SpinnerModel spinnerModel = new SpinnerNumberModel(workbenchSettings.getLimit(), 1, 900, 1);
        maxExplanationsSelector.setModel(spinnerModel);
        maxExplanationsSelector.setEnabled(!workbenchSettings.isFindAllExplanations());

        final JRadioButton computeAllExplanationsRadioButton = new JRadioButton();
        computeAllExplanationsRadioButton.setAction(new AbstractAction("All justifications") {
            public void actionPerformed(ActionEvent e) {
                workbenchSettings.setFindAllExplanations(computeAllExplanationsRadioButton.isSelected());
                maxExplanationsSelector.setEnabled(!workbenchSettings.isFindAllExplanations());
                refill();
            }
        });
        final JRadioButton computeMaxExplanationsRadioButton = new JRadioButton();
        computeMaxExplanationsRadioButton.setAction(new AbstractAction("Limit justifications to") {
            public void actionPerformed(ActionEvent e) {
                workbenchSettings.setFindAllExplanations(false);
                maxExplanationsSelector.setEnabled(!workbenchSettings.isFindAllExplanations());
                refill();
            }
        });
        ButtonGroup limitButtonGroup = new ButtonGroup();
        limitButtonGroup.add(computeAllExplanationsRadioButton);
        limitButtonGroup.add(computeMaxExplanationsRadioButton);

        if (workbenchSettings.isFindAllExplanations()) {
            computeAllExplanationsRadioButton.setSelected(true);
        }
        else {
            computeMaxExplanationsRadioButton.setSelected(true);
        }

        headerPanel.add(computeAllExplanationsRadioButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        headerPanel.add(computeMaxExplanationsRadioButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


        final Timer spinnerUpdateTimer = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                workbenchSettings.setLimit((Integer) maxExplanationsSelector.getValue());
                refill();
            }
        });

        spinnerUpdateTimer.setRepeats(false);

        headerPanel.add(maxExplanationsSelector, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        maxExplanationsSelector.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        maxExplanationsSelector.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                spinnerUpdateTimer.restart();
            }
        });

        return headerPanel;
    }


    public Dimension getMinimumSize() {
        return new Dimension(10, 10);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void explanationLimitChanged(JustificationManager explanationManager) {
        maxExplanationsSelector.setEnabled(!workbenchManager.getWorkbenchSettings().isFindAllExplanations());
//        showAllExplanationsCheckBox.setSelected(expMan.isFindAllExplanations());
        selectionChanged();
    }


    public void explanationsComputed(OWLAxiom entailment) {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class HolderPanel extends JPanel implements Scrollable {


        public HolderPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }


        public Dimension getPreferredScrollableViewportSize() {
            return super.getPreferredSize();
        }


        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 30;
        }


        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 30;
        }


        public boolean getScrollableTracksViewportWidth() {
            return true;
        }


        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }


    public void removeSelectedAxioms() {
//        RepairPanel.showDialog(editorKit.getWorkspace(), editorKit);
//        repMan.applyPlan();
//        editorKit.getModelManager().getOWLReasonerManager().classifyAsynchronously(Collections.emptySet());
        repaint();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void selectionChanged() {
        refill();
    }


    protected ExplanationDisplay createExplanationDisplay(Explanation<OWLAxiom> explanation, int num, int total, int limit) {
        return new JustificationFrameExplanationDisplay(editorKit, this, workbenchManager, explanation);//new ExplanationTable(editorKit, selectionModel, explanation);
    }


    private void refill() {
        try {
            for (ExplanationDisplay panel : panels) {
                panel.dispose();
            }
            explanationDisplayHolder.removeAll();
            explanationDisplayHolder.validate();

            Set<Explanation> allExplanations = new HashSet<Explanation>();
            OWLAxiom entailment = workbenchManager.getEntailment();
            WorkbenchSettings settings = workbenchManager.getWorkbenchSettings();


            Set<Explanation<OWLAxiom>> justifications = workbenchManager.getJustifications(entailment);
            List<Explanation<OWLAxiom>> exps = getOrderedExplanations(justifications);
            allExplanations.addAll(exps);
            int count = 1;
            for (Explanation<OWLAxiom> exp : exps) {
                final ExplanationDisplay explanationDisplayPanel = createExplanationDisplay(exp, count, exps.size(), settings.getLimit());

                ExplanationDisplayList list = new ExplanationDisplayList(editorKit, workbenchManager, explanationDisplayPanel, count);
                list.setBorder(BorderFactory.createEmptyBorder(2, 0, 10, 0));
                explanationDisplayHolder.add(list);
                panels.add(explanationDisplayPanel);
                count++;
                if (!settings.isFindAllExplanations() && count > settings.getLimit()) {
                    break;
                }
            }
            explanationDisplayHolder.add(Box.createVerticalStrut(10));
            scrollPane.validate();
        }
        catch (ExplanationException e) {
            e.printStackTrace();
        }
    }

//    protected double getComplexity(Explanation<OWLAxiom> explanation) {
//        ProtegeOWLReasonerFactoryWrapper rf = new ProtegeOWLReasonerFactoryWrapper(editorKit);
//        ComplexityCalculator calculator = new DefaultComplexityCalculator(rf);
//        return calculator.computeComplexity(explanation.getEntailment(), explanation.getAxioms());
//    }

    protected List<Explanation<OWLAxiom>> getOrderedExplanations(Set<Explanation<OWLAxiom>> explanations) {
        List<Explanation<OWLAxiom>> orderedExplanations = new ArrayList<Explanation<OWLAxiom>>();
        orderedExplanations.addAll(explanations);
        Collections.sort(orderedExplanations, new Comparator<Explanation<OWLAxiom>>() {
            public int compare(Explanation<OWLAxiom> o1, Explanation<OWLAxiom> o2) {
                int diff = getAxiomTypes(o1).size() - getAxiomTypes(o2).size();
                if (diff != 0) {
                    return diff;
                }
                diff = getClassExpressionTypes(o1).size() - getClassExpressionTypes(o2).size();
                if (diff != 0) {
                    return diff;
                }
                return o1.getSize() - o2.getSize();
            }
        });
//        Collections.sort(orderedExplanations, new Comparator<Explanation<OWLAxiom>>() {
//            public int compare(Explanation<OWLAxiom> o1, Explanation<OWLAxiom> o2) {
//
//                double cpx1 = getComplexity(o1);
//                double cpx2 = getComplexity(o2);
//                if(cpx1 < cpx2) {
//                    return -1;
//                }
//                else if(cpx1 > cpx2) {
//                    return 1;
//                }
//                else {
//                    return 0;
//                }
//            }
//        });
        return orderedExplanations;
    }


    private Set<AxiomType<?>> getAxiomTypes(Explanation<OWLAxiom> explanation) {
        Set<AxiomType<?>> result = new HashSet<AxiomType<?>>();
        for (OWLAxiom ax : explanation.getAxioms()) {
            result.add(ax.getAxiomType());
        }
        return result;
    }


    private Set<ClassExpressionType> getClassExpressionTypes(Explanation<OWLAxiom> explanation) {
        Set<ClassExpressionType> result = new HashSet<ClassExpressionType>();
        for (OWLAxiom ax : explanation.getAxioms()) {
            for (OWLClassExpression ce : ax.getNestedClassExpressions()) {
                result.add(ce.getClassExpressionType());
            }
        }
        return result;
    }

    public void dispose() {
        editorKit.getModelManager().removeListener(this);
        for (ExplanationDisplay panel : panels) {
            panel.dispose();
        }
    }


    public void handleChange(OWLModelManagerChangeEvent event) {

    }


    public void axiomSelectionChanged(ExplanationDisplay source) {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Implementation of selection model


    public void addAxiomSelectionListener(AxiomSelectionListener lsnr) {
        selectionModel.addAxiomSelectionListener(lsnr);
    }


    public void removeAxiomSelectionListener(AxiomSelectionListener lsnr) {
        selectionModel.removeAxiomSelectionListener(lsnr);
    }


    public void setAxiomSelected(OWLAxiom axiom, boolean b) {
        selectionModel.setAxiomSelected(axiom, b);
    }


    public Set<OWLAxiom> getSelectedAxioms() {
        return selectionModel.getSelectedAxioms();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension workspaceSize = editorKit.getWorkspace().getSize();
        int width = (int) (workspaceSize.getWidth() * 0.8);
        int height = (int) (workspaceSize.getHeight() * 0.7);
        return new Dimension(width, height);
    }
}
