@Grapes([                                                                                                                                                                                                                                                                     
    @Grab(group='net.sourceforge.owlapi', module='owlapi-api', version='5.1.14'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-apibinding', version='5.1.14'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-impl', version='5.1.14'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-parsers', version='5.1.14'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-distribution', version='5.1.14'),

  @GrabResolver(name='sonatype-nexus-snapshots', root='https://oss.sonatype.org/service/local/repositories/snapshots/content/'),
   // @Grab('org.semanticweb.elk:elk-reasoner:0.5.0-SNAPSHOT'),
   // @Grab('org.semanticweb.elk:elk-owl-implementation:0.5.0-SNAPSHOT'),
    @Grab('au.csiro:elk-owlapi5:0.5.0'),
  
    @GrabConfig(systemClassLoader=true)
])

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.parameters.*
import org.semanticweb.elk.owlapi.*
import org.semanticweb.elk.reasoner.config.*
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.reasoner.*
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.io.*
import org.semanticweb.owlapi.owllink.*
import org.semanticweb.owlapi.util.*
import org.semanticweb.owlapi.search.*
import org.semanticweb.owlapi.manchestersyntax.renderer.*
import org.semanticweb.owlapi.reasoner.structural.*
import org.semanticweb.elk.reasoner.config.*
import org.semanticweb.owlapi.apibinding.*
import org.semanticweb.owlapi.reasoner.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*
import groovyx.gpars.*
import org.codehaus.gpars.*

def manager = OWLManager.createOWLOntologyManager()
def fac = manager.getOWLDataFactory()
def config = new SimpleConfiguration()
def elkFactory = new ElkReasonerFactory() // cute

def doid = manager.loadOntologyFromOntologyDocument(new File("doid.owl"))
def doReasoner = elkFactory.createReasoner(doid, config)

def mesh = manager.loadOntologyFromOntologyDocument(new File("mesh.umls"))
def meshReasoner = elkFactory.createReasoner(mesh, config)

def sentiments = [:]
new File('./individual_sentiments_doid_and_mesh.tsv').splitEachLine('\t') {
  if(it[0] == 'iri') { return; }
  if(it[1] == 'DOID') { 
    it[0] = 'http://purl.obolibrary.org/obo/' + it[0]
  } else {
    it[0] = 'http://purl.bioontology.org/ontology/MESH/' + it[0]
  }

  sentiments[it[0]] = [
    o: it[1],

    VeryPositive: Double.parseDouble(it[2]),
    Positive: Double.parseDouble(it[3]),
    Neutral: Double.parseDouble(it[4]),
    Negative: Double.parseDouble(it[5]),
    VeryNegative: Double.parseDouble(it[6]),

    OVeryPositive: Double.parseDouble(it[2]),
    OPositive: Double.parseDouble(it[3]),
    ONeutral: Double.parseDouble(it[4]),
    ONegative: Double.parseDouble(it[5]),
    OVeryNegative: Double.parseDouble(it[6]),

    count: 1
  ]
}

// Now we add all of the subclasses that we have in the reasoner anyway.
sentiments.each { iri, v ->
	def ce = fac.getOWLClass(IRI.create(iri))

  def reasoner = doReasoner
  if(v.o == 'MESH') { reasoner = meshReasoner }

	def allClasses = reasoner.getSubClasses(ce, false).collect { 
		def scIri = it.getRepresentativeElement().getIRI().toString() 
	}.unique(false)
	allClasses.each { cl ->
		if(sentiments.containsKey(cl) && iri != cl) {
      sentiments[iri].VeryPositive += sentiments[cl].OVeryPositive
      sentiments[iri].Positive += sentiments[cl].OPositive
      sentiments[iri].Neutral += sentiments[cl].ONeutral
      sentiments[iri].Negative += sentiments[cl].ONegative
      sentiments[iri].VeryNegative += sentiments[cl].OVeryNegative
      sentiments[iri].count++
    }	
	}
}

def out = [ ['iri', 'ontology', 'VeryPositive', 'Positive', 'Neutral', 'Negative', 'VeryNegative', 'count'].join('\t') ]

sentiments.each { iri, vals ->
  out << [
    iri,
    vals.o,
    vals.VeryPositive / vals.count,
    vals.Positive / vals.count,
    vals.Neutral / vals.count,
    vals.Negative / vals.count,
    vals.VeryNegative / vals.count,
    vals.count
  ].join('\t')
}

new File('./propagated_do_mesh_sentiment_averages.tsv').text = out.join('\n')
