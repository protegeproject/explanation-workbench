package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.core.Disposable;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
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
 * 03-Oct-2008
 *
 * An interface to a component that can display justifications.
 */
public interface ExplanationDisplay extends Disposable {

    /**
     * Gets the explanation that is displayed by this object.
     * @return The explanation.
     */
    Explanation<OWLAxiom> getExplanation();

//    void setSelectedAxioms(Set<OWLAxiom> axioms);

//    Set<OWLAxiom> getSelectedAxioms();

//    void addAxiomSelectionListener(ExplanationDisplayListener listener);

//    void removeAxiomSelectionListener(ExplanationDisplayListener listener);

    void dispose();


//    public interface ExplanationDisplayListener {
//
//        void axiomSelectionChanged(ExplanationDisplay source);
//    }


    /**
     * Asks the display to show a laconic version of an explanation
     * @param b <code>true</code> if the display should show a laconic version, otherwise <code>false</code>
     */
    void setDisplayLaconicExplanation(boolean b);
    
}
