#!/usr/bin/env groovy
@Grab(group='commons-cli', module='commons-cli', version='1.4')
@Grab(group='org.apache.commons', module='commons-lang3', version='3.4')
@Grab(group='edu.stanford.nlp', module='stanford-corenlp', version='3.7.0')
@Grab(group='edu.stanford.nlp', module='stanford-corenlp', version='3.7.0', classifier='models')
@Grab(group='edu.stanford.nlp', module='stanford-parser', version='3.7.0')
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')

import edu.stanford.nlp.pipeline.*
import edu.stanford.nlp.ling.*
import edu.stanford.nlp.semgraph.*
import edu.stanford.nlp.ie.util.RelationTriple 
import edu.stanford.nlp.util.*
import edu.stanford.nlp.naturalli.*
import edu.stanford.nlp.sentiment.*
import edu.stanford.nlp.neural.rnn.*

def props = new Properties()
props.put("annotators", "tokenize, ssplit, pos, parse, sentiment")
props.put("parse.maxtime", "20000")
props.put("regexner.ignorecase", "true")
props.put("depparse.nthreads", 50)
props.put("ner.nthreads", 50)
props.put("parse.nthreads", 50)
def coreNLP = new StanfordCoreNLP(props)

def fList = []
new File('./metadata/').eachFile { f -> fList << f }
def i = 0
def out = []
fList.each { f ->
  println "${++i}/${fList.size()}"
  def text = f.text
  def aDocument = new edu.stanford.nlp.pipeline.Annotation(text.toLowerCase())
  [ "tokenize", "ssplit", "parse", "sentiment" ].each {
    coreNLP.getExistingAnnotator(it).annotate(aDocument)
  }

  if(text.trim() == '') { 
    println 'empty' 
    out << "${f.getName()}\tNO_DEFINITION"
    return; 
  }

  def sid = 0
  aDocument.get(CoreAnnotations.SentencesAnnotation.class).each { sentence ->
    try {
    def tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    def sm = RNNCoreAnnotations.getPredictions(tree);
    def sentimentType = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
    
    def tags = []

    tags << "OverallSentimentClass:$sentimentType"
    tags << "VeryPositive:${(double)Math.round(sm.get(4) * 100d)}"
    tags << "Positive:${(double)Math.round(sm.get(3) * 100d)}"
    tags << "Neutral:${(double)Math.round(sm.get(2) * 100d)}"
    tags << "Negative:${(double)Math.round(sm.get(1) * 100d)}"
    tags << "VeryNegative:${(double)Math.round(sm.get(0) * 100d)}"

    out << "${f.getName()}\t${sid++}\t${tags.join('\t')}"
    } catch(e) {
      println "err on ${f.getName()}"
    }
  }
}

new File('sentiments.txt').text = out.join('\n')

