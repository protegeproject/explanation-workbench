package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owl.explanation.api.ExplanationException;
import org.semanticweb.owl.explanation.api.RootDerivedReasoner;
import org.semanticweb.owl.explanation.impl.rootderived.StructuralRootDerivedReasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 */
public class CachingRootDerivedGenerator implements RootDerivedReasoner, Disposable, OWLModelManagerListener, OWLOntologyChangeListener {

    private OWLModelManager modelManager;

    private Set<OWLClass> rootUnsatClses;

    private boolean dirty = true;

    public CachingRootDerivedGenerator(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        rootUnsatClses = new HashSet<OWLClass>();
        modelManager.addListener(this);
        modelManager.addOntologyChangeListener(this);
        dirty = true;
    }


    public Set<OWLClass> getRootUnsatisfiableClasses() throws ExplanationException {
        if(dirty) {
            rootUnsatClses.clear();
            dirty = false;
            OWLReasonerFactory rf = new ProtegeOWLReasonerFactoryWrapper(modelManager.getOWLReasonerManager().getCurrentReasonerFactory());
            RootDerivedReasoner gen = new StructuralRootDerivedReasoner(OWLManager.createOWLOntologyManager(),
                                                                                    modelManager.getReasoner(),
                                                                                    rf);
            rootUnsatClses.addAll(gen.getRootUnsatisfiableClasses());
        }
        return Collections.unmodifiableSet(rootUnsatClses);
    }

    public Set<OWLClass> getDependentChildClasses(OWLClass owlClass) {
        return Collections.emptySet();
    }

    public Set<OWLClass> getDependentDescendantClasses(OWLClass owlClass) {
        return Collections.emptySet();
    }

    public void ontologiesChanged(List<? extends OWLOntologyChange> list) throws OWLException {
        dirty = true;
    }


    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) ||
                event.isType(EventType.ONTOLOGY_CLASSIFIED)) {
            dirty = true;
        }
    }


    public void dispose() {
        modelManager.removeListener(this);
        modelManager.removeOntologyChangeListener(this);
    }
}
