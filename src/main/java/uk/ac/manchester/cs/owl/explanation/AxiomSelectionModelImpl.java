package uk.ac.manchester.cs.owl.explanation;

import uk.ac.manchester.cs.owl.explanation.AxiomSelectionModel;
import uk.ac.manchester.cs.owl.explanation.AxiomSelectionListener;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.*;
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
 * 09-Oct-2008<br><br>
 */
public class AxiomSelectionModelImpl implements AxiomSelectionModel {

    private Set<OWLAxiom> selectedAxioms;

    private List<AxiomSelectionListener> listeners;


    public AxiomSelectionModelImpl() {
        selectedAxioms = new HashSet<OWLAxiom>();
        listeners = new ArrayList<AxiomSelectionListener>();
    }


    public void setAxiomSelected(OWLAxiom axiom, boolean b) {
        if(b) {
            if(!selectedAxioms.contains(axiom)) {
                selectedAxioms.add(axiom);
                fireEvent(axiom, true);
            }
        }
        else {
            if(selectedAxioms.contains(axiom)) {
                selectedAxioms.remove(axiom);
                fireEvent(axiom, false);
            }
        }
    }

    protected void fireEvent(OWLAxiom axiom, boolean added) {
        for(AxiomSelectionListener lsnr : new ArrayList<AxiomSelectionListener>(listeners)) {
            if(added) {
                lsnr.axiomAdded(this, axiom);
            }
            else {
                lsnr.axiomRemoved(this, axiom);
            }
        }
    }

    public Set<OWLAxiom> getSelectedAxioms() {
        return Collections.unmodifiableSet(selectedAxioms);
    }


    public void addAxiomSelectionListener(AxiomSelectionListener lsnr) {
        listeners.add(lsnr);
    }


    public void removeAxiomSelectionListener(AxiomSelectionListener lsnr) {
        listeners.remove(lsnr);
    }
}
