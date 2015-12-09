package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.explanation.api.Explanation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
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
 * 19-Oct-2008
 */
public class ExplanationDisplayList extends JPanel implements Disposable {

    private OWLEditorKit editorKit;

    private ExplanationDisplay display;

    private JCheckBox displayLaconicCheckBox = new JCheckBox();

    private int explanationNumber;

    private int entailmentNumber;

    private WorkbenchManager workbenchManager;

    public ExplanationDisplayList(OWLEditorKit editorKit, WorkbenchManager workbenchManager, ExplanationDisplay display, int explanationNumber) {
        this.editorKit = editorKit;
        this.workbenchManager = workbenchManager;
        this.display = display;
        this.explanationNumber = explanationNumber;
        createUI();

    }


    private void createUI() {
        setLayout(new BorderLayout(2, 2));
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Explanation " + explanationNumber);
        headerPanel.add(label);

        displayLaconicCheckBox.setFont(displayLaconicCheckBox.getFont().deriveFont(10.0f));
        displayLaconicCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        displayLaconicCheckBox.setAction(new AbstractAction("Display laconic explanation") {
            public void actionPerformed(ActionEvent e) {
                displayLaconicExplanation(displayLaconicCheckBox.isSelected());
            }
        });
        if (workbenchManager.getWorkbenchSettings().getJustificationType().equals(JustificationType.LACONIC)) {
            displayLaconicCheckBox.setEnabled(false);
        }
        headerPanel.add(displayLaconicCheckBox);
        add(headerPanel, BorderLayout.NORTH);

        JPanel displayHolder = new JPanel(new BorderLayout());
        Border marginBorder = BorderFactory.createEmptyBorder(0, 20, 0, 0);
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        displayHolder.setBorder(BorderFactory.createCompoundBorder(marginBorder, lineBorder));
        displayHolder.add((JComponent) display);
        add(displayHolder);
    }

    private void displayLaconicExplanation(boolean b) {
        display.setDisplayLaconicExplanation(b);
    }


    public void dispose() {
        display.dispose();
    }

}
