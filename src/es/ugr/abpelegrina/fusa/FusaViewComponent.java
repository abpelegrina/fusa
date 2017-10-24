package es.ugr.abpelegrina.fusa;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
//import org.protege.editor.owl.OWLIndividualsList;



import es.ugr.abpelegrina.fusa.fuzzy.FuzzyWeight;
import es.ugr.abpelegrina.fusa.ui.tree.SAObjectHierarchyProvider;
import es.ugr.abpelegrina.fusa.ui.tree.SAResultsTree;

public class FusaViewComponent extends AbstractOWLViewComponent implements ActionListener {
	private static final long serialVersionUID = -4515710047558710080L;

	private static final Logger log = Logger
			.getLogger(FusaViewComponent.class);

	private OWLOntology ontology;
	HashMap<OWLIndividual,Double> activation = new HashMap<OWLIndividual,Double>();
	JTextArea resultsBox = new JTextArea();
	
	JButton doSomething = new JButton("Start Spreading Activation");
	OWLSelectionModel selModel;
	OWLIndividual startInd = null;
	SAResultsTree tree = null;
	SAObjectHierarchyProvider hProvider = null;
	

	@Override
	protected void initialiseOWLView() throws Exception {
		setLayout(new BorderLayout());
		
		add(new JLabel("Results:"), BorderLayout.NORTH);
		add(doSomething, BorderLayout.SOUTH);
		doSomething.addActionListener(this);
		
		selModel = this.getOWLWorkspace().getOWLSelectionModel();
		
		log.info("Example View Component initialized");
		
	}

	@Override
	protected void disposeOWLView() {
		if (this.tree != null) tree.dispose();
	}

	public Double getActivation(OWLIndividual x){
		if (activation.containsKey(x))
			return activation.get(x);
		else
			return 0.0;
	}
	
	
	public String getShortName(OWLIndividual i){
		String name = "";
		if (i != null && i.isNamed()){
			OWLNamedIndividual ni = (OWLNamedIndividual) i;
			name = ni.getIRI().getFragment();
		}
		
		return name;
	}
	
	public int saDummy(HashSet<OWLIndividual> start, OWLOntology ontology, double decay, double F){	
		
		Set<OWLIndividual> toSpread = (HashSet<OWLIndividual>)start.clone();
		Set<OWLIndividual> visited = new HashSet<OWLIndividual>();
		
		String fileName = "/es/ugr/abpelegrina/fusa/fuzzy/sa2.fcl";
		FuzzyWeight fw = FuzzyWeight.getFuzzyWeight(fileName);
		int total = 0;
		
		this.hProvider = new SAObjectHierarchyProvider();
		this.hProvider.setOntologies(this.getOWLModelManager().getActiveOntologies());
		
		//init activatin values
		for (OWLIndividual indi:toSpread){
			activation.put(indi, 1.0);
			this.hProvider.addRoot(indi.asOWLNamedIndividual());
		}
		
		// mientras queden individuos por activar
		while(toSpread.size() > 0){
			Set<OWLIndividual> nextBatch = new HashSet<OWLIndividual>();
			
			// recorremos la lista de individuos por activar -- pulsos
			for(OWLIndividual source: toSpread){
				total++;
				
				//System.out.println("Expandiendo el nodo: " + getShortName(source));
				
				Map<OWLObjectPropertyExpression, Set<OWLIndividual>>  res = source.getObjectPropertyValues(ontology);
				
				OWLNamedIndividual sourceNamed = source.asOWLNamedIndividual();
				
				
				Set<OWLAxiom> axs = ontology.getReferencingAxioms(sourceNamed);
				
				
				// Recuperamos todos los individuos relacionados con source mediante una aserci—n de propiedad de objetos
				for(OWLAxiom ax :axs) {
					
					if (ax instanceof OWLObjectPropertyAssertionAxiom){
						OWLObjectPropertyAssertionAxiom assertion = (OWLObjectPropertyAssertionAxiom) ax;
						//System.out.println(assertion);
						if (assertion.getObject().equals(source)){
							
							Set<OWLIndividual> prev;
							if (res.containsKey(assertion.getProperty())){
								prev = new HashSet<OWLIndividual>(res.get(assertion.getProperty()));
								prev.add(assertion.getSubject());
							}
							else
								prev =  Collections.singleton(assertion.getSubject());
							
							res.put(assertion.getProperty(), prev);
							
							//System.out.println("source es objeto en el axioma!!! Agregando sujeto: " + this.getShortName(assertion.getSubject()));
						}
					}
				}
				
				//Calculamos el nuevo valor de activaci—n para cada individuo
				for(OWLObjectPropertyExpression exp: res.keySet()){
					for(OWLIndividual target : res.get(exp)){
						
						this.hProvider.addNode(target.asOWLNamedIndividual(), source.asOWLNamedIndividual());
						
						Double activation_t1 =getActivation(target)+ decay*fw.evalActivation(getActivation(source),1);// + decay*;
						activation.put(target, activation_t1);
						
						if (activation_t1 >= F && !visited.contains(target)){
							nextBatch.add(target);
							visited.add(target);
						}
					}
				}
			}
			//System.out.println("Ronda de nodos terminada\n==========================\n");
			toSpread.clear();
			toSpread = nextBatch;
		}
		return total;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ontology = this.getOWLModelManager().getActiveOntology();
		
		startInd = selModel.getLastSelectedIndividual();
		
		HashSet<OWLIndividual> start = new HashSet<OWLIndividual>();
		start.add(startInd);
		
		reloadActivation();
		int total = saDummy(start, ontology, 0.9, 0.5);
		
		
		if ( this.tree != null ){
			this.remove(tree);
			tree.dispose();
		}
		//if (total > 1){
			tree = new SAResultsTree(this.getOWLEditorKit(), this.hProvider);
			this.add(tree, BorderLayout.CENTER);
		//}
		this.revalidate();
	}

	private void reloadActivation() {
		activation.clear();
	}

	
}
