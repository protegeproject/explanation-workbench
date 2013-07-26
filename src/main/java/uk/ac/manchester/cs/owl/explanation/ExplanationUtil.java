package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.core.ui.util.UIUtil;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;

import javax.swing.*;
import java.io.File;
import java.net.URI;
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
 * 24-Oct-2008<br><br>
 */
public class ExplanationUtil {

    public static void saveExplanationAsOntology(OWLEditorKit editorKit, Explanation explanation) throws
                                                                                                   OWLOntologyCreationException,
                                                                                                   OWLOntologyChangeException,
                                                                                                   OWLOntologyStorageException {
        File f = UIUtil.saveFile((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, editorKit.getWorkspace()), "Save ontology as",
                        CollectionFactory.createSet("owl", "txt", "rdf"), "JustificationExtract.owl");
        if(f == null) {
            return;
        }
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ont = man.createOntology(explanation.getAxioms());
        OWLDataFactory df = man.getOWLDataFactory();
        IRI annotationIRI = IRI.create("http://owl.cs.manchester.ac.uk/explanation/annotations/inferredAxiom");
        String entailmentRendering = editorKit.getOWLModelManager().getRendering((OWLAxiom) explanation.getEntailment());
        OWLAnnotationProperty prop = df.getOWLAnnotationProperty(annotationIRI);
        OWLAnnotation anno = df.getOWLAnnotation(prop, df.getOWLStringLiteral(entailmentRendering));
        man.applyChange(new AddOntologyAnnotation(ont, anno));
        man.saveOntology(ont, IRI.create(f));
    }
}
