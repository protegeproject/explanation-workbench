package uk.ac.manchester.cs.owl.explanation;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.framelist.ExplainButton;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.framelist.OWLFrameListPopupMenuAction;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 19/03/2012
 */
public class JustificationFrameList extends OWLFrameList<Explanation<OWLAxiom>> {

    public static final Color SINGLE_POPULARITY_COLOR = new Color(170, 70, 15);

    public static final Color MULTI_POPULARITY_COLOR = new Color(10, 75, 175);

    public static final Color ALL_POPULARITY_COLOR = new Color(6, 133, 19);

    private OWLEditorKit editorKit;

    private int buttonRunWidth = 0;

    private AxiomSelectionModel axiomSelectionModel;
    
    private WorkbenchManager workbenchManager;

    public JustificationFrameList(OWLEditorKit editorKit, AxiomSelectionModel axiomSelectionModel, WorkbenchManager workbenchManager, OWLFrame<Explanation<OWLAxiom>> explanationOWLFrame) {
        super(editorKit, explanationOWLFrame);
        this.workbenchManager = workbenchManager;
        this.axiomSelectionModel = axiomSelectionModel;
        this.editorKit = editorKit;
        setWrap(false);
        setCellRenderer(new JustificationFrameListRenderer(editorKit));
        
        Action moveUpAction = new AbstractAction("Move up") {
            public void actionPerformed(ActionEvent e) {
                handleMoveUp();
            }
        };
        getActionMap().put(moveUpAction.getValue(Action.NAME), moveUpAction);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_MASK), moveUpAction.getValue(Action.NAME));


        Action moveDownAction = new AbstractAction("Move down") {
            public void actionPerformed(ActionEvent e) {
                handleMoveDown();
            }
        };
        getActionMap().put(moveDownAction.getValue(Action.NAME), moveDownAction);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK), moveDownAction.getValue(Action.NAME));


        Action increaseIndentation = new AbstractAction("Increase indentation") {
            public void actionPerformed(ActionEvent e) {
                handleIncreaseIndentation();
            }
        };
        getActionMap().put(increaseIndentation.getValue(Action.NAME), increaseIndentation);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_MASK), increaseIndentation.getValue(Action.NAME));


        Action decreaseIndentation = new AbstractAction("decrease indentation") {
            public void actionPerformed(ActionEvent e) {
                handleDecreaseIndentation();
            }
        };
        getActionMap().put(decreaseIndentation.getValue(Action.NAME), decreaseIndentation);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_MASK), decreaseIndentation.getValue(Action.NAME));



    }
    
    
    private void handleMoveUp() {
        OWLAxiom selectedAxiom = getSelectedAxiom();
        if(selectedAxiom == null) {
            return;
        }
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        int newIndex = formattingManager.moveAxiomUp(getRootObject(), selectedAxiom);
        getFrame().setRootObject(getRootObject());
        setSelectedIndex(newIndex + 1);
    }
    
    

    private void handleMoveDown() {
        OWLAxiom selectedAxiom = getSelectedAxiom();
        if(selectedAxiom == null) {
            return;
        }
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        int newIndex = formattingManager.moveAxiomDown(getRootObject(), selectedAxiom);
        getFrame().setRootObject(getRootObject());
        setSelectedIndex(newIndex + 1);
    }
    
    
    private void handleIncreaseIndentation() {
        OWLAxiom selectedAxiom = getSelectedAxiom();
        if(selectedAxiom == null) {
            return;
        }
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        formattingManager.increaseIndentation(getRootObject(), selectedAxiom);
        int selIndex = getSelectedIndex();
        getFrame().setRootObject(getRootObject());
        setSelectedIndex(selIndex);
    }

    private void handleDecreaseIndentation() {
        OWLAxiom selectedAxiom = getSelectedAxiom();
        if(selectedAxiom == null) {
            return;
        }
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        formattingManager.decreaseIndentation(getRootObject(), selectedAxiom);
        int selIndex = getSelectedIndex();
        getFrame().setRootObject(getRootObject());
        setSelectedIndex(selIndex);
    }




    private OWLAxiom getSelectedAxiom() {
        int selectedIndex = getSelectedIndex();
        if(selectedIndex == -1) {
            return null;
        }
        Object element = getModel().getElementAt(selectedIndex);
        if(!(element instanceof JustificationFrameSectionRow)) {
            return null;
        }
        return ((JustificationFrameSectionRow) element).getAxiom();
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        if (value instanceof JustificationFrameSectionRow) {
            List<MListButton> buttons = Arrays.<MListButton>asList(new ExplainButton(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    invokeExplanationHandler();
                }
            }));
            buttonRunWidth = buttons.size() * (getButtonDimension() + 2) + 20;
            return buttons;
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public void addToPopupMenu(OWLFrameListPopupMenuAction<Explanation<OWLAxiom>> explanationOWLFrameListPopupMenuAction) {
        // NO MENU FOR US
    }

    @Override
    protected Color getItemBackgroundColor(MListItem item) {
        if(item instanceof JustificationFrameSectionRow) {
            JustificationFrameSectionRow row = (JustificationFrameSectionRow) item;
            OWLAxiom axiom = row.getAxiom();
            int rowIndex = row.getFrameSection().getRowIndex(row) + 1;
            if(!isSelectedIndex(rowIndex)) {
                if(axiomSelectionModel.getSelectedAxioms().contains(axiom)) {
                    return Color.YELLOW;
                }
                else {
                    boolean inAll = true;
                    for(Explanation<?> expl : workbenchManager.getJustifications(getRootObject().getEntailment())) {
                        if(!expl.contains(axiom)) {
                            inAll = false;
                            break;
                        }
                    }
                    if(inAll) {
                        return new Color(245, 255 , 235);
                    }

                }
            }
        }
        return super.getItemBackgroundColor(item);
    }

    @Override
    protected List<MListButton> getListItemButtons(MListItem item) {
        return Collections.emptyList();
    }

    @Override
    protected Border createListItemBorder(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.createListItemBorder(list, value, index, isSelected, cellHasFocus);
    }


    @Override
    protected Border createPaddingBorder(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.createPaddingBorder(list, value, index, isSelected, cellHasFocus);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int size = getModel().getSize();
        for(int i = 0; i < size; i++) {
            Object element = getModel().getElementAt(i);
            if(element instanceof JustificationFrameSectionRow) {
                JustificationFrameSectionRow row = (JustificationFrameSectionRow) element;
                Rectangle rect = getCellBounds(i, i);
                if (rect.intersects(g.getClip().getBounds())) {
                    OWLAxiom entailment = getRootObject().getEntailment();
                    if(workbenchManager.getJustificationCount(entailment) > 1) {
                        AttributedString popularityString = getPopularityString(isSelectedIndex(i), row);
                        TextLayout textLayout = new TextLayout(popularityString.getIterator(), g2.getFontRenderContext());
                        float advance = textLayout.getAdvance();
                        float x = rect.x + rect.width - advance - buttonRunWidth;
                        float h = textLayout.getAscent() + textLayout.getDescent();
                        float y = ((rect.height - h) / 2) + rect.y + textLayout.getLeading() + textLayout.getAscent();
                        textLayout.draw(g2, x, y);

                        g2.setColor(Color.LIGHT_GRAY);
                        TextLayout numberLayout = new TextLayout(i + ")", g2.getFont(), g2.getFontRenderContext());
                        float numberX = 20 - numberLayout.getAdvance();
                        numberLayout.draw(g2, numberX, y);
                    }
                }
            }
        }
    }

    private AttributedString getPopularityString(boolean sel, JustificationFrameSectionRow row) {
        int popularity = workbenchManager.getPopularity(row.getAxiom());
        OWLAxiom entailment = row.getRoot().getEntailment();
        int count = workbenchManager.getJustificationCount(entailment);

        StringBuilder sb = new StringBuilder("In ");
        int start = sb.length();
        Color highlightColor = sel ? getSelectionForeground() : Color.DARK_GRAY;
            if(popularity <= 1) {
                sb.append("NO");
                highlightColor = SINGLE_POPULARITY_COLOR;
            }
            else if (popularity != count) {
                sb.append(popularity - 1);
                highlightColor = MULTI_POPULARITY_COLOR;
            }
            else {
                sb.append("ALL");
                highlightColor = ALL_POPULARITY_COLOR;
            }
        int end = sb.length();
        sb.append(" other justifications");
        AttributedString as = new AttributedString(sb.toString());
        as.addAttribute(TextAttribute.FOREGROUND, Color.LIGHT_GRAY, 0, sb.length());
        if (!sel) {
            as.addAttribute(TextAttribute.FOREGROUND, highlightColor, start, end);
        }
        return as;
    }
}
