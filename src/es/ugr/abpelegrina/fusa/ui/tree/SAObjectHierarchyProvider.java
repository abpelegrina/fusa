package es.ugr.abpelegrina.fusa.ui.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProviderListener;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class SAObjectHierarchyProvider implements OWLObjectHierarchyProvider<OWLEntity> {

	private Set<OWLEntity> roots;

    private Map<OWLEntity, HashSet<OWLEntity>> parent2ChildMap;

    private Map<OWLEntity, HashSet<OWLEntity>> child2ParentMap;
    
    
	
	public SAObjectHierarchyProvider() {
		super();
		roots = new HashSet<OWLEntity>();
		parent2ChildMap = new HashMap<OWLEntity, HashSet<OWLEntity>>();
		child2ParentMap = new HashMap<OWLEntity, HashSet<OWLEntity>>();
	}

	@Override
	public void setOntologies(Set<OWLOntology> ontologies) {
		// TODO Auto-generated method stub
		
	}
	
	public void addRoot(OWLEntity indv){
		roots.add(indv);
	}
	
	public void addNode(OWLEntity node, OWLEntity parent){
		
		if (this.parent2ChildMap.containsKey(parent))
			this.parent2ChildMap.get(parent).add(node);
		else {
			HashSet<OWLEntity> children = new HashSet<OWLEntity>();
			children.add(node);
			this.parent2ChildMap.put(parent, children);
			//this.roots.add(parent);
		}
		
		if(this.child2ParentMap.containsKey(node))
			this.child2ParentMap.get(node).add(parent);
		else {
			HashSet<OWLEntity> parents = new HashSet<OWLEntity>();
			parents.add(parent);
			this.child2ParentMap.put(node, parents);
		}
	}

	@Override
	public Set<OWLEntity> getRoots() {
		// TODO Auto-generated method stub
		return roots;
	}

	@Override
	public Set<OWLEntity> getChildren(OWLEntity object) {
		// TODO Auto-generated method stub
		Set<OWLEntity> children =  new  HashSet<OWLEntity>();
		children = parent2ChildMap.get(object);
		if (children == null)
			children = Collections.emptySet();
		return children;
	}

	@Override
	public Set<OWLEntity> getDescendants(OWLEntity object) {
		Set<OWLEntity> descendants = new  HashSet<OWLEntity>();
		
		descendants.addAll(this.getChildren(object));
		
		for(OWLEntity indv : this.getChildren(object)){
			descendants.addAll(this.getDescendants(indv));
		}
		
		return descendants;
	}

	@Override
	public Set<OWLEntity> getParents(OWLEntity object) {
		Set<OWLEntity> parents =  new  HashSet<OWLEntity>();
		parents = child2ParentMap.get(object);
		if (parents == null)
			parents = Collections.emptySet();
		
		return parents;
	}

	@Override
	public Set<OWLEntity> getAncestors(OWLEntity object) {
		Set<OWLEntity> ancestors = new  HashSet<OWLEntity>();
		
		ancestors.addAll(this.getParents(object));
		
		for(OWLEntity indv : this.getParents(object)){
			ancestors.addAll(this.getAncestors(indv));
		}
		
		return ancestors;
	}

	@Override
	public Set<OWLEntity> getEquivalents(OWLEntity object) {
		// TODO Auto-generated method stub
		return Collections.emptySet();
	}

	@Override
	public Set<List<OWLEntity>> getPathsToRoot(OWLEntity object) {
		Set<OWLEntity> parents = this.getParents(object);
		Set<List<OWLEntity>> paths = new HashSet<List<OWLEntity>>();
		
		// es el nodo ra’z?
		if (this.roots.contains(object))
			paths.add(Collections.singletonList(object));
		
		for(OWLEntity p : parents){
			List<OWLEntity> lista = new ArrayList<OWLEntity>();
			lista.add(object);
			
			
			if (this.roots.contains(p)){
				lista.add(p);
				paths.add(lista);
			}
			else {
				// seguimos con l
				//lista.addAll(this.getPathsToRoot(p));
			}
			
		}
		
		return Collections.emptySet();
	}
	
	
	/*private List<OWLNamedIndividual> getPathToRootVia(OWLNamedIndividual start, OWLNamedIndividual parent){
		List<OWLNamedIndividual> lista = new ArrayList<OWLNamedIndividual>();
		
		if (!roots.contains(start)){
			lista.add(parent);
			if (!roots.contains(parent)){
				lista.addAll(this.getPathToRootVia(parent, parent))
			}	
		}
		
		return lista;
	}*/

	@Override
	public boolean containsReference(OWLEntity object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addListener(
			OWLObjectHierarchyProviderListener<OWLEntity> listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(
			OWLObjectHierarchyProviderListener<OWLEntity> listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
