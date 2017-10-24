package es.ugr.abpelegrina.fusa.ui.tree;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owlapi.model.OWLEntity;

public class SAResultsTree extends JPanel{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5380702159922240015L;
	
	
	OWLModelManagerTree<OWLEntity> tree = null;
	OWLObjectHierarchyProvider<OWLEntity> hProvider = null;
	
	public SAResultsTree(OWLEditorKit eKit, OWLObjectHierarchyProvider<OWLEntity> provider) {
		super();
		tree = new OWLModelManagerTree<OWLEntity>(eKit, provider);
		tree.expandRow(0);
		hProvider = provider;
		this.setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane (tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scroll, BorderLayout.CENTER);
	}

	
	public void dispose(){
		tree.dispose();
	}

}
